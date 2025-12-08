package com.mipt.semengolodniuk.hw10io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;

public class TextFileAnalyzerTest {

    @Test
    public void testAnalyzeFileCountsAndFrequency() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        Path f = Files.createTempFile("test", ".txt");
        String content = "Hello world!\nThis is test.";
        Files.write(f, content.getBytes(StandardCharsets.UTF_8));

        AnalysisResult r = analyzer.analyzeFile(f.toString());

        assertEquals(2, r.getLineCount());
        assertEquals(5, r.getWordCount());
        assertEquals(content.length(), r.getCharCount());

        assertEquals(Long.valueOf(3L), r.getCharFrequency().get(' '));
        assertEquals(Long.valueOf(1L), r.getCharFrequency().get('\n'));
        assertEquals(Long.valueOf(1L), r.getCharFrequency().get('!'));
    }

    @Test
    public void testSaveAnalysisResult() throws IOException {
        TextFileAnalyzer analyzer = new TextFileAnalyzer();

        Path f = Files.createTempFile("src", ".txt");
        String content = "a a\nbb";
        Files.write(f, content.getBytes(StandardCharsets.UTF_8));

        AnalysisResult r = analyzer.analyzeFile(f.toString());

        Path out = Files.createTempFile("analysis", ".txt");
        analyzer.saveAnalysisResult(r, out.toString());

        assertTrue(Files.exists(out));
        assertTrue(Files.size(out) > 0);

        String saved = Files.readString(out, StandardCharsets.UTF_8);
        assertTrue(saved.contains("Lines: " + r.getLineCount()));
        assertTrue(saved.contains("Words: " + r.getWordCount()));
        assertTrue(saved.contains("Chars: " + r.getCharCount()));
        assertTrue(saved.contains("Char frequency:"));
    }
}
