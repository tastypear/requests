package net.dongliu.requests.struct;

/**
 * @author Dong Liu dongliu@wandoujia.com
 */
public class Pair<K, V> {
    private K name;
    private V value;

    public static <K, V> Pair<K, V> of(K name, V value) {
        Pair<K, V> pair = new Pair<>();
        pair.setName(name);
        pair.setValue(value);
        return pair;
    }

    public K getName() {
        return name;
    }

    public void setName(K name) {
        this.name = name;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
