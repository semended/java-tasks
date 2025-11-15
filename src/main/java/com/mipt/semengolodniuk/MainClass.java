package com.mipt.semengolodniuk;

public class MainClass {
    private int count;
    private String text;
    protected static double ratio;
    public final long seed = 1L;

    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            System.out.println("Step " + i);
        }
    }
}
