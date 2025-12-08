package com.mipt.semengolodniuk.hw10io;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileProcessor {

    private final FileSplitter splitter = new FileSplitter();
    private final FileMerger merger = new FileMerger();

    public List<Path> splitFile(String sourcePath, String outputDir, int partSize) throws IOException {
        return splitter.split(Paths.get(sourcePath), Paths.get(outputDir), partSize);
    }

    public void mergeFiles(List<Path> partPaths, String outputPath) throws IOException {
        merger.merge(partPaths, Paths.get(outputPath));
    }
}
