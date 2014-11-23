package net.dongliu.requests.struct;

/**
 * @author Dong Liu dongliu@wandoujia.com
 */
public class AuthInfo {
    private String userName;
    private String password;

    public AuthInfo() {
    }

    public AuthInfo(String userName, String password) {
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
