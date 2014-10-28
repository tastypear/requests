package net.dongliu.commons.requests;

import net.dongliu.commons.lang.Charsets;
import net.dongliu.commons.lang.collection.Pair;
import net.dongliu.commons.requests.code.ResponseConverter;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * http requests builder
 *
 * @param <T> the response content type
 */
public class RequestBuilder<T> {
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

    protected RequestBuilder() {
    }

    public Requests<T> build() {
        HttpRequestBase request;
        switch (method) {
            case POST:
                request = buildHttpPost();
                break;
            case GET:
                request = buildHttpGet();
                break;
            case HEAD:
                request = buildHttpHead();
                break;
            case PUT:
                request = buildHttpPut();
                break;
            case DELETE:
                request = buildHttpDelete();
                break;
            case OPTIONS:
            case TRACE:
            case CONNECT:
            default:
                throw new UnsupportedOperationException("Unsupported method:" + method);
        }

        for (Header header : headers) {
            request.addHeader(header.getName(), header.valueAsString());
        }

        return new Requests<>(request, configBuilder.build(), transformer, provider, gzip,
                checkSsl);
    }


    private HttpRequestBase buildHttpPut() {
        URIBuilder urlBuilder;
        try {
            urlBuilder = new URIBuilder(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        for (Parameter param : this.params) {
            urlBuilder.addParameter(param.getName(), param.valueAsString());
        }
        URI uri;
        try {
            uri = urlBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpPut httpPut = new HttpPut(uri);
        if (body != null) {
            httpPut.setEntity(new ByteArrayEntity(body));
        }
        return httpPut;
    }


    private HttpPost buildHttpPost() {
        if (body != null) {
            URI uri = buildFullUrl();
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new ByteArrayEntity(body));
            return httpPost;
        } else {
            HttpPost httpPost = new HttpPost(url);
            // use www-form-urlencoded to send params
            List<BasicNameValuePair> paramList = new ArrayList<>(params.size());
            for (Parameter param : this.params) {
                paramList.add(new BasicNameValuePair(param.getName(), param.valueAsString()));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, Charsets.UTF_8);
            header(Header.CONTENT_TYPE, Header.CONTENT_TYPE_FORM);
            httpPost.setEntity(entity);
            return httpPost;
        }
    }

    private HttpRequestBase buildHttpHead() {
        URI uri = buildFullUrl();
        return new HttpHead(uri);
    }

    private HttpRequestBase buildHttpGet() {
        URI uri = buildFullUrl();
        return new HttpGet(uri);
    }

    private HttpRequestBase buildHttpDelete() {
        URI uri = buildFullUrl();
        return new HttpDelete(uri);
    }

    // build full url with parameters
    private URI buildFullUrl() {
        try {
            if (this.params.isEmpty()) {
                return new URI(this.url);
            }
            URIBuilder urlBuilder = new URIBuilder(url);
            for (Parameter param : this.params) {
                urlBuilder.addParameter(param.getName(), param.valueAsString());
            }
            return urlBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public RequestBuilder<T> url(String url) {
        this.url = url;
        return this;
    }

    /**
     * get url, and return content
     */
    public Response<T> get() throws IOException {
        return method(Method.GET).build().execute();
    }


    /**
     * get url, and return content
     */
    public Response<T> head() throws IOException {
        return method(Method.HEAD).build().execute();
    }

    /**
     * get url, and return content
     */
    public Response<T> post() throws IOException {
        return method(Method.POST).build().execute();
    }

    /**
     * put method
     */
    public Response<T> put() throws IOException {
        return method(Method.PUT).build().execute();
    }

    /**
     * delete method
     */
    public Response<T> delete() throws IOException {
        return method(Method.DELETE).build().execute();
    }

    /**
     * set userAgent
     */
    public RequestBuilder<T> userAgent(String userAgent) {
        if (userAgent != null) {
            header("User-Agent", userAgent);
        }
        return this;
    }

    /**
     * add parameters
     */
    public RequestBuilder<T> params(Map<String, ?> params) {
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.param(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * add params
     */
    public RequestBuilder<T> params(Pair<String, ?>... params) {
        for (Pair<String, ?> param : params) {
            this.param(param.getKey(), param.getValue());
        }
        return this;
    }

    /**
     * add one parameter
     */
    public RequestBuilder<T> param(String key, Object value) {
        this.params.add(Parameter.of(key, value));
        return this;
    }

    /**
     * set http body data for Post/Put requests
     *
     * @param body the data to post
     */
    public RequestBuilder<T> body(byte[] body) {
        this.body = body;
        return this;
    }

    /**
     * set http body with string
     */
    public RequestBuilder<T> body(String body, Charset charset) {
        return body(body.getBytes(charset));
    }

    private RequestBuilder<T> method(Method method) {
        this.method = method;
        return this;
    }

    /**
     * add headers
     */
    public RequestBuilder<T> headers(Map<String, ?> params) {
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.header(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * add one header
     */
    public RequestBuilder<T> header(String key, Object value) {
        this.headers.add(Header.of(key, value));
        return this;
    }

    /**
     * set transformer. default is String transformer
     */
    protected RequestBuilder<T> transformer(ResponseConverter<T> transformer) {
        this.transformer = transformer;
        return this;
    }

    /**
     * set socket connect timeout in milliseconds. default is 10_000
     */
    public RequestBuilder<T> connectTimeout(int timeout) {
        configBuilder.setConnectTimeout(timeout);
        return this;
    }

    /**
     * set socket read timeout in milliseconds. default is 10_000
     */
    public RequestBuilder<T> socketTimeout(int timeout) {
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
    public RequestBuilder<T> proxy(String proxy) {
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
    public RequestBuilder<T> enableGzip() {
        this.gzip = true;
        return this;
    }

    /**
     * disable ssl check for https requests
     */
    public RequestBuilder<T> disableSslVerify() {
        this.checkSsl = false;
        return this;
    }
}
