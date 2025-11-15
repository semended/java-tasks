package com.mipt.semengolodniuk;

public abstract class WorkingPerson {
    public abstract void work(int hours);
    public boolean isOne(String a, String b) {
        return a != null && a.equals(b);
    }
}
