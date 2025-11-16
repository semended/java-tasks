package com.mipt.semengolodniuk.hw4;

public class Calculator<T extends Number> {
    public double sum(T a, T b) {
        return (a == null || b == null) ? Double.NaN : a.doubleValue() + b.doubleValue();
    }

    public double subtract(T a, T b) {
        return (a == null || b == null) ? Double.NaN : a.doubleValue() - b.doubleValue();
    }

    public double multiply(T a, T b) {
        return (a == null || b == null) ? Double.NaN : a.doubleValue() * b.doubleValue();
    }

    public double divide(T a, T b) {
        if (a == null || b == null) return Double.NaN;
        double denom = b.doubleValue();
        return denom == 0.0 ? Double.NaN : a.doubleValue() / denom;
    }
}
