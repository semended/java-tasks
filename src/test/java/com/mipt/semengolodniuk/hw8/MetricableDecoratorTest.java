package com.mipt.semengolodniuk.hw8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Duration;
import java.util.Optional;
import org.junit.Test;

public class MetricableDecoratorTest {

    @Test
    public void sendsMetricForEachMethod() {
        CapturingMetricService ms = new CapturingMetricService();
        DataService base = new SimpleDataService();
        DataService svc = new MetricableDecorator(base, ms);

        svc.saveData("k", "v");
        Optional<String> v = svc.findDataByKey("k");
        svc.deleteData("k");

        assertEquals(Optional.of("v"), v);
        assertEquals(3, ms.calls);
        assertNotNull(ms.lastDuration);
    }

    private static final class CapturingMetricService extends MetricableDecorator.MetricService {
        int calls;
        Duration lastDuration;

        @Override
        public void sendMetric(Duration duration) {
            calls++;
            lastDuration = duration;
        }
    }
}
