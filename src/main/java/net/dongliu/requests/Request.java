package net.dongliu.requests;

import net.dongliu.requests.struct.*;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

/**
 * Http request
 *
 * @author Dong Liu dongliu@live.cn
 */
public class Request {

    private final Method method;
    private final URI url;
    private final String userAgent;
    private final Headers headers;
    private final Cookies cookies;
    private final Parameters parameters;
    private final byte[] body;
    private final Parameters paramBody;
    private final InputStream in;
    private final List<MultiPart> multiParts;


    private final AuthInfo authInfo;
    // if enable gzip response
    private final boolean gzip;
    // if verify certificate of https site
    private final boolean verify;
    private final boolean allowRedirects;

    private final int connectTimeout;
    private final int socketTimeout;

    private final Proxy proxy;

    Request(Method method, URI url, Parameters parameters, String userAgent, Headers headers,
            InputStream in, List<MultiPart> multiParts, byte[] body, Parameters paramBody,
            AuthInfo authInfo, boolean gzip, boolean verify, Cookies cookies,
            boolean allowRedirects, int connectTimeout, int socketTimeout, Proxy proxy) {
        this.method = method;
        this.url = url;
        this.parameters = parameters;
        this.userAgent = userAgent;
        this.multiParts = multiParts;
        this.body = body;
        this.paramBody = paramBody;
        this.in = in;
        this.headers = headers;
        this.authInfo = authInfo;
        this.gzip = gzip;
        this.verify = verify;
        this.cookies = cookies;
        this.allowRedirects = allowRedirects;
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
        this.proxy = proxy;
    }

    public Method getMethod() {
        return method;
    }

    public URI getUrl() {
        return url;
    }

    public byte[] getBody() {
        return body;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Cookies getCookies() {
        return cookies;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public InputStream getIn() {
        return in;
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public boolean isGzip() {
        return gzip;
    }

    public boolean isVerify() {
        return verify;
    }

    public boolean isAllowRedirects() {
        return allowRedirects;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public List<MultiPart> getMultiParts() {
        return multiParts;
    }

    public Parameters getParamBody() {
        return paramBody;
    }
}
