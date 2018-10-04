package de.iani.cubesideutils.collections;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class GeneralHashMap<K, V> extends AbstractMap<K, V> implements Cloneable, Serializable {

    private static final long serialVersionUID = 6031498036577864018L;

    public static final ToIntFunction<Object> DEFAULT_HASHER = Objects::hashCode;
    public static final BiPredicate<Object, Object> DEFAULT_EQUALITY = Objects::equals;

    // All three constants from java.util.HashMap
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static final Method STREAM_CHECK_ARRAY_METHOD;

    static {
        try {
            STREAM_CHECK_ARRAY_METHOD =
                    ObjectInputStream.class.getDeclaredMethod("checkArray", Class.class, int.class);
            STREAM_CHECK_ARRAY_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError("checkArray in ObjectInputStream");
        } catch (SecurityException e) {
            throw new Error("Cannot access method checkArray in ObjectInputStream.java.");
        }
    }

    /**
     * Creates a wrapper around the given original that returns 0 if the input is null and delegates
     * it to the original otherwise.
     *
     * @param original the hasher to be wrapped
     * @return null resistant wrapper hasher
     */
    public static <T> ToIntFunction<T> createNullResistantHasher(ToIntFunction<T> original) {
        return x -> x == null ? 0 : original.applyAsInt(x);
    }

    /**
     * Creates a wrapper around the given original that returns 0 if the input is not of the given
     * type and delegates it to the original otherwise.
     * <p>
     * If the input is null, it is delegated.
     *
     * @param original the hasher to be wrapped
     * @param type the type of which all non-delegated input must be
     * @return type resistant wrapper hasher
     */
    @SuppressWarnings("unchecked")
    public static <T> ToIntFunction<Object> createTypeResistantHasher(
            ToIntFunction<? super T> original, Class<T> type) {
        return x -> {
            if (x != null && !type.isInstance(x)) {
                return original.applyAsInt((T) x);
            }
            return 0;
        };
    }

    /**
     * Creates a wrapper around the given original that returns 0 if the input is null or not of the
     * given type and delegates it to the original otherwise.
     *
     * @param original the hasher to be wrapped
     * @param type the type of which all non-delegated input must be
     * @return type and null resistant wrapper hasher
     */
    @SuppressWarnings("unchecked")
    public static <T> ToIntFunction<Object> createResistantHasher(ToIntFunction<? super T> original,
            Class<T> type) {
        return x -> {
            if (x == null) {
                return 0;
            }
            return type.isInstance(x) ? original.applyAsInt((T) x) : 0;
        };
    }

    private static int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    private static void checkArray(ObjectInputStream stream, Class<?> arrayType, int arrayLength)
            throws InvalidClassException {
        try {
            STREAM_CHECK_ARRAY_METHOD.invoke(stream, arrayType, arrayLength);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof InvalidClassException) {
                throw (InvalidClassException) e.getCause();
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    class Node implements Map.Entry<K, V> {

        final int hash;
        final K key;
        V value;
        Node next;

        Node(int hash, K key, V value, Node next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public final K getKey() {
            return this.key;
        }

        @Override
        public final V getValue() {
            return this.value;
        }

        @Override
        public final String toString() {
            return this.key + "=" + this.value;
        }

        @Override
        public final int hashCode() {
            return GeneralHashMap.this.hasher.applyAsInt(this.key) ^ Objects.hashCode(this.value);
        }

        @Override
        public final V setValue(V newValue) {
            V oldValue = this.value;
            this.value = newValue;
            return oldValue;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                try {
                    if (GeneralHashMap.this.equality.test(this.key, (K) e.getKey())
                            && Objects.equals(this.value, e.getValue())) {
                        return true;
                    }
                } catch (ClassCastException exc) {
                    return false;
                }
            }
            return false;
        }
    }

    /**
     * The hash function used by this GeneralHashMap.
     *
     * @serial
     */
    private final ToIntFunction<? super K> hasher;

    /**
     * The equality used to compare keys.
     *
     * @serial
     */
    private final BiPredicate<? super K, ? super K> equality;

    /**
     * The next size value at which to resize (capacity * load factor).
     *
     * @serial
     */
    // (The javadoc description is true upon serialization.
    // Additionally, if the table array has not been allocated, this
    // field holds the initial array capacity, or zero signifying
    // DEFAULT_INITIAL_CAPACITY.)
    private int threshold;

    /**
     * The load factor for the hash table.
     *
     * @serial
     */
    private final float loadFactor;

    /**
     * The table, initialized on first use, and resized as necessary. When allocated, length is
     * always a power of two. (We also tolerate length zero in some operations to allow
     * bootstrapping mechanics that are currently not needed.)
     */
    transient Node[] table;

    /**
     * Holds cached keySet().
     */
    transient Set<K> keySet;

    /**
     * Holds cached entrySet().
     */
    transient Set<Map.Entry<K, V>> entrySet;

    /**
     * Holds cached values().
     */
    transient Collection<V> values;

    /**
     * The number of key-value mappings contained in this map.
     */
    transient int size;

    /**
     * The number of times this HashMap has been structurally modified Structural modifications are
     * those that change the number of mappings in the HashMap or otherwise modify its internal
     * structure (e.g., rehash). This field is used to make iterators on Collection-views of the
     * HashMap fail-fast. (See ConcurrentModificationException).
     */
    transient int modCount;

    public GeneralHashMap(ToIntFunction<? super K> hasher,
            BiPredicate<? super K, ? super K> equality, int initialCapacity, float loadFactor) {

        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);

        this.hasher = Objects.requireNonNull(hasher);
        this.equality = Objects.requireNonNull(equality);
    }

    public GeneralHashMap(ToIntFunction<? super K> hasher,
            BiPredicate<? super K, ? super K> equality, int initialCapacity) {
        this(hasher, equality, initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public GeneralHashMap(ToIntFunction<? super K> hasher,
            BiPredicate<? super K, ? super K> equality) {
        this(hasher, equality, DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public GeneralHashMap(ToIntFunction<? super K> hasher,
            BiPredicate<? super K, ? super K> equality, Map<? extends K, ? extends V> copyOf) {
        this(hasher, equality, DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
        putMapEntries(copyOf, false);
    }

    @SuppressWarnings("unchecked")
    protected int hash(Object key) {
        try {
            int h = this.hasher.applyAsInt((K) key);
            return h ^ (h >>> 16);
        } catch (ClassCastException e) {
            return 0;
        }
    }

    protected void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        int s = m.size();
        if (s > 0) {
            if (this.table == null) { // pre-size
                float ft = (s / this.loadFactor) + 1.0F;
                int t = ((ft < MAXIMUM_CAPACITY) ? (int) ft : MAXIMUM_CAPACITY);
                if (t > this.threshold) {
                    this.threshold = tableSizeFor(t);
                }
            } else if (s > this.threshold) {
                resize();
            }
            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                putVal(hash(key), key, value, false, evict);
            }
        }
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this map contains
     * no mapping for the key.
     *
     * <p>
     * More formally, if this map contains a mapping from a key {@code k} to a value {@code v} such
     * that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise it returns {@code null}.
     * (There can be at most one such mapping.)
     *
     * <p>
     * A return value of {@code null} does not <i>necessarily</i> indicate that the map contains no
     * mapping for the key; it's also possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to distinguish these two cases.
     *
     * @see #put(Object, Object)
     */
    @Override
    public V get(Object key) {
        Node e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    /**
     * Implements Map.get and related methods
     *
     * @param hash hash for key
     * @param key the key
     * @return the node, or null if none
     */
    @SuppressWarnings("unchecked")
    final Node getNode(int hash, Object key) {
        try {
            Node[] tab;
            Node first, e;
            int n;
            if ((tab = this.table) != null && (n = tab.length) > 0
                    && (first = tab[(n - 1) & hash]) != null) {
                if (first.hash == hash && // always check first node
                        this.equality.test(first.key, (K) key)) {
                    return first;
                }
                if ((e = first.next) != null) {
                    do {
                        if (e.hash == hash && this.equality.test(e.key, (K) key)) {
                            return e;
                        }
                    } while ((e = e.next) != null);
                }
            }
            return null;
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified key.
     *
     * @param key The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified key.
     */
    @Override
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

    /**
     * Associates the specified value with the specified key in this map. If the map previously
     * contained a mapping for the key, the old value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no
     *         mapping for <tt>key</tt>. (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    @Override
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    /**
     * Implements Map.put and related methods
     *
     * @param hash hash for key
     * @param key the key
     * @param value the value to put
     * @param onlyIfAbsent if true, don't change existing value
     * @param evict if false, the table is in creation mode.
     * @return previous value, or null if none
     */
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
        Node[] tab;
        Node p;
        int n, i;
        if ((tab = this.table) == null || (n = tab.length) == 0) {
            n = (tab = resize()).length;
        }
        if ((p = tab[i = (n - 1) & hash]) == null) {
            tab[i] = newNode(hash, key, value, null);
        } else {
            Node e;
            if (p.hash == hash && this.equality.test(p.key, key)) {
                e = p;
            } else {
                while (true) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        break;
                    }
                    if (e.hash == hash && this.equality.test(e.key, key)) {
                        break;
                    }
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null) {
                    e.value = value;
                }
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++this.modCount;
        if (++this.size > this.threshold) {
            resize();
        }
        afterNodeInsertion(evict);
        return null;
    }

    /**
     * Initializes or doubles table size. If null, allocates in accord with initial capacity target
     * held in field threshold. Otherwise, because we are using power-of-two expansion, the elements
     * from each bin must either stay at same index, or move with a power of two offset in the new
     * table.
     *
     * @return the table
     */
    final Node[] resize() {
        Node[] oldTab = this.table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = this.threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                this.threshold = Integer.MAX_VALUE;
                return oldTab;
            } else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY
                    && oldCap >= DEFAULT_INITIAL_CAPACITY) {
                newThr = oldThr << 1; // double threshold
            }
        } else if (oldThr > 0) {
            newCap = oldThr;
        } else { // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = newCap * this.loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < MAXIMUM_CAPACITY ? (int) ft
                    : Integer.MAX_VALUE);
        }
        this.threshold = newThr;
        @SuppressWarnings("unchecked")
        Node[] newTab = (Node[]) new GeneralHashMap<?, ?>.Node[newCap];
        this.table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null) {
                        newTab[e.hash & (newCap - 1)] = e;
                    } else { // preserve order
                        Node loHead = null, loTail = null;
                        Node hiHead = null, hiTail = null;
                        Node next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null) {
                                    loHead = e;
                                } else {
                                    loTail.next = e;
                                }
                                loTail = e;
                            } else {
                                if (hiTail == null) {
                                    hiHead = e;
                                } else {
                                    hiTail.next = e;
                                }
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

    /**
     * Copies all of the mappings from the specified map to this map. These mappings will replace
     * any mappings that this map had for any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        putMapEntries(m, true);
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no
     *         mapping for <tt>key</tt>. (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    @Override
    public V remove(Object key) {
        Node e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ? null : e.value;
    }

    /**
     * Implements Map.remove and related methods
     *
     * @param hash hash for key
     * @param key the key
     * @param value the value to match if matchValue, else ignored
     * @param matchValue if true only remove if value is equal
     * @param movable if false do not move other nodes while removing
     * @return the node, or null if none
     */
    @SuppressWarnings("unchecked")
    final Node removeNode(int hash, Object key, Object value, boolean matchValue, boolean movable) {
        try {
            Node[] tab;
            Node p;
            int n, index;
            if ((tab = this.table) != null && (n = tab.length) > 0
                    && (p = tab[index = (n - 1) & hash]) != null) {
                Node node = null, e;
                V v;
                if (p.hash == hash && this.equality.test(p.key, (K) key)) {
                    node = p;
                } else if ((e = p.next) != null) {
                    do {
                        if (e.hash == hash && this.equality.test(e.key, (K) key)) {
                            node = e;
                            break;
                        }
                        p = e;
                    } while ((e = e.next) != null);
                }
                if (node != null && (!matchValue || (v = node.value) == value
                        || (value != null && value.equals(v)))) {
                    if (node == p) {
                        tab[index] = node.next;
                    } else {
                        p.next = node.next;
                    }
                    ++this.modCount;
                    --this.size;
                    afterNodeRemoval(node);
                    return node;
                }
            }
            return null;
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Removes all of the mappings from this map. The map will be empty after this call returns.
     */
    @Override
    public void clear() {
        Node[] tab;
        this.modCount++;
        if ((tab = this.table) != null && this.size > 0) {
            this.size = 0;
            for (int i = 0; i < tab.length; ++i) {
                tab[i] = null;
            }
        }
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the specified value
     */
    @Override
    public boolean containsValue(Object value) {
        Node[] tab;
        V v;
        if ((tab = this.table) != null && this.size > 0) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node e = tab[i]; e != null; e = e.next) {
                    if ((v = e.value) == value || (value != null && value.equals(v))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map. The set is backed by the map,
     * so changes to the map are reflected in the set, and vice-versa. If the map is modified while
     * an iteration over the set is in progress (except through the iterator's own <tt>remove</tt>
     * operation), the results of the iteration are undefined. The set supports element removal,
     * which removes the corresponding mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this map
     */
    @Override
    public Set<K> keySet() {
        Set<K> ks = this.keySet;
        if (ks == null) {
            ks = new KeySet();
            this.keySet = ks;
        }
        return ks;
    }

    final class KeySet extends AbstractSet<K> {

        @Override
        public final int size() {
            return GeneralHashMap.this.size;
        }

        @Override
        public final void clear() {
            GeneralHashMap.this.clear();
        }

        @Override
        public final Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public final boolean contains(Object o) {
            return containsKey(o);
        }

        @Override
        public final boolean remove(Object key) {
            return removeNode(hash(key), key, null, false, true) != null;
        }

        @Override
        public final Spliterator<K> spliterator() {
            return new KeySpliterator(GeneralHashMap.this, 0, -1, 0, 0);
        }

        @Override
        public final void forEach(Consumer<? super K> action) {
            Node[] tab;
            if (action == null) {
                throw new NullPointerException();
            }
            if (GeneralHashMap.this.size > 0 && (tab = GeneralHashMap.this.table) != null) {
                int mc = GeneralHashMap.this.modCount;
                for (int i = 0; i < tab.length; ++i) {
                    for (Node e = tab[i]; e != null; e = e.next) {
                        action.accept(e.key);
                    }
                }
                if (GeneralHashMap.this.modCount != mc) {
                    throw new ConcurrentModificationException();
                }
            }
        }
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map. The collection is
     * backed by the map, so changes to the map are reflected in the collection, and vice-versa. If
     * the map is modified while an iteration over the collection is in progress (except through the
     * iterator's own <tt>remove</tt> operation), the results of the iteration are undefined. The
     * collection supports element removal, which removes the corresponding mapping from the map,
     * via the <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations. It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     *
     * @return a view of the values contained in this map
     */
    @Override
    public Collection<V> values() {
        Collection<V> vs = this.values;
        if (vs == null) {
            vs = new Values();
            this.values = vs;
        }
        return vs;
    }

    final class Values extends AbstractCollection<V> {

        @Override
        public final int size() {
            return GeneralHashMap.this.size;
        }

        @Override
        public final void clear() {
            GeneralHashMap.this.clear();
        }

        @Override
        public final Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public final boolean contains(Object o) {
            return containsValue(o);
        }

        @Override
        public final Spliterator<V> spliterator() {
            return new ValueSpliterator(GeneralHashMap.this, 0, -1, 0, 0);
        }

        @Override
        public final void forEach(Consumer<? super V> action) {
            Node[] tab;
            if (action == null) {
                throw new NullPointerException();
            }
            if (GeneralHashMap.this.size > 0 && (tab = GeneralHashMap.this.table) != null) {
                int mc = GeneralHashMap.this.modCount;
                for (int i = 0; i < tab.length; ++i) {
                    for (Node e = tab[i]; e != null; e = e.next) {
                        action.accept(e.value);
                    }
                }
                if (GeneralHashMap.this.modCount != mc) {
                    throw new ConcurrentModificationException();
                }
            }
        }
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map. The set is backed by the
     * map, so changes to the map are reflected in the set, and vice-versa. If the map is modified
     * while an iteration over the set is in progress (except through the iterator's own
     * <tt>remove</tt> operation, or through the <tt>setValue</tt> operation on a map entry returned
     * by the iterator) the results of the iteration are undefined. The set supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations. It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     *
     * @return a set view of the mappings contained in this map
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es;
        return (es = this.entrySet) == null ? (this.entrySet = new EntrySet()) : es;
    }

    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public final int size() {
            return GeneralHashMap.this.size;
        }

        @Override
        public final void clear() {
            GeneralHashMap.this.clear();
        }

        @Override
        public final Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            Object key = e.getKey();
            Node candidate = getNode(hash(key), key);
            return candidate != null && candidate.equals(e);
        }

        @Override
        public final boolean remove(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                Object key = e.getKey();
                Object value = e.getValue();
                return removeNode(hash(key), key, value, true, true) != null;
            }
            return false;
        }

        @Override
        public final Spliterator<Map.Entry<K, V>> spliterator() {
            return new EntrySpliterator(GeneralHashMap.this, 0, -1, 0, 0);
        }

        @Override
        public final void forEach(Consumer<? super Map.Entry<K, V>> action) {
            Node[] tab;
            if (action == null) {
                throw new NullPointerException();
            }
            if (GeneralHashMap.this.size > 0 && (tab = GeneralHashMap.this.table) != null) {
                int mc = GeneralHashMap.this.modCount;
                for (int i = 0; i < tab.length; ++i) {
                    for (Node e = tab[i]; e != null; e = e.next) {
                        action.accept(e);
                    }
                }
                if (GeneralHashMap.this.modCount != mc) {
                    throw new ConcurrentModificationException();
                }
            }
        }
    }

    // Overrides of JDK8 Map extension methods

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        Node e;
        return (e = getNode(hash(key), key)) == null ? defaultValue : e.value;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return putVal(hash(key), key, value, true, true);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return removeNode(hash(key), key, value, true, true) != null;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        Node e;
        V v;
        if ((e = getNode(hash(key), key)) != null
                && ((v = e.value) == oldValue || (v != null && v.equals(oldValue)))) {
            e.value = newValue;
            afterNodeAccess(e);
            return true;
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        Node e;
        if ((e = getNode(hash(key), key)) != null) {
            V oldValue = e.value;
            e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
        return null;
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        if (mappingFunction == null) {
            throw new NullPointerException();
        }
        int hash = hash(key);
        Node[] tab;
        Node first;
        int n, i;
        Node old = null;
        if (this.size > this.threshold || (tab = this.table) == null || (n = tab.length) == 0) {
            n = (tab = resize()).length;
        }
        if ((first = tab[i = (n - 1) & hash]) != null) {
            Node e = first;
            do {
                if (e.hash == hash && this.equality.test(e.key, key)) {
                    old = e;
                    break;
                }
            } while ((e = e.next) != null);
            V oldValue;
            if (old != null && (oldValue = old.value) != null) {
                afterNodeAccess(old);
                return oldValue;
            }
        }
        V v = mappingFunction.apply(key);
        if (v == null) {
            return null;
        } else if (old != null) {
            old.value = v;
            afterNodeAccess(old);
            return v;
        } else {
            tab[i] = newNode(hash, key, v, first);
        }
        ++this.modCount;
        ++this.size;
        afterNodeInsertion(true);
        return v;
    }

    @Override
    public V computeIfPresent(K key,
            BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (remappingFunction == null) {
            throw new NullPointerException();
        }
        Node e;
        V oldValue;
        int hash = hash(key);
        if ((e = getNode(hash, key)) != null && (oldValue = e.value) != null) {
            V v = remappingFunction.apply(key, oldValue);
            if (v != null) {
                e.value = v;
                afterNodeAccess(e);
                return v;
            } else {
                removeNode(hash, key, null, false, true);
            }
        }
        return null;
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (remappingFunction == null) {
            throw new NullPointerException();
        }
        int hash = hash(key);
        Node[] tab;
        Node first;
        int n, i;
        Node old = null;
        if (this.size > this.threshold || (tab = this.table) == null || (n = tab.length) == 0) {
            n = (tab = resize()).length;
        }
        if ((first = tab[i = (n - 1) & hash]) != null) {
            Node e = first;
            do {
                if (e.hash == hash && this.equality.test(e.key, key)) {
                    old = e;
                    break;
                }
            } while ((e = e.next) != null);
        }
        V oldValue = (old == null) ? null : old.value;
        V v = remappingFunction.apply(key, oldValue);
        if (old != null) {
            if (v != null) {
                old.value = v;
                afterNodeAccess(old);
            } else {
                removeNode(hash, key, null, false, true);
            }
        } else if (v != null) {
            tab[i] = newNode(hash, key, v, first);
            ++this.modCount;
            ++this.size;
            afterNodeInsertion(true);
        }
        return v;
    }

    @Override
    public V merge(K key, V value,
            BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (remappingFunction == null) {
            throw new NullPointerException();
        }
        int hash = hash(key);
        Node[] tab;
        Node first;
        int n, i;
        Node old = null;
        if (this.size > this.threshold || (tab = this.table) == null || (n = tab.length) == 0) {
            n = (tab = resize()).length;
        }
        if ((first = tab[i = (n - 1) & hash]) != null) {
            Node e = first;
            do {
                if (e.hash == hash && this.equality.test(e.key, key)) {
                    old = e;
                    break;
                }
            } while ((e = e.next) != null);
        }
        if (old != null) {
            V v;
            if (old.value != null) {
                v = remappingFunction.apply(old.value, value);
            } else {
                v = value;
            }
            if (v != null) {
                old.value = v;
                afterNodeAccess(old);
            } else {
                removeNode(hash, key, null, false, true);
            }
            return v;
        }
        tab[i] = newNode(hash, key, value, first);
        ++this.modCount;
        ++this.size;
        afterNodeInsertion(true);
        return value;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Node[] tab;
        if (action == null) {
            throw new NullPointerException();
        }
        if (this.size > 0 && (tab = this.table) != null) {
            int mc = this.modCount;
            for (int i = 0; i < tab.length; ++i) {
                for (Node e = tab[i]; e != null; e = e.next) {
                    action.accept(e.key, e.value);
                }
            }
            if (this.modCount != mc) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Node[] tab;
        if (function == null) {
            throw new NullPointerException();
        }
        if (this.size > 0 && (tab = this.table) != null) {
            int mc = this.modCount;
            for (int i = 0; i < tab.length; ++i) {
                for (Node e = tab[i]; e != null; e = e.next) {
                    e.value = function.apply(e.key, e.value);
                }
            }
            if (this.modCount != mc) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /* ------------------------------------------------------------ */
    // Cloning and serialization

    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and values themselves are
     * not cloned.
     *
     * @return a shallow copy of this map
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        GeneralHashMap<K, V> result;
        try {
            result = (GeneralHashMap<K, V>) super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
        result.reinitialize();
        result.putMapEntries(this, false);
        return result;
    }

    // These methods are also used when serializing HashSets
    final float loadFactor() {
        return this.loadFactor;
    }

    final int capacity() {
        return (this.table != null) ? this.table.length
                : (this.threshold > 0) ? this.threshold : DEFAULT_INITIAL_CAPACITY;
    }

    /**
     * Save the state of the <tt>HashMap</tt> instance to a stream (i.e., serialize it).
     *
     * @serialData The <i>capacity</i> of the HashMap (the length of the bucket array) is emitted
     *             (int), followed by the <i>size</i> (an int, the number of key-value mappings),
     *             followed by the key (Object) and value (Object) for each key-value mapping. The
     *             key-value mappings are emitted in no particular order.
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        int buckets = capacity();
        // Write out the threshold, loadfactor, and any hidden stuff
        s.defaultWriteObject();
        s.writeInt(buckets);
        s.writeInt(this.size);
        internalWriteEntries(s);
    }

    /**
     * Reconstitute the {@code HashMap} instance from a stream (i.e., deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        // Read in the threshold (ignored), loadfactor, and any hidden stuff
        s.defaultReadObject();
        reinitialize();
        if (this.loadFactor <= 0 || Float.isNaN(this.loadFactor)) {
            throw new InvalidObjectException("Illegal load factor: " + this.loadFactor);
        }
        s.readInt(); // Read and ignore number of buckets
        int mappings = s.readInt(); // Read number of mappings (size)
        if (mappings < 0) {
            throw new InvalidObjectException("Illegal mappings count: " + mappings);
        } else if (mappings > 0) { // (if zero, use defaults)
            // Size the table using given load factor only if within
            // range of 0.25...4.0
            float lf = Math.min(Math.max(0.25f, this.loadFactor), 4.0f);
            float fc = mappings / lf + 1.0f;
            int cap = ((fc < DEFAULT_INITIAL_CAPACITY) ? DEFAULT_INITIAL_CAPACITY
                    : (fc >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : tableSizeFor((int) fc));
            float ft = cap * lf;
            this.threshold = ((cap < MAXIMUM_CAPACITY && ft < MAXIMUM_CAPACITY) ? (int) ft
                    : Integer.MAX_VALUE);

            // Check Map.Entry[].class since it's the nearest public type to
            // what we're actually creating.
            checkArray(s, Map.Entry[].class, cap);
            @SuppressWarnings("unchecked")
            Node[] tab = (Node[]) new GeneralHashMap<?, ?>.Node[cap];
            this.table = tab;

            // Read the keys and values, and put the mappings in the HashMap
            for (int i = 0; i < mappings; i++) {
                @SuppressWarnings("unchecked")
                K key = (K) s.readObject();
                @SuppressWarnings("unchecked")
                V value = (V) s.readObject();
                putVal(hash(key), key, value, false, false);
            }
        }
    }

    /* ------------------------------------------------------------ */
    // iterators

    abstract class HashIterator {

        Node next; // next entry to return
        Node current; // current entry
        int expectedModCount; // for fast-fail
        int index; // current slot

        HashIterator() {
            this.expectedModCount = GeneralHashMap.this.modCount;
            Node[] t = GeneralHashMap.this.table;
            this.current = this.next = null;
            this.index = 0;
            if (t != null && GeneralHashMap.this.size > 0) { // advance to first entry
                do {
                } while (this.index < t.length && (this.next = t[this.index++]) == null);
            }
        }

        public final boolean hasNext() {
            return this.next != null;
        }

        final Node nextNode() {
            Node[] t;
            Node e = this.next;
            if (GeneralHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (e == null) {
                throw new NoSuchElementException();
            }
            if ((this.next = (this.current = e).next) == null
                    && (t = GeneralHashMap.this.table) != null) {
                do {
                } while (this.index < t.length && (this.next = t[this.index++]) == null);
            }
            return e;
        }

        public final void remove() {
            Node p = this.current;
            if (p == null) {
                throw new IllegalStateException();
            }
            if (GeneralHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            this.current = null;
            K key = p.key;
            removeNode(hash(key), key, null, false, false);
            this.expectedModCount = GeneralHashMap.this.modCount;
        }
    }

    final class KeyIterator extends HashIterator implements Iterator<K> {

        @Override
        public final K next() {
            return nextNode().key;
        }
    }

    final class ValueIterator extends HashIterator implements Iterator<V> {

        @Override
        public final V next() {
            return nextNode().value;
        }
    }

    final class EntryIterator extends HashIterator implements Iterator<Map.Entry<K, V>> {

        @Override
        public final Map.Entry<K, V> next() {
            return nextNode();
        }
    }

    /* ------------------------------------------------------------ */
    // spliterators

    class HashMapSpliterator {

        final GeneralHashMap<K, V> map;
        Node current; // current node
        int index; // current index, modified on advance/split
        int fence; // one past last index
        int est; // size estimate
        int expectedModCount; // for comodification checks

        HashMapSpliterator(GeneralHashMap<K, V> m, int origin, int fence, int est,
                int expectedModCount) {
            this.map = m;
            this.index = origin;
            this.fence = fence;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }

        final int getFence() { // initialize fence and size on first use
            int hi;
            if ((hi = this.fence) < 0) {
                GeneralHashMap<K, V> m = this.map;
                this.est = m.size;
                this.expectedModCount = m.modCount;
                Node[] tab = m.table;
                hi = this.fence = (tab == null) ? 0 : tab.length;
            }
            return hi;
        }

        public final long estimateSize() {
            getFence(); // force init
            return this.est;
        }
    }

    final class KeySpliterator extends HashMapSpliterator implements Spliterator<K> {

        KeySpliterator(GeneralHashMap<K, V> m, int origin, int fence, int est,
                int expectedModCount) {
            super(m, origin, fence, est, expectedModCount);
        }

        @Override
        public KeySpliterator trySplit() {
            int hi = getFence(), lo = this.index, mid = (lo + hi) >>> 1;
            return (lo >= mid || this.current != null) ? null
                    : new KeySpliterator(this.map, lo, this.index = mid, this.est >>>= 1,
                            this.expectedModCount);
        }

        @Override
        public void forEachRemaining(Consumer<? super K> action) {
            int i, hi, mc;
            if (action == null) {
                throw new NullPointerException();
            }
            GeneralHashMap<K, V> m = this.map;
            Node[] tab = m.table;
            if ((hi = this.fence) < 0) {
                mc = this.expectedModCount = m.modCount;
                hi = this.fence = (tab == null) ? 0 : tab.length;
            } else {
                mc = this.expectedModCount;
            }
            if (tab != null && tab.length >= hi && (i = this.index) >= 0
                    && (i < (this.index = hi) || this.current != null)) {
                Node p = this.current;
                this.current = null;
                do {
                    if (p == null) {
                        p = tab[i++];
                    } else {
                        action.accept(p.key);
                        p = p.next;
                    }
                } while (p != null || i < hi);
                if (m.modCount != mc) {
                    throw new ConcurrentModificationException();
                }
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super K> action) {
            int hi;
            if (action == null) {
                throw new NullPointerException();
            }
            Node[] tab = this.map.table;
            if (tab != null && tab.length >= (hi = getFence()) && this.index >= 0) {
                while (this.current != null || this.index < hi) {
                    if (this.current == null) {
                        this.current = tab[this.index++];
                    } else {
                        K k = this.current.key;
                        this.current = this.current.next;
                        action.accept(k);
                        if (this.map.modCount != this.expectedModCount) {
                            throw new ConcurrentModificationException();
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public int characteristics() {
            return (this.fence < 0 || this.est == this.map.size ? Spliterator.SIZED : 0)
                    | Spliterator.DISTINCT;
        }
    }

    final class ValueSpliterator extends HashMapSpliterator implements Spliterator<V> {

        ValueSpliterator(GeneralHashMap<K, V> m, int origin, int fence, int est,
                int expectedModCount) {
            super(m, origin, fence, est, expectedModCount);
        }

        @Override
        public ValueSpliterator trySplit() {
            int hi = getFence(), lo = this.index, mid = (lo + hi) >>> 1;
            return (lo >= mid || this.current != null) ? null
                    : new ValueSpliterator(this.map, lo, this.index = mid, this.est >>>= 1,
                            this.expectedModCount);
        }

        @Override
        public void forEachRemaining(Consumer<? super V> action) {
            int i, hi, mc;
            if (action == null) {
                throw new NullPointerException();
            }
            GeneralHashMap<K, V> m = this.map;
            Node[] tab = m.table;
            if ((hi = this.fence) < 0) {
                mc = this.expectedModCount = m.modCount;
                hi = this.fence = (tab == null) ? 0 : tab.length;
            } else {
                mc = this.expectedModCount;
            }
            if (tab != null && tab.length >= hi && (i = this.index) >= 0
                    && (i < (this.index = hi) || this.current != null)) {
                Node p = this.current;
                this.current = null;
                do {
                    if (p == null) {
                        p = tab[i++];
                    } else {
                        action.accept(p.value);
                        p = p.next;
                    }
                } while (p != null || i < hi);
                if (m.modCount != mc) {
                    throw new ConcurrentModificationException();
                }
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super V> action) {
            int hi;
            if (action == null) {
                throw new NullPointerException();
            }
            Node[] tab = this.map.table;
            if (tab != null && tab.length >= (hi = getFence()) && this.index >= 0) {
                while (this.current != null || this.index < hi) {
                    if (this.current == null) {
                        this.current = tab[this.index++];
                    } else {
                        V v = this.current.value;
                        this.current = this.current.next;
                        action.accept(v);
                        if (this.map.modCount != this.expectedModCount) {
                            throw new ConcurrentModificationException();
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public int characteristics() {
            return (this.fence < 0 || this.est == this.map.size ? Spliterator.SIZED : 0);
        }
    }

    final class EntrySpliterator extends HashMapSpliterator
            implements Spliterator<Map.Entry<K, V>> {

        EntrySpliterator(GeneralHashMap<K, V> m, int origin, int fence, int est,
                int expectedModCount) {
            super(m, origin, fence, est, expectedModCount);
        }

        @Override
        public EntrySpliterator trySplit() {
            int hi = getFence(), lo = this.index, mid = (lo + hi) >>> 1;
            return (lo >= mid || this.current != null) ? null
                    : new EntrySpliterator(this.map, lo, this.index = mid, this.est >>>= 1,
                            this.expectedModCount);
        }

        @Override
        public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
            int i, hi, mc;
            if (action == null) {
                throw new NullPointerException();
            }
            GeneralHashMap<K, V> m = this.map;
            Node[] tab = m.table;
            if ((hi = this.fence) < 0) {
                mc = this.expectedModCount = m.modCount;
                hi = this.fence = (tab == null) ? 0 : tab.length;
            } else {
                mc = this.expectedModCount;
            }
            if (tab != null && tab.length >= hi && (i = this.index) >= 0
                    && (i < (this.index = hi) || this.current != null)) {
                Node p = this.current;
                this.current = null;
                do {
                    if (p == null) {
                        p = tab[i++];
                    } else {
                        action.accept(p);
                        p = p.next;
                    }
                } while (p != null || i < hi);
                if (m.modCount != mc) {
                    throw new ConcurrentModificationException();
                }
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> action) {
            int hi;
            if (action == null) {
                throw new NullPointerException();
            }
            Node[] tab = this.map.table;
            if (tab != null && tab.length >= (hi = getFence()) && this.index >= 0) {
                while (this.current != null || this.index < hi) {
                    if (this.current == null) {
                        this.current = tab[this.index++];
                    } else {
                        Node e = this.current;
                        this.current = this.current.next;
                        action.accept(e);
                        if (this.map.modCount != this.expectedModCount) {
                            throw new ConcurrentModificationException();
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public int characteristics() {
            return (this.fence < 0 || this.est == this.map.size ? Spliterator.SIZED : 0)
                    | Spliterator.DISTINCT;
        }
    }

    /* ------------------------------------------------------------ */
    // LinkedHashMap support

    /*
     * The following package-protected methods are designed to be overridden by LinkedHashMap, but
     * not by any other subclass. Nearly all other internal methods are also package-protected but
     * are declared final, so can be used by LinkedHashMap, view classes, and HashSet.
     */

    // Create a regular (non-tree) node
    Node newNode(int hash, K key, V value, Node next) {
        return new Node(hash, key, value, next);
    }

    // For conversion from TreeNodes to plain nodes
    Node replacementNode(Node p, Node next) {
        return new Node(p.hash, p.key, p.value, next);
    }

    /**
     * Reset to initial default state. Called by clone and readObject.
     */
    void reinitialize() {
        this.table = null;
        this.entrySet = null;
        this.keySet = null;
        this.values = null;
        this.modCount = 0;
        this.threshold = 0;
        this.size = 0;
    }

    // Callbacks to allow LinkedHashMap post-actions
    void afterNodeAccess(Node p) {}

    void afterNodeInsertion(boolean evict) {}

    void afterNodeRemoval(Node p) {}

    // Called only from writeObject, to ensure compatible ordering.
    void internalWriteEntries(java.io.ObjectOutputStream s) throws IOException {
        Node[] tab;
        if (this.size > 0 && (tab = this.table) != null) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node e = tab[i]; e != null; e = e.next) {
                    s.writeObject(e.key);
                    s.writeObject(e.value);
                }
            }
        }
    }

}
