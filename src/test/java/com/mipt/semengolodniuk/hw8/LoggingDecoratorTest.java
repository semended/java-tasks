package com.mipt.semengolodniuk.hw8;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.Test;

public class LoggingDecoratorTest {

    @Test
    public void logsAllOperations() {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(buf, true, StandardCharsets.UTF_8));

        try {
            DataService base = new SimpleDataService();
            DataService svc = new LoggingDecorator(base);

            svc.saveData("k", "v");
            Optional<String> v = svc.findDataByKey("k");
            svc.deleteData("k");

            String out = buf.toString(StandardCharsets.UTF_8);
            assertTrue(v.isPresent());
            assertTrue(out.contains("saveData: key=k"));
            assertTrue(out.contains("findDataByKey: key=k"));
            assertTrue(out.contains("deleteData: key=k"));
        } finally {
            System.setOut(old);
        }
    }
}
