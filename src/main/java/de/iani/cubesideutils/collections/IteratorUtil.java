package de.iani.cubesideutils.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteratorUtil {
    private IteratorUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static <T> T first(Iterable<T> of) {
        return of.iterator().next();
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> concat(Iterable<? extends T>... iterables) {
        return () -> {
            return new Iterator<T>() {

                private Iterator<T>[] iterators = Arrays.stream(iterables).map(iterable -> iterable.iterator()).toArray(i -> new Iterator[iterables.length]);
                private int index = 0;
                private int incremented = 0;

                @Override
                public boolean hasNext() {
                    if (this.index >= this.iterators.length) {
                        return false;
                    }
                    if (this.iterators[this.index].hasNext()) {
                        return true;
                    } else {
                        this.index++;
                        this.incremented++;
                        return hasNext();
                    }
                }

                @Override
                public T next() {
                    try {
                        T result = this.iterators[this.index].next();
                        this.incremented = 0;
                        return result;
                    } catch (NoSuchElementException e) {
                        if (hasNext()) {
                            return next();
                        } else {
                            throw e;
                        }
                    }
                }

                @Override
                public void remove() {
                    int index = this.index - this.incremented;
                    if (index >= this.iterators.length) {
                        throw new IllegalStateException();
                    }
                    iterators[index].remove();
                }

            };
        };
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> concatUnmodifiable(Iterable<? extends T>... iterables) {
        return () -> {
            return new Iterator<T>() {

                private Iterator<T>[] iterators = Arrays.stream(iterables).map(iterable -> iterable.iterator()).toArray(i -> new Iterator[iterables.length]);
                private int index = 0;

                @Override
                public boolean hasNext() {
                    if (this.index >= this.iterators.length) {
                        return false;
                    }
                    if (this.iterators[this.index].hasNext()) {
                        return true;
                    } else {
                        this.index++;
                        return hasNext();
                    }
                }

                @Override
                public T next() {
                    try {
                        return this.iterators[this.index].next();
                    } catch (NoSuchElementException e) {
                        if (hasNext()) {
                            return next();
                        } else {
                            throw e;
                        }
                    }
                }

            };
        };
    }
}
