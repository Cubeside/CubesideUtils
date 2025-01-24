package de.iani.cubesideutils.reflection;

import de.iani.cubesideutils.Pair;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PublisStaticFieldMapLoader {
    private static final ConcurrentHashMap<Pair<Class<?>, Class<?>>, Map<String, Object>> cachedFields = new ConcurrentHashMap<>();

    public static <T> Map<String, T> getFields(Class<T> searchInAndfieldClass) {
        return getFields(searchInAndfieldClass, searchInAndfieldClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getFields(Class<?> searchInClass, Class<T> fieldClass) {
        Pair<Class<?>, Class<?>> typePair = new Pair<>(searchInClass, fieldClass);
        Map<String, Object> result = cachedFields.get(typePair);
        if (result == null) {
            result = new HashMap<>();
            for (Field f : searchInClass.getDeclaredFields()) {
                int modifiers = f.getModifiers();
                if (Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && fieldClass.isAssignableFrom(f.getType())) {
                    try {
                        Object value = f.get(null);
                        if (value != null) {
                            String name = f.getName();
                            result.put(name, value);
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new RuntimeException("Could not access field " + f.getName(), e);
                    }
                }
            }
            result = Collections.unmodifiableMap(result);
            cachedFields.put(typePair, result);
        }
        return (Map<String, T>) result;
    }
}
