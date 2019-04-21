package de.iani.cubesideutils;

import java.util.Objects;
import java.util.function.Predicate;

public class FunctionUtil {
    private FunctionUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    private static class NegatedPredicate<T> implements Predicate<T> {
        private final Predicate<T> original;

        public NegatedPredicate(Predicate<T> original) {
            this.original = Objects.requireNonNull(original);
        }

        @Override
        public boolean test(T t) {
            return !this.original.test(t);
        }

    }

    public static <T> Predicate<T> negate(Predicate<T> predicate) {
        return (predicate instanceof NegatedPredicate<?>) ? ((NegatedPredicate<T>) predicate).original : new NegatedPredicate<>(predicate);
    }
}
