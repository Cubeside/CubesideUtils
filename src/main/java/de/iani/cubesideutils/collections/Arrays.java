package de.iani.cubesideutils.collections;

import java.util.Objects;
import java.util.Random;

import com.google.common.base.Preconditions;

public class Arrays {
    private Arrays() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    /**
     * Shuffles a part of an int array. Only elements from start (including) to end (excluding) are shuffeled.
     *
     * @param array
     *            the array to shuffle. may not be null.
     * @param rnd
     *            the random generator to use. may not be null.
     * @param start
     *            the first element to shuffle.
     * @param end
     *            the element after the last element to shuffle.
     */
    public static void shuffle(int[] array, Random rnd, int start, int end) {
        Preconditions.checkArgument(start >= 0, "start >= 0");
        Preconditions.checkArgument(end <= array.length, "end <= array.length");
        Preconditions.checkArgument(start <= end, "start <= end");
        for (int i = end; i > start + 1; i--) {
            swap(array, i - 1, rnd.nextInt(i - start) + start);
        }
    }

    /**
     * Shuffles an int array.
     *
     * @param array
     *            the array to shuffle. may not be null.
     * @param rnd
     *            the random generator to use. may not be null.
     */
    public static void shuffle(int[] array, Random rnd) {
        int size = array.length;
        for (int i = size; i > 1; i--) {
            swap(array, i - 1, rnd.nextInt(i));
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    @SafeVarargs
    public static <T> int indexOf(T element, T... array) {
        for (int i=0; i<array.length; i++) {
            if (Objects.equals(element, array[i])) {
                return i;
            }
        }
        return -1;
    }
}
