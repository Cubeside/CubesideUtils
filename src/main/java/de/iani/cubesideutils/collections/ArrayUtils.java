package de.iani.cubesideutils.collections;

import java.util.Objects;
import java.util.Random;

import com.google.common.base.Preconditions;

public class ArrayUtils {
    private ArrayUtils() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static void shuffle(boolean[] array, Random rnd, int start, int end) {
        Preconditions.checkArgument(start >= 0, "start >= 0");
        Preconditions.checkArgument(end <= array.length, "end <= array.length");
        Preconditions.checkArgument(start <= end, "start <= end");
        for (int i = end; i > start + 1; i--) {
            swap(array, i - 1, rnd.nextInt(i - start) + start);
        }
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

    public static void shuffle(long[] array, Random rnd, int start, int end) {
        Preconditions.checkArgument(start >= 0, "start >= 0");
        Preconditions.checkArgument(end <= array.length, "end <= array.length");
        Preconditions.checkArgument(start <= end, "start <= end");
        for (int i = end; i > start + 1; i--) {
            swap(array, i - 1, rnd.nextInt(i - start) + start);
        }
    }

    public static void shuffle(char[] array, Random rnd, int start, int end) {
        Preconditions.checkArgument(start >= 0, "start >= 0");
        Preconditions.checkArgument(end <= array.length, "end <= array.length");
        Preconditions.checkArgument(start <= end, "start <= end");
        for (int i = end; i > start + 1; i--) {
            swap(array, i - 1, rnd.nextInt(i - start) + start);
        }
    }

    public static void shuffle(float[] array, Random rnd, int start, int end) {
        Preconditions.checkArgument(start >= 0, "start >= 0");
        Preconditions.checkArgument(end <= array.length, "end <= array.length");
        Preconditions.checkArgument(start <= end, "start <= end");
        for (int i = end; i > start + 1; i--) {
            swap(array, i - 1, rnd.nextInt(i - start) + start);
        }
    }

    public static void shuffle(double[] array, Random rnd, int start, int end) {
        Preconditions.checkArgument(start >= 0, "start >= 0");
        Preconditions.checkArgument(end <= array.length, "end <= array.length");
        Preconditions.checkArgument(start <= end, "start <= end");
        for (int i = end; i > start + 1; i--) {
            swap(array, i - 1, rnd.nextInt(i - start) + start);
        }
    }

    public static <T> void shuffle(T[] array, Random rnd, int start, int end) {
        Preconditions.checkArgument(start >= 0, "start >= 0");
        Preconditions.checkArgument(end <= array.length, "end <= array.length");
        Preconditions.checkArgument(start <= end, "start <= end");
        for (int i = end; i > start + 1; i--) {
            swap(array, i - 1, rnd.nextInt(i - start) + start);
        }
    }

    public static void shuffle(boolean[] array, Random rnd) {
        int size = array.length;
        for (int i = size; i > 1; i--) {
            swap(array, i - 1, rnd.nextInt(i));
        }
    }

    private static void swap(boolean[] arr, int i, int j) {
        boolean tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
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

    public static void shuffle(long[] array, Random rnd) {
        int size = array.length;
        for (int i = size; i > 1; i--) {
            swap(array, i - 1, rnd.nextInt(i));
        }
    }

    private static void swap(long[] arr, int i, int j) {
        long tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static void shuffle(char[] array, Random rnd) {
        int size = array.length;
        for (int i = size; i > 1; i--) {
            swap(array, i - 1, rnd.nextInt(i));
        }
    }

    private static void swap(char[] arr, int i, int j) {
        char tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static void shuffle(float[] array, Random rnd) {
        int size = array.length;
        for (int i = size; i > 1; i--) {
            swap(array, i - 1, rnd.nextInt(i));
        }
    }

    private static void swap(float[] arr, int i, int j) {
        float tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static void shuffle(double[] array, Random rnd) {
        int size = array.length;
        for (int i = size; i > 1; i--) {
            swap(array, i - 1, rnd.nextInt(i));
        }
    }

    private static void swap(double[] arr, int i, int j) {
        double tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static <T> void shuffle(T[] array, Random rnd) {
        int size = array.length;
        for (int i = size; i > 1; i--) {
            swap(array, i - 1, rnd.nextInt(i));
        }
    }

    private static <T> void swap(T[] arr, int i, int j) {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static void flip(boolean[] array) {
        for (int i=0; i<array.length/2; i++) {
            boolean b = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = b;
        }
    }

    public static void flip(int[] array) {
        for (int i=0; i<array.length/2; i++) {
            int j = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = j;
        }
    }

    public static void flip(long[] array) {
        for (int i=0; i<array.length/2; i++) {
            long l = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = l;
        }
    }

    public static void flip(char[] array) {
        for (int i=0; i<array.length/2; i++) {
            char c = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = c;
        }
    }

    public static void flip(float[] array) {
        for (int i=0; i<array.length/2; i++) {
            float f = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = f;
        }
    }

    public static void flip(double[] array) {
        for (int i=0; i<array.length/2; i++) {
            double d = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = d;
        }
    }

    public static <T> void flip(T[] array) {
        for (int i=0; i<array.length/2; i++) {
            T t = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = t;
        }
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
