package net.dongliu.requests.code;

/**
 * http parameter
 *
 * @author Dong Liu dongliu@wandoujia.com
 */
public class Parameter {
    private String name;
    private String value;

    public static Parameter of(String name, String value) {
        Parameter parameter = new Parameter();
        parameter.setName(name);
        parameter.setValue(value);
        return parameter;
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
}
