package net.dongliu.requests;

import net.dongliu.requests.converter.ResponseConverter;
import net.dongliu.requests.exception.InvalidUrlException;
import net.dongliu.requests.exception.RuntimeIOException;
import net.dongliu.requests.struct.*;

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
    private Parameters parameters = new Parameters();
    private Headers headers = new Headers();
    // send cookie values
    private Cookies cookies = new Cookies();
    // http multi part post request files
    private List<MultiPart> files = new ArrayList<>();
    // http request body from inputStream
    private InputStream in;

    private int connectTimeout = 10_000;
    private int socketTimeout = 10_000;

    private ResponseConverter<T> transformer;
    private boolean gzip = true;
    // if check ssl certificate
    private boolean verify = true;
    private boolean allowRedirects = true;
    //private CredentialsProvider provider;
    private AuthInfo authInfo;
    private String[] cert;
    private Proxy proxy;

    RequestBuilder() {
    }

    public RequestBuilder<T> url(String url) {
        this.url = url;
        return this;
    }

    Request build() {
        return new Request(method, url, parameters, headers, in, files, body,
                authInfo, gzip, verify, cookies, allowRedirects,
                connectTimeout, socketTimeout, proxy);
    }

    /**
     * get url, and return content
     */
    public Response<T> get() throws RuntimeIOException {
        Request request = method(Method.GET).build();
        return Requests.execute(request, this.transformer);
    }

    /**
     * get url, and return content
     */
    public Response<T> head() throws RuntimeIOException {
        Request request = method(Method.HEAD).build();
        return Requests.execute(request, this.transformer);
    }

    /**
     * get url, and return content
     */
    public Response<T> post() throws RuntimeIOException {
        Request request = method(Method.POST).build();
        return Requests.execute(request, this.transformer);
    }

    /**
     * put method
     */
    public Response<T> put() throws RuntimeIOException {
        Request request = method(Method.PUT).build();
        return Requests.execute(request, this.transformer);
    }

    /**
     * delete method
     */
    public Response<T> delete() throws RuntimeIOException {
        Request request = method(Method.DELETE).build();
        return Requests.execute(request, this.transformer);
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
        this.parameters.add(Parameter.of(key, value));
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
        this.socketTimeout = this.connectTimeout = timeout;
        return this;
    }

    /**
     * set socket connect and read timeout in milliseconds. default is 10_000
     */
    public RequestBuilder<T> timeout(int connectTimeout, int socketTimeout) {
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
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
    public RequestBuilder<T> proxy(String proxyUrl) throws InvalidUrlException {
        if (proxyUrl == null) {
            return null;
        }
        URI uri;
        try {
            uri = new URI(proxyUrl);
        } catch (URISyntaxException e) {
            throw InvalidUrlException.of(e);
        }
        String userInfo = uri.getUserInfo();
        Proxy proxy = new Proxy();
        proxy.setHost(uri.getHost());
        proxy.setPort(uri.getPort());
        proxy.setScheme(uri.getScheme());
        if (userInfo != null) {
            String[] items = userInfo.split(":");
            String userName = items[0];
            String password = items[1];
            AuthInfo authInfo = new AuthInfo(userName, password);
            proxy.setAuthInfo(authInfo);
        }
        this.proxy = proxy;

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
        authInfo = new AuthInfo(userName, password);
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
        for (Cookie cookie : cookies) {
            this.cookies.add(cookie);
        }
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
     * add multi part file, send multipart requests
     */
    public RequestBuilder<T> files(List<MultiPart> files) {
        this.files.addAll(files);
        return this;
    }

    /**
     * add multi part file, send multipart requests
     *
     * @param files
     * @return
     */
    public RequestBuilder<T> files(MultiPart... files) {
        Collections.addAll(this.files, files);
        return this;
    }
}
