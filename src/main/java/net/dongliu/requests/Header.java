package net.dongliu.requests;

/**
 * http header
 *
 * @author Dong Liu dongliu@live.cn
 */
public class Header {
    private String name;
    private String value;

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    public static final String Accept_Encoding = "Accept-Encoding";
    public static final String Accept_Encoding_COMPRESS = "gzip, deflate";

    public static Header of(String name, Object value) {
        Header header = new Header();
        header.setName(name);
        header.setValue(String.valueOf(value));
        return header;
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

    public String valueAsString() {
        return String.valueOf(value);
    }

}
