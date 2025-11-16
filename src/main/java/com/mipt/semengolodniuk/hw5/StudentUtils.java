package com.mipt.semengolodniuk.hw5;

import java.util.*;
import java.util.stream.Collectors;

public final class StudentUtils {
    private StudentUtils() {}

    public static List<Student> topByAge(Collection<Student> input, int limit) {
        return input.stream()
                .sorted(Comparator.comparingInt(Student::age).reversed().thenComparing(Student::name))
                .limit(Math.max(0, limit))
                .collect(Collectors.toList());
    }

    public static Map<String, List<Student>> groupByGroup(Collection<Student> input) {
        return input.stream().collect(Collectors.groupingBy(Student::group, LinkedHashMap::new, Collectors.toList()));
    }
}
