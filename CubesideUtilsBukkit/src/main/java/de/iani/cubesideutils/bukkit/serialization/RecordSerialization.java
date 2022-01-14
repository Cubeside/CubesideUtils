package de.iani.cubesideutils.bukkit.serialization;

import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.serialization.StringSerializable;
import de.iani.cubesideutils.serialization.StringSerialization;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class RecordSerialization {

    public static interface ConfigurationSerializableRecord extends ConfigurationSerializable {

        @Override
        public default Map<String, Object> serialize() {
            return RecordSerialization.serialize((Record & ConfigurationSerializableRecord) this);
        }
    }

    public static interface StringSerializableRecord extends StringSerializable {

        @Override
        public default String serializeToString() {
            return RecordSerialization.serializeToString((Record & StringSerializableRecord) this);
        }
    }

    public static interface SerializableRecord extends ConfigurationSerializableRecord, StringSerializableRecord {

    }

    public static <T extends Record & ConfigurationSerializableRecord> Map<String, Object> serialize(T record) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (RecordComponent component : record.getClass().getRecordComponents()) {
            try {
                result.put(component.getName(), component.getAccessor().invoke(record));
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException("expected record component accessor methods to be present and public");
            } catch (InvocationTargetException e) {
                if (e.getCause()instanceof RuntimeException f) {
                    throw f;
                } else if (e.getCause()instanceof Error f) {
                    throw f;
                } else {
                    throw new RuntimeException("unexpected type of throwable", e.getCause());
                }
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Record & ConfigurationSerializableRecord> T deserialize(Class<T> recordClass, Map<String, Object> serialized) {
        RecordComponent[] components = recordClass.getRecordComponents();
        Object[] constructorArgs = new Object[components.length];
        for (int i = 0; i < components.length; i++) {
            constructorArgs[i] = serialized.get(components[i].getName());
        }

        try {
            return (T) recordClass.getConstructors()[0].newInstance(constructorArgs);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException("expected record constructor to be present and public");
        } catch (InvocationTargetException e) {
            if (e.getCause()instanceof RuntimeException f) {
                throw f;
            } else if (e.getCause()instanceof Error f) {
                throw f;
            } else {
                throw new RuntimeException("unexpected type of throwable", e.getCause());
            }
        }
    }

    public static <T extends Record & StringSerializableRecord> String serializeToString(T record) {
        StringBuilder result = new StringBuilder();
        for (RecordComponent component : record.getClass().getRecordComponents()) {
            if (!result.isEmpty()) {
                result.append(" ; ");
            }

            Object value;
            try {
                value = component.getAccessor().invoke(record);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException("expected record component accessor methods to be present and public");
            } catch (InvocationTargetException e) {
                if (e.getCause()instanceof RuntimeException f) {
                    throw f;
                } else if (e.getCause()instanceof Error f) {
                    throw f;
                } else {
                    throw new RuntimeException("unexpected type of throwable", e.getCause());
                }
            }

            Pair<String, String> serialized = StringSerialization.serialize(value);
            result.append(component.getName()).append(":");
            result.append(serialized.first()).append(":");
            result.append(escape(serialized.second()));
        }

        return result.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Record & StringSerializableRecord> T deserializeFromString(Class<T> recordClass, String serialized) {
        Pattern partSeperatorPattern = Pattern.compile(Pattern.quote(":"));
        Map<String, Object> componentValues = new HashMap<>();
        String[] compStrings = serialized.split(Pattern.quote(" ; "));

        for (String compString : compStrings) {
            String[] serializationParts = partSeperatorPattern.split(compString, 3);
            String compName = serializationParts[0];
            String compType = serializationParts[1];
            String compSerialized = unescape(serializationParts[2]);
            Object compValue = StringSerialization.deserialize(compType, compSerialized);
            componentValues.put(compName, compValue);
        }

        RecordComponent[] components = recordClass.getRecordComponents();
        Object[] constructorArgs = new Object[components.length];
        for (int i = 0; i < components.length; i++) {
            constructorArgs[i] = componentValues.get(components[i].getName());
        }

        try {
            return (T) recordClass.getConstructors()[0].newInstance(constructorArgs);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException("expected record constructor to be present and public");
        } catch (InvocationTargetException e) {
            if (e.getCause()instanceof RuntimeException f) {
                throw f;
            } else if (e.getCause()instanceof Error f) {
                throw f;
            } else {
                throw new RuntimeException("unexpected type of throwable", e.getCause());
            }
        }
    }

    private static String escape(String arg) {
        if (!arg.contains(";")) {
            return arg;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arg.length(); i++) {
            char c = arg.charAt(i);
            builder.append(c);
            if (c != ';') {
                continue;
            }
            for (; i < arg.length() - 1 && arg.charAt(i + 1) == ';'; i++) {
                builder.append(';');
            }
            builder.append(';');
        }

        return builder.toString();
    }

    private static String unescape(String arg) {
        if (!arg.contains(";")) {
            return arg;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arg.length() - 1; i++) {
            char c = arg.charAt(i);
            if (c != ';') {
                builder.append(c);
                continue;
            }
            for (; i < arg.length() - 1 && arg.charAt(i + 1) == ';'; i++) {
                builder.append(';');
            }
        }

        return builder.toString();
    }

}
