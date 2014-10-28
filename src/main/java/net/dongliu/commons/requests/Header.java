package net.dongliu.commons.requests;

/**
 * one http header
 *
 * @author Dong Liu
 */
class Header {
    private String name;
    private Object value;

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    public static final String Accept_Encoding = "Accept-Encoding";
    public static final String Accept_Encoding_COMPRESS = "gzip, deflate";

    public static Header of(String name, Object value) {
        Header header = new Header();
        header.setName(name);
        header.setValue(value);
        return header;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String valueAsString() {
        return String.valueOf(value);
    }

}
