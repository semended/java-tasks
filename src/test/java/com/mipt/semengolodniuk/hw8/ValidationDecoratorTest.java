package com.mipt.semengolodniuk.hw8;

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import org.junit.Test;

public class ValidationDecoratorTest {

    @Test(expected = IllegalArgumentException.class)
    public void findRejectsNullKey() {
        DataService svc = new ValidationDecorator(new SimpleDataService());
        svc.findDataByKey(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveRejectsBlankKey() {
        DataService svc = new ValidationDecorator(new SimpleDataService());
        svc.saveData("   ", "v");
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveRejectsNullData() {
        DataService svc = new ValidationDecorator(new SimpleDataService());
        svc.saveData("k", null);
    }

    @Test
    public void passesValidData() {
        DataService svc = new ValidationDecorator(new SimpleDataService());
        svc.saveData("k", "v");
        Optional<String> v = svc.findDataByKey("k");
        assertEquals(Optional.of("v"), v);
    }
}
