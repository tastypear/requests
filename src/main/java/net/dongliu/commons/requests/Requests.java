package net.dongliu.commons.requests;

import net.dongliu.commons.lang.Charsets;
import net.dongliu.commons.lang.collection.Pair;
import net.dongliu.commons.lang.exception.RIOException;
import net.dongliu.commons.requests.code.ResponseConverter;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * construct and execute http requests
 *
 * @param <T> the response Type
 * @author Dong Liu
 */
public class Requests<T> {
    private final HttpRequestBase request;
    private final ResponseConverter<T> transformer;
    private final RequestConfig config;
    private final CredentialsProvider provider;
    private final boolean gzip;
    private final boolean checkSsl;

    private Requests(HttpRequestBase request, RequestConfig config, ResponseConverter<T> transformer,
                     CredentialsProvider provider, boolean gzip, boolean checkSsl) {
        this.request = request;
        this.config = config;
        this.transformer = transformer;
        this.provider = provider;
        this.gzip = gzip;
        this.checkSsl = checkSsl;
    }

    /**
     * execute http requests, and get result
     *
     * @return
     */
    public Response<T> execute() {
        request.setConfig(config);
        if (gzip) {
            request.addHeader(Header.Accept_Encoding, Header.Accept_Encoding_COMPRESS);
        }

        HttpClientBuilder clientBuilder = HttpClients.custom();
        if (provider != null) {
            clientBuilder.setDefaultCredentialsProvider(provider);
        }
        if (!checkSsl) {
            SSLConnectionSocketFactory sslsf;
            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                sslsf = new SSLConnectionSocketFactory(builder.build());
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
            clientBuilder.setSSLSocketFactory(sslsf);
        }

        try (CloseableHttpClient client = clientBuilder.build()) {
            try (CloseableHttpResponse httpResponse = client.execute(request)) {
                Response<T> response = new Response<>();
                response.setCode(httpResponse.getStatusLine().getStatusCode());
                org.apache.http.Header[] respHeaders = httpResponse.getAllHeaders();
                List<Pair<String, String>> headers = new ArrayList<>(respHeaders.length);
                for (org.apache.http.Header header : respHeaders) {
                    headers.add(Pair.of(header.getName(), header.getValue()));
                }
                response.setHeaders(headers);
                T result = transformer.convert(httpResponse.getEntity());
                response.setBody(result);
                return response;
            }
        } catch (IOException e) {
            throw new RIOException(e);
        }
    }

    /**
     * get one requests client for return result with Type T.
     */
    public static <T> Builder<T> client(ResponseConverter<T> transformer) {
        return new Builder<T>().transformer(transformer);
    }

    /**
     * get one requests client for return string result.
     */
    public static Builder<String> stringClient() {
        return client(ResponseConverter.string);
    }

    /**
     * get one requests client for return byte array result.
     */
    public static Builder<byte[]> bytesClient() {
        return client(ResponseConverter.bytes);
    }

    public static class Builder<T> {
        private Method method;
        private String url;
        private byte[] body;
        private List<Parameter> params = new ArrayList<>();
        private List<Header> headers = new ArrayList<>();
        private ResponseConverter<T> transformer;
        private RequestConfig.Builder configBuilder = RequestConfig.custom()
                .setConnectTimeout(10_000).setSocketTimeout(10_000);
        private CredentialsProvider provider;
        private boolean gzip;
        private boolean checkSsl = true;

        private Builder() {
        }

        public Requests<T> build() {
            HttpRequestBase request;
            switch (method) {
                case POST:
                    request = getHttpPost();
                    break;
                case GET:
                    request = getHttpGet();
                    break;
                case HEAD:
                case PUT:
                case DELETE:
                default:
                    //TODO: put/delete
                    throw new UnsupportedOperationException();
            }

            for (Header header : headers) {
                request.addHeader(header.getName(), header.valueAsString());
            }

            return new Requests<>(request, configBuilder.build(), transformer, provider, gzip,
                    checkSsl);
        }

        private HttpPost getHttpPost() {
            if (!params.isEmpty() && body != null) {
                throw new IllegalArgumentException("Post body and params cannot set both");
            }

            HttpPost httpPost = new HttpPost(url);
            if (!params.isEmpty()) {
                List<BasicNameValuePair> paramList = new ArrayList<>(params.size());
                for (Parameter param : this.params) {
                    paramList.add(new BasicNameValuePair(param.getName(), param.valueAsString()));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, Charsets.UTF_8);
                header(Header.CONTENT_TYPE, Header.CONTENT_TYPE_FORM);
                httpPost.setEntity(entity);
            } else if (body != null) {
                httpPost.setEntity(new ByteArrayEntity(body));
            }
            return httpPost;
        }

        private HttpRequestBase getHttpGet() {
            HttpRequestBase request;
            URI uri;
            try {
                uri = new URI(url);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            URIBuilder urlBuilder = new URIBuilder(uri);
            for (Parameter param : this.params) {
                urlBuilder.addParameter(param.getName(), param.valueAsString());
            }
            try {
                uri = urlBuilder.build();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            request = new HttpGet(uri);
            return request;
        }

        public Builder<T> url(String url) {
            this.url = url;
            return this;
        }

        /**
         * get url, and return content
         *
         * @throws net.dongliu.commons.lang.exception.RIOException
         */
        public Response<T> get(String url) {
            return method(Method.GET).url(url).build().execute();
        }

        /**
         * get url, and return content
         *
         * @throws net.dongliu.commons.lang.exception.RIOException
         */
        public Response<T> get() {
            return method(Method.GET).build().execute();
        }

        /**
         * post, and return content
         *
         * @throws net.dongliu.commons.lang.exception.RIOException
         */
        public Response<T> post(String url) {
            return method(Method.POST).url(url).build().execute();
        }

        /**
         * get url, and return content
         *
         * @throws net.dongliu.commons.lang.exception.RIOException
         */
        public Response<T> post() {
            return method(Method.POST).build().execute();
        }

        /**
         * put method
         *
         * @throws net.dongliu.commons.lang.exception.RIOException
         */
        public Response<T> put(String url) {
            return method(Method.PUT).url(url).build().execute();
        }

        /**
         * put method
         *
         * @throws net.dongliu.commons.lang.exception.RIOException
         */
        public Response<T> put() {
            return method(Method.PUT).build().execute();
        }

        /**
         * delete method
         *
         * @throws net.dongliu.commons.lang.exception.RIOException
         */
        public Response<T> delete(String url) {
            return method(Method.DELETE).url(url).build().execute();
        }

        /**
         * delete method
         *
         * @throws net.dongliu.commons.lang.exception.RIOException
         */
        public Response<T> delete() {
            return method(Method.DELETE).build().execute();
        }

        /**
         * set userAgent
         */
        public Builder<T> userAgent(String userAgent) {
            if (userAgent != null) {
                header("User-Agent", userAgent);
            }
            return this;
        }

        /**
         * add parameters
         */
        public Builder<T> params(Map<String, ?> params) {
            for (Map.Entry<String, ?> entry : params.entrySet()) {
                this.param(entry.getKey(), entry.getValue());
            }
            return this;
        }

        /**
         * add one parameter
         */
        public Builder<T> param(String key, Object value) {
            this.params.add(Parameter.of(key, value));
            return this;
        }

        /**
         * set http body data for Post/Put requests
         *
         * @param body the data to post
         */
        public Builder<T> body(byte[] body) {
            this.body = body;
            return this;
        }

        /**
         * set http body with string
         */
        public Builder<T> body(String body, Charset charset) {
            return body(body.getBytes(charset));
        }

        private Builder<T> method(Method method) {
            this.method = method;
            return this;
        }

        /**
         * add headers
         */
        public Builder<T> headers(Map<String, ?> params) {
            for (Map.Entry<String, ?> entry : params.entrySet()) {
                this.header(entry.getKey(), entry.getValue());
            }
            return this;
        }

        /**
         * add one header
         */
        public Builder<T> header(String key, Object value) {
            this.headers.add(Header.of(key, value));
            return this;
        }

        /**
         * set transformer. default is String transformer
         */
        private Builder<T> transformer(ResponseConverter<T> transformer) {
            this.transformer = transformer;
            return this;
        }

        /**
         * set socket connect timeout in milliseconds. default is 10_000
         */
        public Builder<T> connectTimeout(int timeout) {
            configBuilder.setConnectTimeout(timeout);
            return this;
        }

        /**
         * set socket read timeout in milliseconds. default is 10_000
         */
        public Builder<T> socketTimeout(int timeout) {
            configBuilder.setSocketTimeout(timeout);
            return this;
        }

        /**
         * set http proxy, will ignore null parameter. examples:
         * <pre>
         *     http://127.0.0.1:7890/
         *     https://127.0.0.1:7890/
         *     http://username:password@127.0.0.1:7890/
         * </pre>
         */
        public Builder<T> proxy(String proxy) {
            if (proxy == null) {
                return null;
            }
            URI uri;
            try {
                uri = new URI(proxy);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            String userInfo = uri.getUserInfo();
            if (userInfo != null) {
                String[] items = userInfo.split(":");
                String userName = items[0];
                String password = items[1];
                CredentialsProvider provider = new BasicCredentialsProvider();
                provider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
                        new UsernamePasswordCredentials(userName, password));
                this.provider = provider;
            }
            HttpHost httpHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            configBuilder.setProxy(httpHost);
            return this;
        }

        /**
         * send gzip requests. default false
         */
        public Builder<T> enableGzip() {
            this.gzip = true;
            return this;
        }

        /**
         * disable ssl check for https requests
         */
        public Builder<T> disableSslVerify() {
            this.checkSsl = false;
            return this;
        }
    }
}