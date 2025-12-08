package com.mipt.semengolodniuk.hw10io;

public final class WordCounter {
    private boolean inWord;

    public long acceptChar(char c) {
        boolean ws = Character.isWhitespace(c);
        if (ws) {
            inWord = false;
            return 0;
        }
        if (!inWord) {
            inWord = true;
            return 1;
        }
        return 0;
    }

    public void reset() {
        inWord = false;
    }
}
