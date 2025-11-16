package com.mipt.semengolodniuk.hw4;

import java.util.ArrayList;
import java.util.List;

public final class CollectionUtils {
    private CollectionUtils() {}

    public static <T> void addAll(List<? super T> dest, List<? extends T> src) {
        if (dest == null || src == null) return;
        for (T t : src) dest.add(t);
    }

    public static <T> List<T> combine(List<? extends T> a, List<? extends T> b) {
        List<T> out = new ArrayList<>();
        if (a != null) out.addAll(a);
        if (b != null) out.addAll(b);
        return out;
    }
}
