package net.dongliu.requests.struct;

/**
 * http proxy
 *
 * @author Dong Liu
 */
public class Proxy {
    private String scheme;
    private String host;
    private int port;
    private AuthInfo authInfo;

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

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }
}
