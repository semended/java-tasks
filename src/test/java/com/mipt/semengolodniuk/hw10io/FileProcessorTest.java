package com.mipt.semengolodniuk.hw10io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import org.junit.Test;

public class FileProcessorTest {

    @Test
    public void testSplitAndMergeFile() throws IOException {
        FileProcessor processor = new FileProcessor();

        Path testFile = Files.createTempFile("test", ".dat");
        byte[] testData = new byte[1500];
        new Random(123).nextBytes(testData);
        Files.write(testFile, testData);

        Path outputDir = Files.createTempDirectory("parts");
        List<Path> parts = processor.splitFile(testFile.toString(), outputDir.toString(), 500);

        assertEquals(3, parts.size());
        for (int i = 0; i < parts.size(); i++) {
            Path p = parts.get(i);
            assertTrue(Files.exists(p));
            assertEquals(500, Files.size(p));
            assertTrue(p.getFileName().toString().contains(".part" + (i + 1)));
        }

        Path mergedFile = Files.createTempFile("merged", ".dat");
        processor.mergeFiles(parts, mergedFile.toString());

        assertArrayEquals(Files.readAllBytes(testFile), Files.readAllBytes(mergedFile));
    }
}
