package com.mipt.semengolodniuk.hw10io;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public final class FileMerger {

    public void merge(List<Path> parts, Path output) throws IOException {
        if (parts == null || parts.isEmpty()) {
            throw new IllegalArgumentException("partPaths is empty");
        }
        for (Path p : parts) {
            if (p == null || !Files.exists(p)) {
                throw new IOException("Part does not exist: " + p);
            }
        }

        if (output.getParent() != null) {
            Files.createDirectories(output.getParent());
        }

        try (FileChannel out = FileChannel.open(
                output,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            for (Path part : parts) {
                try (FileChannel in = FileChannel.open(part, StandardOpenOption.READ)) {
                    long pos = 0;
                    long size = in.size();
                    while (pos < size) {
                        long transferred = in.transferTo(pos, size - pos, out);
                        if (transferred <= 0) break;
                        pos += transferred;
                    }
                }
            }
        }
    }
}
