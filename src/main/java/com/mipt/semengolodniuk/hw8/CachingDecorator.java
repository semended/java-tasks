package com.mipt.semengolodniuk.hw8;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CachingDecorator implements DataService {
    private final DataService original;
    private final Map<String, Optional<String>> cache = new HashMap<>();

    public CachingDecorator(DataService original) {
        this.original = original;
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        Optional<String> res = original.findDataByKey(key);
        cache.put(key, res);
        return res;
    }

    @Override
    public void saveData(String key, String data) {
        original.saveData(key, data);
        cache.put(key, Optional.ofNullable(data));
    }

    @Override
    public boolean deleteData(String key) {
        boolean ok = original.deleteData(key);
        cache.remove(key);
        return ok;
    }
}
