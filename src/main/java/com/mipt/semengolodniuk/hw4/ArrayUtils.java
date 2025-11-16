package com.mipt.semengolodniuk.hw4;

public final class ArrayUtils {
    private ArrayUtils() {}

    public static <T> int indexOf(T[] array, T value) {
        if (array == null || array.length == 0) return -1;
        for (int i = 0; i < array.length; i++) {
            if (value == null ? array[i] == null : value.equals(array[i])) return i;
        }
        return -1;
    }
}
