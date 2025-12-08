package com.mipt.semengolodniuk.hw10io;

import java.util.Map;

public final class TextStatistics {
    private long lineCount;
    private long wordCount;
    private long charCount;

    private char lastChar = 0;
    private boolean hasAnyChar;

    private final WordCounter wordCounter = new WordCounter();
    private final CharFrequencyAnalyzer freqAnalyzer = new CharFrequencyAnalyzer();

    public void acceptChar(char c) {
        hasAnyChar = true;
        lastChar = c;

        charCount++;
        freqAnalyzer.acceptChar(c);
        wordCount += wordCounter.acceptChar(c);

        if (c == '\n') {
            lineCount++;
            // wordCounter сам сбрасывается на whitespace
        }
    }

    public AnalysisResult toResult() {
        long lines = lineCount;
        if (hasAnyChar && lastChar != '\n') {
            lines++;
        }
        Map<Character, Long> freq = freqAnalyzer.snapshot();
        return new AnalysisResult(lines, wordCount, charCount, freq);
    }
}
