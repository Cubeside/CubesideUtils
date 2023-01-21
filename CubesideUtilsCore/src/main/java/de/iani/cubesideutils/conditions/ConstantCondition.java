package de.iani.cubesideutils.conditions;

public class ConstantCondition implements Condition<Object> {

    public static final String SERIALIZATION_TYPE = "ConstantCondition";

    public static final ConstantCondition TRUE = new ConstantCondition(true);
    public static final ConstantCondition FALSE = new ConstantCondition(false);

    public static ConstantCondition deserialize(String serialized) {
        if ("true".equals(serialized)) {
            return TRUE;
        }
        if ("false".equals(serialized)) {
            return FALSE;
        }
        throw new IllegalArgumentException();
    }

    private final boolean value;

    private ConstantCondition(boolean value) {
        this.value = value;
    }

    @Override
    public boolean test(Object t) {
        return value;
    }

    @Override
    public String getSerializationType() {
        return SERIALIZATION_TYPE;
    }

    @Override
    public String serializeToString() {
        return String.valueOf(value);
    }

}
