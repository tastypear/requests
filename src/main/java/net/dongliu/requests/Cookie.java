package net.dongliu.requests;

import net.dongliu.requests.exception.RuntimeIOException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * http cookie
 *
 * @author Dong Liu dongliu@wandoujia.com
 */
public class Cookie {

    private String name;
    private String value;
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

    public String string() {
        try {
            if (name != null) {
                return URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
            } else {
                return URLEncoder.encode(value, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            //should not happen
            throw new RuntimeIOException(e);
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
