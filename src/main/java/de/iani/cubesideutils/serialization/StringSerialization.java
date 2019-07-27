package de.iani.cubesideutils.serialization;

import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.collections.GeneralHashMap;
import java.util.Map;
import java.util.function.Function;

public class StringSerialization {

    public static final int MAX_TYPE_NAME_LENGTH = 64;
    private static Map<String, Function<String, StringSerializable>> serializationTypes;

    static {
        serializationTypes = new GeneralHashMap<>(StringUtil.CASE_IGNORING_HASHER, StringUtil.CASE_IGNORING_EQUALITY);
        register(GlobalLocationWrapper.SERIALIZATION_TYPE, GlobalLocationWrapper::deserialize);
    }

    public static void register(String serializationType, Function<String, StringSerializable> deserializer) {
        if (serializationType.length() > MAX_TYPE_NAME_LENGTH) {
            throw new IllegalArgumentException("Name of serialization type is too long.");
        }
        Function<String, StringSerializable> old = serializationTypes.putIfAbsent(serializationType, deserializer);
        if (old != null) {
            throw new IllegalArgumentException("This serializationType is already registered!");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends StringSerializable> T deserialize(String serializationType, String serialized) {
        Function<String, StringSerializable> deserializer = serializationTypes.get(serializationType);
        if (deserializer == null) {
            throw new IllegalArgumentException("Unknown serializationType " + serializationType + ".");
        }
        return (T) deserializer.apply(serializationType);
    }

    private StringSerialization() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevent instances
    }

}
