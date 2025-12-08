package com.mipt.semengolodniuk.hw8;

import java.util.Optional;

public class LoggingDecorator implements DataService {
    private final DataService original;

    public LoggingDecorator(DataService original) {
        this.original = original;
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        System.out.println("findDataByKey: key=" + key);
        return original.findDataByKey(key);
    }

    @Override
    public void saveData(String key, String data) {
        System.out.println("saveData: key=" + key);
        original.saveData(key, data);
    }

    @Override
    public boolean deleteData(String key) {
        System.out.println("deleteData: key=" + key);
        return original.deleteData(key);
    }
}
