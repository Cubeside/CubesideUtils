package de.iani.cubesideutils.conditions;

import de.iani.cubesideutils.serialization.StringSerialization;
import java.util.Objects;

public class NegatedCondition<T> implements Condition<T> {

    public static final String SERIALIZATION_TYPE = "NegatedCondition";

    private static final char SEPERATION_CHAR = '#';

    public static <T> NegatedCondition<T> deserialize(String serialized) {
        String originalType = serialized.substring(0, serialized.indexOf(SEPERATION_CHAR));
        String originalSerialized = serialized.substring(serialized.indexOf(SEPERATION_CHAR) + 1);
        return new NegatedCondition<>(StringSerialization.deserialize(originalType, originalSerialized));
    }

    public static <T> Condition<T> negate(Condition<T> original) {
        if (original instanceof NegatedCondition) {
            return ((NegatedCondition<T>) original).original;
        }
        return new NegatedCondition<>(original);
    }

    private Condition<T> original;

    private NegatedCondition(Condition<T> original) {
        this.original = Objects.requireNonNull(original);
    }

    @Override
    public boolean test(T t) {
        return !original.test(t);
    }

    @Override
    public String getSerializationType() {
        return SERIALIZATION_TYPE;
    }

    @Override
    public String serializeToString() {
        return original.getSerializationType() + SEPERATION_CHAR + original.serializeToString();
    }

}
