package de.iani.cubesideutils.collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Test;

public class GeneralHashMapTest {

    private static final ToIntFunction<Object> NON_STANDARD_HASHER = arg -> Objects.hashCode(arg) ^ -0x713A3C3F;

    private static final ToIntFunction<Integer> MODULO_100_HASHER = arg -> arg == null ? 0 : Integer.hashCode(arg % 100);
    private static final BiPredicate<Integer, Integer> MODULO_100_EQUALITY = (p1, p2) -> p1 == null ? p2 == null : (p2 != null && p1 % 100 == p2 % 100);

    protected GeneralHashMap<Integer, Object> defaultHashMap;
    protected GeneralHashMap<Integer, Object> nonStandardHasherMap;
    protected GeneralHashMap<Integer, Object> modulo100HasherMap;

    public GeneralHashMapTest() {
        init();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void init() {
        initMaps((hasher, equality) -> new GeneralHashMap(hasher, equality));
    }

    @SuppressWarnings("unchecked")
    protected void initMaps(BiFunction<ToIntFunction<?>, BiPredicate<?, ?>, ? extends GeneralHashMap<?, ?>> mapCreator) {
        this.defaultHashMap = (GeneralHashMap<Integer, Object>) mapCreator.apply(GeneralHashMap.DEFAULT_HASHER, GeneralHashMap.DEFAULT_EQUALITY);
        this.nonStandardHasherMap = (GeneralHashMap<Integer, Object>) mapCreator.apply(NON_STANDARD_HASHER, GeneralHashMap.DEFAULT_EQUALITY);
        this.modulo100HasherMap = (GeneralHashMap<Integer, Object>) mapCreator.apply(MODULO_100_HASHER, MODULO_100_EQUALITY);
    }

    @Test
    public void testEmptyMaps() {
        testEmpty(this.defaultHashMap);
        testEmpty(this.nonStandardHasherMap);
        testEmpty(this.modulo100HasherMap);
    }

    protected <K, V> void testEmpty(GeneralHashMap<K, V> map) {
        assertThat(map.isEmpty(), is(true));
        assertThat(map.size(), is(equalTo(0)));
        assertThat(map.entrySet(), is(empty()));
        assertThat(map.keySet(), is(empty()));
        assertThat(map.values(), is(empty()));

        for (int i = -1000; i < 1000; i++) {
            assertThat(map.containsKey(i), is(false));
            assertThat(map.get(i), is(nullValue()));
        }
    }

    @Test
    public void testFewPutsAndRemovesOnMaps() {
        testSomePutsAndRemoves(this.defaultHashMap);
        testSomePutsAndRemoves(this.nonStandardHasherMap);
        testSomePutsAndRemoves(this.modulo100HasherMap);
    }

    protected void testSomePutsAndRemoves(GeneralHashMap<Integer, Object> map) {
        Object[] values = new Object[5];
        for (int i = 0; i < values.length; i++) {
            values[i] = "old" + i;
        }

        for (int i = 0; i < values.length; i++) {
            assertThat(map.put(i, values[i]), is(nullValue()));
        }

        Object[] newValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = "new" + i;
        }

        for (int i = 0; i < values.length; i++) {
            assertThat(map.put(i, newValues[i]), is(equalTo(values[i])));
        }

        for (int i = 0; i < values.length; i++) {
            assertThat(map.remove(i), is(equalTo(newValues[i])));
        }
    }

    @Test
    public void testManyPutsAndRemovesOnMaps() {
        testSomePutsAndRemoves(this.defaultHashMap);
        testSomePutsAndRemoves(this.nonStandardHasherMap);
    }

    protected void testManyPutsAndRemoves(GeneralHashMap<Integer, Object> map) {
        Object[] oldValues = new Object[1000];
        for (int i = 0; i < oldValues.length; i++) {
            oldValues[i] = "old" + i;
        }

        for (int i = 0; i < oldValues.length; i++) {
            assertThat(map.put(i, oldValues[i]), is(nullValue()));
        }

        Object[] newValues = new Object[8 * oldValues.length];
        for (int i = 0; i < newValues.length; i++) {
            newValues[i] = "new" + i;
        }

        for (int i = 0; i < oldValues.length; i++) {
            assertThat(map.put(i, newValues[i]), is(equalTo(oldValues[i])));
        }
        for (int i = oldValues.length; i < newValues.length; i++) {
            assertThat(map.put(i, newValues[i]), is(nullValue()));
        }

        for (int i = 0; i < newValues.length; i++) {
            assertThat(map.remove(i), is(equalTo(newValues[i])));
        }
    }

    @Test
    public void testManyPutsAndRemovesOnModulo100HasherMap() {
        GeneralHashMap<Integer, Object> map = this.modulo100HasherMap;

        Object[] oldValues = new Object[10 * 100];
        for (int i = 0; i < oldValues.length; i++) {
            oldValues[i] = "old" + i;
        }

        for (int i = 0; i < 100; i++) {
            assertThat(map.put(i, oldValues[i]), is(nullValue()));
        }
        for (int factor = 1; factor < 10; factor++) {
            for (int i = 0; i < 100; i++) {
                assertThat(map.put(factor * 100 + i, oldValues[factor * 100 + i]), is(oldValues[(factor - 1) * 100 + i]));
            }
        }

        Object[] newValues = new Object[10 * 100];
        for (int i = 0; i < oldValues.length; i++) {
            newValues[i] = "new" + i;
        }

        for (int i = 0; i < 100; i++) {
            assertThat(map.put(i, newValues[i]), is(equalTo(oldValues[9 * 100 + i])));
        }
        for (int factor = 1; factor < 10; factor++) {
            for (int i = 0; i < 100; i++) {
                assertThat(map.put(factor * 100 + i, newValues[factor * 100 + i]), is(newValues[(factor - 1) * 100 + i]));
            }
        }
    }

    @Test
    public void testIterationOnMaps() {
        testIteration(this.defaultHashMap);
        testIteration(this.nonStandardHasherMap);
        testIteration(this.modulo100HasherMap);
    }

    public void testIteration(Map<Integer, Object> map) {
        Object[] values = new Object[100];
        for (int i = 0; i < values.length; i++) {
            values[i] = "val" + i;
            map.put(i, values[i]);
        }

        Object[] iterated = new Object[values.length];

        Iterator<? extends Map.Entry<Integer, Object>> entryIt = map.entrySet().iterator();
        Iterator<Integer> keyIt = map.keySet().iterator();
        Iterator<Object> valueIt = map.values().iterator();

        for (int i = 0; i < values.length; i++) {
            assertThat(entryIt.hasNext(), is(true));
            assertThat(keyIt.hasNext(), is(true));
            assertThat(valueIt.hasNext(), is(true));

            Map.Entry<Integer, Object> entry = entryIt.next();
            Integer key = keyIt.next();
            Object value = valueIt.next();

            assertThat(iterated[key], is(nullValue()));
            assertThat(entry.getKey(), is(equalTo(key)));
            assertThat(entry.getValue(), is(equalTo(value)));
            iterated[key] = value;
        }

        assertThat(entryIt.hasNext(), is(false));
        assertThat(keyIt.hasNext(), is(false));
        assertThat(valueIt.hasNext(), is(false));

        assertThat(iterated, is(arrayContaining(values)));
    }

}
