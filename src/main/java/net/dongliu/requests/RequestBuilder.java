package net.dongliu.requests;

import net.dongliu.requests.converter.FileResponseProcessor;
import net.dongliu.requests.converter.ResponseProcessor;
import net.dongliu.requests.converter.StringResponseProcessor;
import net.dongliu.requests.exception.InvalidUrlException;
import net.dongliu.requests.exception.RuntimeIOException;
import net.dongliu.requests.struct.*;

import java.io.File;
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
 */
public class RequestBuilder {
    private Method method;
    private URI url;
    private byte[] body;
    private Parameters parameters = new Parameters();
    private String userAgent = "Requests/1.6.1, Java";
    private Headers headers = new Headers();
    // send cookie values
    private Cookies cookies = new Cookies();
    // http multi part post request files
    private List<MultiPart> files = new ArrayList<>();
    // http request body from inputStream
    private InputStream in;

    private int connectTimeout = 10_000;
    private int socketTimeout = 10_000;

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

    /**
     * get http response for return result with Type T.
     */
    public <T> Response<T> client(ResponseProcessor<T> transformer) throws RuntimeIOException {
        return new RequestExecutor(build()).executeWith(transformer);
    }

    /**
     * get http response for return text result, use default encoding.
     */
    public Response<String> text() throws RuntimeIOException {
        return client(ResponseProcessor.string);
    }

    /**
     * get http response for return text result.
     *
     * @param charset the encoding to use if not found in response header
     */
    public Response<String> text(Charset charset) throws RuntimeIOException {
        return client(new StringResponseProcessor(charset));
    }

    /**
     * get http response for return text result.
     *
     * @param charset the encoding to use if not found in response header
     */
    public Response<String> text(String charset) throws RuntimeIOException {
        return text(Charset.forName(charset));
    }

    /**
     * get http response for return byte array result.
     */
    public Response<byte[]> bytes() throws RuntimeIOException {
        return client(ResponseProcessor.bytes);
    }

    /**
     * get http response for write response body to file.
     * only save to file when return status is 200, otherwise return response with null body
     */
    public Response<File> file(File file) throws RuntimeIOException {
        return client(new FileResponseProcessor(file));
    }

    RequestBuilder url(String url) throws InvalidUrlException {
        try {
            this.url = new URI(url);
        } catch (URISyntaxException e) {
            throw InvalidUrlException.of(e);
        }
        return this;
    }

    Request build() {
        return new Request(method, url, parameters, userAgent, headers, in, files, body,
                authInfo, gzip, verify, cookies, allowRedirects,
                connectTimeout, socketTimeout, proxy);
    }

    /**
     * set userAgent
     */
    public RequestBuilder userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * add parameters
     */
    public RequestBuilder params(Map<String, ?> params) {
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.param(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * add params
     */
    public RequestBuilder params(Parameter... params) {
        for (Parameter param : params) {
            this.param(param.getName(), param.getValue());
        }
        return this;
    }

    /**
     * add one parameter
     */
    public RequestBuilder param(String key, Object value) {
        this.parameters.add(new Parameter(key, value));
        return this;
    }

    /**
     * set http data data for Post/Put requests
     *
     * @param data the data to post
     */
    public RequestBuilder data(byte[] data) {
        this.body = data;
        return this;
    }

    /**
     * set http data from inputStream for Post/Put requests
     */
    public RequestBuilder data(InputStream in) {
        this.in = in;
        return this;
    }

    /**
     * set http data with text
     */
    public RequestBuilder data(String body, Charset charset) {
        return data(body.getBytes(charset));
    }

    RequestBuilder method(Method method) {
        this.method = method;
        return this;
    }

    /**
     * add headers
     */
    public RequestBuilder headers(Map<String, ?> params) {
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.header(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * add one header
     */
    public RequestBuilder header(String key, Object value) {
        this.headers.add(new Header(key, value));
        return this;
    }

    /**
     * set socket connect and read timeout in milliseconds. default is 10_000
     */
    public RequestBuilder timeout(int timeout) {
        this.socketTimeout = this.connectTimeout = timeout;
        return this;
    }

    /**
     * set socket connect and read timeout in milliseconds. default is 10_000
     */
    public RequestBuilder timeout(int connectTimeout, int socketTimeout) {
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
    public RequestBuilder proxy(String proxyUrl) throws InvalidUrlException {
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
    public RequestBuilder gzip(boolean gzip) {
        this.gzip = gzip;
        return this;
    }

    /**
     * set false to disable ssl check for https requests
     */
    public RequestBuilder verify(boolean verify) {
        this.verify = verify;
        return this;
    }

    /**
     * set http basic auth info
     */
    public RequestBuilder auth(String userName, String password) {
        authInfo = new AuthInfo(userName, password);
        return this;
    }

    /**
     * add cookies
     */
    public RequestBuilder cookies(Map<String, String> cookies) {
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            this.cookies.add(new Cookie(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    /**
     * add cookies
     */
    public RequestBuilder cookies(Cookie... cookies) {
        for (Cookie cookie : cookies) {
            this.cookies.add(cookie);
        }
        return this;
    }

    /**
     * add cookie
     */
    public RequestBuilder cookie(String name, String value) {
        this.cookies.add(new Cookie(name, value));
        return this;
    }

    /**
     * if follow redirect
     */
    public RequestBuilder allowRedirects(boolean allowRedirects) {
        this.allowRedirects = allowRedirects;
        return this;
    }

    /**
     * set cert path
     * TODO: custom cert
     */
    public RequestBuilder cert(String... cert) {
        throw new UnsupportedOperationException();
//        this.cert = cert;
//        return this;
    }

    /**
     * add multi part file, will send multipart requests
     */
    public RequestBuilder files(List<MultiPart> files) {
        this.files.addAll(files);
        return this;
    }

    /**
     * add multi part file, will send multipart requests
     */
    public RequestBuilder files(MultiPart... files) {
        Collections.addAll(this.files, files);
        return this;
    }

    /**
     * add multi part file, will send multipart requests
     */
    public RequestBuilder file(MultiPart file) {
        this.files.add(file);
        return this;
    }

    /**
     * add multi part file, will send multipart requests
     *
     * @param name     the http request field name for this file
     * @param filePath the file path
     * @return
     */
    public RequestBuilder file(String name, String filePath) {
        this.files.add(MultiPart.of(name, filePath));
        return this;
    }
}
