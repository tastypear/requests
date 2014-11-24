package net.dongliu.requests.struct;

/**
 * http parameter
 *
 * @author Dong Liu
 */
public class Parameter extends Pair<String, String> {

    public Parameter() {
    }

    public Parameter(String name, Object value) {
        super(name, String.valueOf(value));
    }

}
