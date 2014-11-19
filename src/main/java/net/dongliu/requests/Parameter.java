package net.dongliu.requests;

/**
 * http parameter
 *
 * @author Dong Liu
 */
class Parameter {
    private String name;
    private String value;

    public static Parameter of(String name, Object value) {
        Parameter parameter = new Parameter();
        parameter.setName(name);
        parameter.setValue(String.valueOf(value));
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
