package com.mipt.semengolodniuk.hw8;

import java.util.Optional;

public class ValidationDecorator implements DataService {
    private final DataService original;

    public ValidationDecorator(DataService original) {
        this.original = original;
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        validateKey(key);
        return original.findDataByKey(key);
    }

    @Override
    public void saveData(String key, String data) {
        validateKey(key);
        if (data == null) {
            throw new IllegalArgumentException("data must not be null");
        }
        original.saveData(key, data);
    }

    @Override
    public boolean deleteData(String key) {
        validateKey(key);
        return original.deleteData(key);
    }

    private static void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be null/blank");
        }
    }
}
