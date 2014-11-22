package net.dongliu.requests.lang;

import java.util.*;

/**
 * map contains a Pair<K, V> list. same key can have multi values
 *
 * @author Dong Liu dongliu@wandoujia.com
 */
public class MultiMap<K, V, T extends Pair<K, V>> implements Iterable<T> {

    private List<T> pairs;

    /**
     * create empty multi map
     */
    public MultiMap() {
        pairs = new ArrayList<>();
    }

    /**
     * create multi map with pairs
     */
    @SafeVarargs
    public MultiMap(T... pairs) {
        this.pairs = Arrays.asList(pairs);
    }

    /**
     * create multi map with pairs
     */
    public MultiMap(List<T> pairs) {
        this.pairs = pairs;
    }


    /**
     * add one key-value
     */
    public void add(T pair) {
        this.pairs.add(pair);
    }

    /**
     * get first value with key. return null if not found
     */
    public T getFirst(K key) {
        for (T pair : pairs) {
            if (pair.getName().equals(key)) {
                return pair;
            }
        }
        return null;
    }

    /**
     * get values with key. return empty list if not found
     */
    public Collection<T> get(K key) {
        List<T> list = new ArrayList<>();
        for (T pair : pairs) {
            if (pair.getName().equals(key)) {
                list.add(pair);
            }
        }
        return list;
    }

    /**
     * delete items with key
     */
    public void delete(K key) {
        List<T> pairs = new ArrayList<>(this.pairs.size());
        for (T pair : this.pairs) {
            if (!pair.getName().equals(key)) {
                pairs.add(pair);
            }
        }
        this.pairs = pairs;
    }

    /**
     * return all data
     */
    public Collection<T> items() {
        return this.pairs;
    }

    public Iterator<T> iterator() {
        return pairs.iterator();
    }

    public int size() {
        return pairs.size();
    }

    public boolean isEmpty() {
        return pairs.isEmpty();
    }
}
