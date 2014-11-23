package net.dongliu.requests.struct;

/**
 * http cookie
 *
 * @author Dong Liu dongliu@wandoujia.com
 */
public class Cookie extends Pair<String, String> {

    private String domain;
    private String path;
    private long expiry;

    public static Cookie of(String name, String value) {
        Cookie cookie = new Cookie();
        cookie.setName(name);
        cookie.setValue(value);
        return cookie;
    }

    public static Cookie of(String value) {
        Cookie cookie = new Cookie();
        cookie.setValue(value);
        return cookie;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }
}
