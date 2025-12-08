package com.mipt.semengolodniuk.hw10io;

import java.util.Map;

public final class AnalysisResultFormatter {

    public String format(AnalysisResult r) {
        StringBuilder sb = new StringBuilder();
        sb.append("Lines: ").append(r.getLineCount()).append('\n');
        sb.append("Words: ").append(r.getWordCount()).append('\n');
        sb.append("Chars: ").append(r.getCharCount()).append('\n');
        sb.append('\n');
        sb.append("Char frequency:").append('\n');

        for (Map.Entry<Character, Long> e : r.getCharFrequency().entrySet()) {
            sb.append(formatChar(e.getKey())).append(" -> ").append(e.getValue()).append('\n');
        }
        return sb.toString();
    }

    public String formatChar(char c) {
        if (c == ' ') return "' '";
        if (c == '\t') return "'\\t'";
        if (c == '\r') return "'\\r'";
        if (c == '\n') return "'\\n'";
        return "'" + c + "'";
    }
}
