package com.mipt.semengolodniuk.hw5;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollectionPerformanceTester {
    public static void main(String[] args) {
        int n = 200_000;
        long a1 = timeAdd(new ArrayList<>(), n);
        long a2 = timeAdd(new LinkedList<>(), n);
        System.out.println("ArrayList add: " + a1 + " ms");
        System.out.println("LinkedList add: " + a2 + " ms");
    }

    private static long timeAdd(List<Integer> list, int n) {
        long t = System.currentTimeMillis();
        for (int i = 0; i < n; i++) list.add(i);
        return System.currentTimeMillis() - t;
    }
}
