package de.iani.cubesideutils.conditions;

import de.iani.cubesideutils.serialization.StringSerialization;
import java.util.Objects;

public class BinaryCombinedCondition<T> implements Condition<T> {

    public static enum BinaryBooleanOperation {
        AND {
            @Override
            public <T> boolean apply(T t, Condition<? super T> first, Condition<? super T> second) {
                return first.test(t) && second.test(t);
            }
        },
        OR {
            @Override
            public <T> boolean apply(T t, Condition<? super T> first, Condition<? super T> second) {
                return first.test(t) || second.test(t);
            }
        },
        XOR {
            @Override
            public <T> boolean apply(T t, Condition<? super T> first, Condition<? super T> second) {
                return first.test(t) ^ second.test(t);
            }
        },
        NAND {
            @Override
            public <T> boolean apply(T t, Condition<? super T> first, Condition<? super T> second) {
                return !(first.test(t) && second.test(t));
            }
        },
        NOR {
            @Override
            public <T> boolean apply(T t, Condition<? super T> first, Condition<? super T> second) {
                return !(first.test(t) || second.test(t));
            }
        },
        XNOR {
            @Override
            public <T> boolean apply(T t, Condition<? super T> first, Condition<? super T> second) {
                return !(first.test(t) ^ second.test(t));
            }
        },
        IMPLY {
            @Override
            public <T> boolean apply(T t, Condition<? super T> first, Condition<? super T> second) {
                return !first.test(t) || second.test(t);
            }
        };

        public abstract <T> boolean apply(T t, Condition<? super T> first, Condition<? super T> second);
    }

    public static final String SERIALIZATION_TYPE = "BinaryCombinedCondition";

    private static final char SEPERATION_CHAR = '#';

    public static <T> BinaryCombinedCondition<T> deserialize(String serialized) {
        String operationString = null;
        String firstConditionString = null;
        String secondConditionString = null;

        int i = 0;
        for (; i < serialized.length(); i++) {
            if (serialized.charAt(i) == SEPERATION_CHAR) {
                operationString = serialized.substring(0, i);
                i++;
                break;
            }
        }

        int j = i;
        int parenthesis = 0;
        for (; i < serialized.length(); i++) {
            switch (serialized.charAt(i)) {
                case '(':
                    parenthesis++;
                    continue;
                case ')':
                    parenthesis--;
                    continue;
                case SEPERATION_CHAR:
                    if (parenthesis != 0) {
                        continue;
                    }
                    firstConditionString = serialized.substring(j + 1, i - 1);
                    j = i + 1;
            }
        }

        if (parenthesis == 0) {
            secondConditionString = serialized.substring(j + 1, i - 1);
        }

        if (operationString == null || firstConditionString == null || secondConditionString == null) {
            throw new IllegalArgumentException("invalid syntax");
        }

        BinaryBooleanOperation operation = BinaryBooleanOperation.valueOf(operationString);

        String firstConditionType = firstConditionString.substring(0, firstConditionString.indexOf(SEPERATION_CHAR));
        String firstConditionSerialized = firstConditionString.substring(firstConditionString.indexOf(SEPERATION_CHAR) + 1);
        Condition<? super T> first = StringSerialization.deserialize(firstConditionType, firstConditionSerialized);

        String secondConditionType = secondConditionString.substring(0, secondConditionString.indexOf(SEPERATION_CHAR));
        String secondConditionSerialized = secondConditionString.substring(secondConditionString.indexOf(SEPERATION_CHAR) + 1);
        Condition<? super T> second = StringSerialization.deserialize(secondConditionType, secondConditionSerialized);

        return new BinaryCombinedCondition<>(operation, first, second);
    }

    private BinaryBooleanOperation operation;
    private Condition<? super T> first;
    private Condition<? super T> second;

    public BinaryCombinedCondition(BinaryBooleanOperation operation, Condition<? super T> first, Condition<? super T> second) {
        this.operation = Objects.requireNonNull(operation);
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }

    @Override
    public boolean test(T t) {
        return operation.apply(t, first, second);
    }

    @Override
    public String getSerializationType() {
        return SERIALIZATION_TYPE;
    }

    @Override
    public String serializeToString() {
        return operation.name() + SEPERATION_CHAR + '(' + first.getSerializationType() + SEPERATION_CHAR + first.serializeToString() + ')' + SEPERATION_CHAR + '(' + second.getSerializationType() + SEPERATION_CHAR + second.serializeToString() + ')';
    }

}
