package net.dongliu.commons.requests;

/**
 * http basic auth info
 *
 * @author Dong Liu dongliu@live.cn
 */
public class BasicAuth {
    private String userName;
    private String password;

    public BasicAuth(String userName, String password) {
        this.userName = userName;
        this.password = password;
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
