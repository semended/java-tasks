package com.mipt.semengolodniuk.hw10io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TextFileAnalyzer {

    private final AnalysisResultWriter writer = new AnalysisResultWriter();

    public AnalysisResult analyzeFile(String filePath) throws IOException {
        TextStatistics stats = new TextStatistics();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            char[] buf = new char[4096];
            int read;
            while ((read = br.read(buf)) != -1) {
                for (int i = 0; i < read; i++) {
                    stats.acceptChar(buf[i]);
                }
            }
        }

        return stats.toResult();
    }

    public void saveAnalysisResult(AnalysisResult result, String outputPath) throws IOException {
        writer.writeToFile(result, outputPath);
    }
}
