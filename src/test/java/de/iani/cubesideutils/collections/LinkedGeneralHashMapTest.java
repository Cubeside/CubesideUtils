package de.iani.cubesideutils.collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class LinkedGeneralHashMapTest extends GeneralHashMapTest {

    public LinkedGeneralHashMapTest() {
        super();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void init() {
        this.initMaps((hasher, equality) -> new LinkedGeneralHashMap(hasher, equality));
    }

    @Test
    public void testIterationOrderOnMaps() {
        testIterationOrder(this.defaultHashMap);
        testIterationOrder(this.nonStandardHasherMap);
        testIterationOrder(this.modulo100HasherMap);
    }

    public void testIterationOrder(Map<Integer, Object> map) {
        Integer[] keys = new Integer[100];
        Object[] values = new Object[keys.length];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = i;
        }

        Collections.shuffle(Arrays.asList(keys), new Random(102938475));
        for (int i = 0; i < keys.length; i++) {
            values[i] = "val" + keys[i];
            map.put(keys[i], values[i]);
        }

        Iterator<? extends Map.Entry<Integer, Object>> entryIt = map.entrySet().iterator();
        Iterator<Integer> keyIt = map.keySet().iterator();
        Iterator<Object> valueIt = map.values().iterator();

        for (int i = 0; i < keys.length; i++) {
            assertThat(entryIt.hasNext(), is(true));
            assertThat(keyIt.hasNext(), is(true));
            assertThat(valueIt.hasNext(), is(true));

            Map.Entry<Integer, Object> entry = entryIt.next();
            Integer key = keyIt.next();
            Object value = valueIt.next();

            assertThat(entry.getKey(), is(equalTo(keys[i])));
            assertThat(entry.getValue(), is(equalTo(values[i])));
            assertThat(key, is(equalTo(keys[i])));
            assertThat(value, is(equalTo(values[i])));
        }

        assertThat(entryIt.hasNext(), is(false));
        assertThat(keyIt.hasNext(), is(false));
        assertThat(valueIt.hasNext(), is(false));
    }

}
