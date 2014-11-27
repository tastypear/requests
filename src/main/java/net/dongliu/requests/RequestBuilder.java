package net.dongliu.requests;

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
    private Parameters parameters;
    private String userAgent = "Requests/1.6.5, Java";
    private Headers headers;
    // send cookie values
    private Cookies cookies;

    private byte[] body;
    // parameter type body(form-encoded)
    private Parameters paramBody;
    // http multi part post request multiParts
    private List<MultiPart> multiParts;
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

    private Session session;

    RequestBuilder() {
    }

    /**
     * get http response for return result with Type T.
     */
    <T> Response<T> client(ResponseProcessor<T> processor) throws RuntimeIOException {
        return new RequestExecutor<>(build(), processor).execute();
    }

    /**
     * set custom handler to handle http response
     */
    public <T> Response<T> handler(ResponseHandler<T> handler) throws RuntimeIOException {
        return client(new ResponseHandlerAdapter<T>(handler));
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
     * only save to file when return status is 200, otherwise return response with null body.
     */
    public Response<File> file(File file) throws RuntimeIOException {
        return client(new FileResponseProcessor(file));
    }

    /**
     * get http response for write response body to file.
     * only save to file when return status is 200, otherwise return response with null body.
     */
    public Response<File> file(String filePath) throws RuntimeIOException {
        return client(new FileResponseProcessor(filePath));
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
        return new Request(method, url, parameters, userAgent, headers, in, multiParts, body,
                paramBody, authInfo, gzip, verify, cookies, allowRedirects,
                connectTimeout, socketTimeout, proxy, session);
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
        ensureParameters();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.parameters.add(new Parameter(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    /**
     * add params
     */
    public RequestBuilder params(Parameter... params) {
        ensureParameters();
        for (Parameter param : params) {
            this.parameters.add(new Parameter(param.getName(), param.getValue()));
        }
        return this;
    }

    /**
     * add one parameter
     */
    public RequestBuilder param(String key, Object value) {
        ensureParameters();
        this.parameters.add(new Parameter(key, value));
        return this;
    }

    private void ensureParameters() {
        if (this.parameters == null) {
            this.parameters = new Parameters();
        }
    }

    /**
     * set http data for requests
     */
    public RequestBuilder data(Map<String, ?> data) {
        ensureParamBody();
        for (Map.Entry<String, ?> e : data.entrySet()) {
            paramBody.add(new Parameter(e.getKey(), e.getValue()));
        }
        return this;
    }

    /**
     * set http data for requests
     */
    public RequestBuilder data(Parameter... params) {
        ensureParamBody();
        for (Parameter param : params) {
            paramBody.add(param);
        }
        return this;
    }

    /**
     * add one key-value param to http data for requests
     */
    public RequestBuilder data(String key, Object value) {
        ensureParamBody();
        paramBody.add(new Parameter(key, value));
        return this;
    }

    private void ensureParamBody() {
        if (this.paramBody == null) {
            this.paramBody = new Parameters();
        }
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
        ensureHeaders();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            this.headers.add(new Header(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    /**
     * add headers
     */
    public RequestBuilder headers(Header... headers) {
        ensureHeaders();
        for (Header header : headers) {
            this.headers.add(header);
        }
        return this;
    }

    /**
     * add one header
     */
    public RequestBuilder header(String key, Object value) {
        ensureHeaders();
        this.headers.add(new Header(key, value));
        return this;
    }

    private void ensureHeaders() {
        if (this.headers == null) {
            this.headers = new Headers();
        }
    }

    /**
     * set socket connect and read timeout in milliseconds. default is 10_000.
     * A timeout value of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
     */
    public RequestBuilder timeout(int timeout) {
        this.socketTimeout = this.connectTimeout = timeout;
        return this;
    }

    /**
     * set socket connect and read timeout in milliseconds. default is 10_000.
     * A timeout value of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
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
        ensureCookies();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            this.cookies.add(new Cookie(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    /**
     * add cookies
     */
    public RequestBuilder cookies(Cookie... cookies) {
        ensureCookies();
        for (Cookie cookie : cookies) {
            this.cookies.add(cookie);
        }
        return this;
    }

    /**
     * add cookie
     */
    public RequestBuilder cookie(String name, String value) {
        ensureCookies();
        this.cookies.add(new Cookie(name, value));
        return this;
    }

    private void ensureCookies() {
        if (this.cookies == null) {
            this.cookies = new Cookies();
        }
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
     * add multi part file, will send multiPart requests.
     * this should be used with post method
     */
    public RequestBuilder multiPart(List<MultiPart> files) {
        ensureMultiPart();
        this.multiParts.addAll(files);
        return this;
    }

    /**
     * add multi part file, will send multiPart requests.
     * this should be used with post method
     */
    public RequestBuilder multiPart(MultiPart... files) {
        ensureMultiPart();
        Collections.addAll(this.multiParts, files);
        return this;
    }

    /**
     * add multi part file, will send multiPart requests.
     * this should be used with post method
     */
    public RequestBuilder multiPart(MultiPart file) {
        ensureMultiPart();
        this.multiParts.add(file);
        return this;
    }

    /**
     * add multi part file, will send multiPart requests.
     * this should be used with post method
     *
     * @param name     the http request field name for this file
     * @param filePath the file path
     */
    public RequestBuilder multiPart(String name, String filePath) {
        ensureMultiPart();
        this.multiParts.add(new MultiPart(name, filePath));
        return this;
    }

    private void ensureMultiPart() {
        if (this.multiParts == null) {
            this.multiParts = new ArrayList<>();
        }
    }

    RequestBuilder session(Session session) {
        this.session = session;
        return this;
    }
}
