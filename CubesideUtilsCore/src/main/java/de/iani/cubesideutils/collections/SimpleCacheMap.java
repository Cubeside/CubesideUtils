package de.iani.cubesideutils.collections;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleCacheMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = -8551176421553702847L;

    private int maxSize;

    public SimpleCacheMap(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (size() > maxSize) {
            eldestUncached(eldest.getKey(), eldest.getValue());
            return true;
        } else {
            return false;
        }
    }

    protected void eldestUncached(K key, V value) {

    }

}
