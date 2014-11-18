package net.dongliu.requests;

/**
 * http basic auth info
 *
 * @author Dong Liu dongliu@live.cn
 */
public class BasicAuth {
    private String userName;
    private String password;

    public static BasicAuth of(String userName, String password) {
        BasicAuth basicAuth = new BasicAuth();
        basicAuth.userName = userName;
        basicAuth.password = password;
        return basicAuth;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
