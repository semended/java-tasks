package com.mipt.semengolodniuk.hw10io;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class AnalysisResult {
    private final long lineCount;
    private final long wordCount;
    private final long charCount;
    private final Map<Character, Long> charFrequency;

    public AnalysisResult(long lineCount, long wordCount, long charCount, Map<Character, Long> charFrequency) {
        this.lineCount = lineCount;
        this.wordCount = wordCount;
        this.charCount = charCount;
        this.charFrequency = Collections.unmodifiableMap(new TreeMap<>(charFrequency));
    }

    public long getLineCount() {
        return lineCount;
    }

    public long getWordCount() {
        return wordCount;
    }

    public long getCharCount() {
        return charCount;
    }

    public Map<Character, Long> getCharFrequency() {
        return charFrequency;
    }

    @Override
    public String toString() {
        return "AnalysisResult{"
                + "lineCount=" + lineCount
                + ", wordCount=" + wordCount
                + ", charCount=" + charCount
                + ", charFrequencySize=" + charFrequency.size()
                + '}';
    }
}
