package com.mipt.semengolodniuk.hw10io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public final class AnalysisResultWriter {
    private final AnalysisResultFormatter formatter = new AnalysisResultFormatter();

    public void writeToFile(AnalysisResult result, String outputPath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
            bw.write(formatter.format(result));
        }
    }
}
