package de.iani.cubesideutils.collections;

import com.google.common.collect.Sets;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AdvancedCacheMap<K, V, D> extends SimpleCacheMap<K, V> {

    private static final long serialVersionUID = -47074743727889121L;

    private ReadWriteLock lock;
    private Map<K, V> hardCache;
    private D defaultData;

    protected AdvancedCacheMap(int maxSoftCacheSize, D defaultData) {
        super(maxSoftCacheSize);

        this.lock = new ReentrantReadWriteLock();
        this.hardCache = new HashMap<>();
        this.defaultData = defaultData;
    }

    protected void invalidate(K key) {
        // MAY NOT HAVE READ LOCK
        writeLock().lock();
        try {
            boolean hardCached = false;
            V uncached = this.hardCache.remove(key);
            if (uncached == null) {
                uncached = super.remove(key);
            } else {
                hardCached = true;
            }

            if (uncached == null) {
                return;
            }

            boolean replace = shouldBeReplaced(key, uncached, hardCached);
            uncached(key, uncached, hardCached, replace);

            if (replace) {
                V replacement = getReplacement(key, uncached);
                if (replacement != null) {
                    this.hardCache.put(key, replacement);
                }
                replaced(key, uncached, replacement);
            }
        } finally {
            writeLock().unlock();
        }
    }

    @Override
    public V get(Object key) {
        return get(key, defaultData);
    }

    @SuppressWarnings("unchecked")
    protected V get(Object key, D data) {
        checkData(data);
        if (!checkKey(key)) {
            return null;
        }

        readLock().lock();
        try {
            V result = this.hardCache.get(key);
            if (result != null) {
                return result;
            }

            result = super.get(key);
            if (result != null || !shouldLoadIntoCache((K) key, data)) {
                return result;
            }

            // unlock read to be allowed to lock write
            readLock().unlock();
            writeLock().lock();

            try {
                // relock read to unlock in outer finally
                readLock().lock();

                // might have changed during temporary unlock
                result = this.hardCache.get(key);
                if (result != null) {
                    return result;
                }

                return super.computeIfAbsent((K) key, k -> load(k, data));
            } finally {
                writeLock().unlock();
            }
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V result = get(key);
        return result == null ? defaultValue : result;
    }

    @Override
    public boolean containsKey(Object key) {
        readLock().lock();
        try {
            return super.containsKey(key) || this.hardCache.containsKey(key);
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public boolean containsValue(Object key) {
        readLock().lock();
        try {
            return super.containsValue(key) || this.hardCache.containsValue(key);
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V putIfAbsent(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V replace(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return Sets.union(super.entrySet(), this.hardCache.entrySet());
    }

    @Override
    public Set<K> keySet() {
        return Sets.union(super.keySet(), this.hardCache.keySet());
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<>() {
            @Override
            public Iterator<V> iterator() {
                return IteratorUtil.concat(AdvancedCacheMap.super.values(), AdvancedCacheMap.this.hardCache.values()).iterator();
            }

            @Override
            public int size() {
                return AdvancedCacheMap.this.size();
            }
        };
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        readLock().lock();
        try {
            entrySet().forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public int size() {
        readLock().lock();
        try {
            return super.size() + this.hardCache.size();
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public Object clone() {
        throw new UnsupportedOperationException();
    }

    protected Lock readLock() {
        return this.lock.readLock();
    }

    protected Lock writeLock() {
        return this.lock.writeLock();
    }

    protected abstract void checkData(D data);

    protected abstract boolean checkKey(Object key);

    protected abstract boolean shouldLoadIntoCache(K key, D data);

    protected abstract V load(K key, D data);

    protected Map<K, V> getHardCache() {
        return this.hardCache;
    }

    protected V getFromHardCache(K key) {
        readLock().lock();
        try {
            return this.hardCache.get(key);
        } finally {
            readLock().unlock();
        }
    }

    protected void addToHardCache(K key, V value) {
        writeLock().lock();
        try {
            invalidate(key);
            this.hardCache.put(key, value);
        } finally {
            writeLock().unlock();
        }
    }

    protected V removeFromHardCache(K key) {
        writeLock().lock();
        try {
            return this.hardCache.remove(key);
        } finally {
            writeLock().unlock();
        }
    }

    protected V getFromSoftCache(K key) {
        readLock().lock();
        try {
            return super.get(key);
        } finally {
            readLock().unlock();
        }
    }

    protected void addToSoftCache(K key, V value) {
        writeLock().lock();
        try {
            if (this.hardCache.containsKey(key)) {
                throw new IllegalArgumentException("cannot add mapping to soft cache that exists in hard cache");
            }
            super.put(key, value);
        } finally {
            writeLock().unlock();
        }
    }

    protected V removeFromSoftCache(K key) {
        writeLock().lock();
        try {
            return super.remove(key);
        } finally {
            writeLock().unlock();
        }
    }

    protected boolean shouldBeReplaced(K key, V uncached, boolean removedFromHardCache) {
        return removedFromHardCache;
    }

    protected void uncached(K key, V uncached, boolean removedFromHardCache, boolean willBeReplaced) {

    }

    protected abstract V getReplacement(K key, V uncached);

    protected void replaced(K key, V uncached, V replacement) {

    }

}
