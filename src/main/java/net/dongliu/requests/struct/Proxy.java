package net.dongliu.requests.struct;

/**
 * http proxy / socket proxy
 *
 * @author Dong Liu
 */
public class Proxy {
    private Scheme scheme;
    private String host;
    private int port;
    private AuthInfo authInfo;

    public Proxy() {

    }

    public Proxy(Scheme scheme, String host, int port, AuthInfo authInfo) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.authInfo = authInfo;
    }

    public static Proxy httpProxy(String host, int port, String userName, String password) {
        return new Proxy(Scheme.http, host, port, new AuthInfo(userName, password));
    }

    public static Proxy httpsProxy(String host, int port, String userName, String password) {
        return new Proxy(Scheme.https, host, port, new AuthInfo(userName, password));
    }

    public static Proxy socketProxy(String host, int port, String userName, String password) {
        return new Proxy(Scheme.socks, host, port, new AuthInfo(userName, password));
    }

    public static Proxy httpProxy(String host, int port) {
        return new Proxy(Scheme.http, host, port, null);
    }

    public static Proxy httpsProxy(String host, int port) {
        return new Proxy(Scheme.https, host, port, null);
    }

    public static Proxy socketProxy(String host, int port) {
        return new Proxy(Scheme.socks, host, port, null);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(AuthInfo authInfo) {
        this.authInfo = authInfo;
    }

    public String getUserName() {
        return authInfo.getUserName();
    }

    public String getPassword() {
        return authInfo.getPassword();
    }

    public Scheme getScheme() {
        return scheme;
    }

    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }

    public static enum Scheme {
        http, https, socks
    }
}
