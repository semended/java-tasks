package com.mipt.semengolodniuk.hw6;

public record ValidationError(String field, String message) {
    @Override public String toString() { return field + ": " + message; }
}
