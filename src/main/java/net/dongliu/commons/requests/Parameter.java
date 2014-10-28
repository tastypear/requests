package net.dongliu.commons.requests;

/**
 * http parameter
 *
 * @author Dong Liu
 */
class Parameter {
    private String name;
    private Object value;

    public static Parameter of(String name, Object value) {
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
