package net.dongliu.requests.struct;

/**
 * @author Dong Liu dongliu@wandoujia.com
 */
public class Pair<K, V> {
    private K name;
    private V value;

    public Pair() {
    }

    public Pair(K name, V value) {
        this.name = name;
        this.value = value;
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
