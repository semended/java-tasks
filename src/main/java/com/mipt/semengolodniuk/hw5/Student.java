package com.mipt.semengolodniuk.hw5;

import java.util.Objects;

public class Student {
    private final String name;
    private final int age;
    private final String group;

    public Student(String name, int age, String group) {
        this.name = name;
        this.age = age;
        this.group = group;
    }

    public String name() { return name; }
    public int age() { return age; }
    public String group() { return group; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student s = (Student) o;
        return age == s.age && Objects.equals(name, s.name) && Objects.equals(group, s.group);
    }

    public int hashCode() {
        return Objects.hash(name, age, group);
    }

    public String toString() {
        return name + "|" + age + "|" + group;
    }
}
