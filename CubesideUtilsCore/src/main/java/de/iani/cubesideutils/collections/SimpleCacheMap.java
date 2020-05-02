package de.iani.cubesideutils.collections;

import java.util.LinkedHashMap;

public class SimpleCacheMap<K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = -8551176421553702847L;

    private int maxSize;

    public SimpleCacheMap(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }

}
