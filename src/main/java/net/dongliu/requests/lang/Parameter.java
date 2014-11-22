package net.dongliu.requests.lang;

/**
 * http parameter
 *
 * @author Dong Liu
 */
public class Parameter extends Pair<String, String> {

    public static Parameter of(String name, Object value) {
        Parameter parameter = new Parameter();
        parameter.setName(name);
        parameter.setValue(String.valueOf(value));
        return parameter;
    }

}
