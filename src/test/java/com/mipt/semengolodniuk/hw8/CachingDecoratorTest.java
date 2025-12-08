package com.mipt.semengolodniuk.hw8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;

public class CachingDecoratorTest {

    @Test
    public void cachesFindResults() {
        CountingDataService base = new CountingDataService();
        base.saveData("k", "v");

        DataService svc = new CachingDecorator(base);

        Optional<String> a = svc.findDataByKey("k");
        Optional<String> b = svc.findDataByKey("k");

        assertEquals(Optional.of("v"), a);
        assertEquals(Optional.of("v"), b);
        assertEquals(1, base.findCalls);
    }

    @Test
    public void updatesCacheOnSave() {
        CountingDataService base = new CountingDataService();
        DataService svc = new CachingDecorator(base);

        svc.saveData("k", "v");
        Optional<String> res = svc.findDataByKey("k");

        assertEquals(Optional.of("v"), res);
        assertEquals(0, base.findCalls);
        assertEquals(1, base.saveCalls);
    }

    @Test
    public void invalidatesCacheOnDelete() {
        CountingDataService base = new CountingDataService();
        base.saveData("k", "v");

        DataService svc = new CachingDecorator(base);

        assertEquals(Optional.of("v"), svc.findDataByKey("k"));
        assertEquals(1, base.findCalls);

        assertTrue(svc.deleteData("k"));
        base.saveData("k", "v2");

        assertEquals(Optional.of("v2"), svc.findDataByKey("k"));
        assertEquals(2, base.findCalls);
    }

    private static final class CountingDataService implements DataService {
        private final Map<String, String> m = new HashMap<>();
        int findCalls;
        int saveCalls;

        @Override
        public Optional<String> findDataByKey(String key) {
            findCalls++;
            return Optional.ofNullable(m.get(key));
        }

        @Override
        public void saveData(String key, String data) {
            saveCalls++;
            m.put(key, data);
        }

        @Override
        public boolean deleteData(String key) {
            return m.remove(key) != null;
        }
    }
}
