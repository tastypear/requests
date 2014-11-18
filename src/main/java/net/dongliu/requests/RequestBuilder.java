package net.dongliu.requests;

import net.dongliu.requests.code.ResponseConverter;
import net.dongliu.requests.exception.InvalidUrlException;
import net.dongliu.requests.exception.RuntimeIOException;
import org.apache.commons.io.Charsets;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
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
    private RequestConfig.Builder configBuilder = RequestConfig.custom().setConnectTimeout(10_000)
            .setSocketTimeout(10_000);
    private CredentialsProvider provider;
    private boolean gzip = true;
    // if check ssl certificate
    private boolean verify = true;
    // send cookie values
    private List<Cookie> cookies = new ArrayList<>();
    private boolean allowRedirects = true;
    private String[] cert;
    // http request body from inputStream
    private InputStream in;
    // http multi part post request files
    private List<MultiPart> files;

    RequestBuilder() {
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
                request = buildHttpOptions();
                break;
            case TRACE:
            case CONNECT:
            default:
                throw new UnsupportedOperationException("Unsupported method:" + method);
        }

        Request req = new Request(request, provider, headers, gzip, verify, configBuilder.build(),
                cookies, allowRedirects);
        return new Requests<>(req, transformer);
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
        } else if (in != null) {
            httpPut.setEntity(new InputStreamEntity(in));
        }
        return httpPut;
    }


    private HttpPost buildHttpPost() {
        int bodyCount = 0;
        if (files != null) bodyCount++;
        if (body != null) bodyCount++;
        if (in != null) bodyCount++;
        if (bodyCount > 1) {
            //can not set both
            throw new RuntimeException("body and in cannot both be set");
        }

        if (files != null) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            for (Parameter parameter : params) {
                entityBuilder.addTextBody(parameter.getName(), String.valueOf(parameter.getValue()));
            }
            for (MultiPart f : files) {
                entityBuilder.addBinaryBody(f.getFileName(), f.getFile(), ContentType.create(f.getMeta()),
                        f.getFileName());
            }
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entityBuilder.build());
            return httpPost;
        } else if (body != null) {
            URI uri = buildFullUrl();
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new ByteArrayEntity(body));
            return httpPost;
        } else if (in != null) {
            URI uri = buildFullUrl();
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new InputStreamEntity(in));
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

    private HttpRequestBase buildHttpOptions() {
        URI uri = buildFullUrl();
        return new HttpOptions(uri);
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
    public Response<T> get() throws RuntimeIOException {
        return method(Method.GET).build().execute();
    }


    /**
     * get url, and return content
     */
    public Response<T> head() throws RuntimeIOException {
        return method(Method.HEAD).build().execute();
    }

    /**
     * get url, and return content
     */
    public Response<T> post() throws RuntimeIOException {
        return method(Method.POST).build().execute();
    }

    /**
     * put method
     */
    public Response<T> put() throws RuntimeIOException {
        return method(Method.PUT).build().execute();
    }

    /**
     * delete method
     */
    public Response<T> delete() throws RuntimeIOException {
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
    public RequestBuilder<T> params(Parameter... params) {
        for (Parameter param : params) {
            this.param(param.getName(), param.getValue());
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
     * set http data data for Post/Put requests
     *
     * @param data the data to post
     */
    public RequestBuilder<T> data(byte[] data) {
        this.body = data;
        return this;
    }

    /**
     * set http data from inputStream for Post/Put requests
     */
    public RequestBuilder<T> data(InputStream in) {
        this.in = in;
        return this;
    }

    /**
     * set http data with text
     */
    public RequestBuilder<T> data(String body, Charset charset) {
        return data(body.getBytes(charset));
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
    RequestBuilder<T> transformer(ResponseConverter<T> transformer) {
        this.transformer = transformer;
        return this;
    }

    /**
     * set socket connect and read timeout in milliseconds. default is 10_000
     */
    public RequestBuilder<T> timeout(int timeout) {
        configBuilder.setConnectTimeout(timeout).setSocketTimeout(timeout);
        return this;
    }

    /**
     * set socket connect and read timeout in milliseconds. default is 10_000
     */
    public RequestBuilder<T> timeout(int connectTimeout, int socketTimeout) {
        configBuilder.setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout);
        return this;
    }

    /**
     * set http proxy, will ignore null parameter. examples:
     * <pre>
     *     http://127.0.0.1:7890/
     *     https://127.0.0.1:7890/
     *     http://username:password@127.0.0.1:7890/
     * </pre>
     * TODO: socket proxy
     */
    public RequestBuilder<T> proxy(String proxy) throws InvalidUrlException {
        if (proxy == null) {
            return null;
        }
        URI uri;
        try {
            uri = new URI(proxy);
        } catch (URISyntaxException e) {
            throw InvalidUrlException.of(e);
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
     * if send gzip requests. default true
     */
    public RequestBuilder<T> gzip(boolean gzip) {
        this.gzip = gzip;
        return this;
    }

    /**
     * set false to disable ssl check for https requests
     */
    public RequestBuilder<T> verify(boolean verify) {
        this.verify = verify;
        return this;
    }

    /**
     * set http basic auth info
     */
    public RequestBuilder<T> auth(String userName, String password) {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);
        provider.setCredentials(AuthScope.ANY, credentials);
        this.provider = provider;
        return this;
    }

    /**
     * add cookies
     */
    public RequestBuilder<T> cookies(Map<String, String> cookies) {
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            this.cookies.add(Cookie.of(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    /**
     * add cookies
     */
    public RequestBuilder<T> cookies(Cookie... cookies) {
        Collections.addAll(this.cookies, cookies);
        return this;
    }

    /**
     * add cookie
     */
    public RequestBuilder<T> cookie(String name, String value) {
        this.cookies.add(Cookie.of(name, value));
        return this;
    }

    /**
     * if follow redirect
     */
    public RequestBuilder<T> allowRedirects(boolean allowRedirects) {
        this.allowRedirects = allowRedirects;
        return this;
    }

    /**
     * set cert path
     * TODO: custom cert
     */
    public RequestBuilder<T> cert(String... cert) {
        throw new UnsupportedOperationException();
//        this.cert = cert;
//        return this;
    }

    /**
     * send multi part requests
     */
    public RequestBuilder<T> files(List<MultiPart> files) {
        this.files = files;
        return this;
    }
}
