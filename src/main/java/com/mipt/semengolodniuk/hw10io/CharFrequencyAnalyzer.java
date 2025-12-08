package com.mipt.semengolodniuk.hw10io;

import java.util.HashMap;
import java.util.Map;

public final class CharFrequencyAnalyzer {
    private final Map<Character, Long> freq = new HashMap<>();

    public void acceptChar(char c) {
        freq.put(c, freq.getOrDefault(c, 0L) + 1L);
    }

    public Map<Character, Long> snapshot() {
        return new HashMap<>(freq);
    }
}
