package com.mipt.semengolodniuk.hw8;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class MetricableDecorator implements DataService {
    private final DataService original;
    private final MetricService metricService;

    public MetricableDecorator(DataService original) {
        this(original, new MetricService());
    }

    public MetricableDecorator(DataService original, MetricService metricService) {
        this.original = original;
        this.metricService = metricService;
    }

    @Override
    public Optional<String> findDataByKey(String key) {
        Instant start = Instant.now();
        try {
            return original.findDataByKey(key);
        } finally {
            metricService.sendMetric(Duration.between(start, Instant.now()));
        }
    }

    @Override
    public void saveData(String key, String data) {
        Instant start = Instant.now();
        try {
            original.saveData(key, data);
        } finally {
            metricService.sendMetric(Duration.between(start, Instant.now()));
        }
    }

    @Override
    public boolean deleteData(String key) {
        Instant start = Instant.now();
        try {
            return original.deleteData(key);
        } finally {
            metricService.sendMetric(Duration.between(start, Instant.now()));
        }
    }

    public static class MetricService {
        public void sendMetric(Duration duration) {
            System.out.println("Метод выполнялся: " + duration);
        }
    }
}
