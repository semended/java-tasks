package com.mipt.semengolodniuk.hw10io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public final class FileSplitter {

    private final FilePartNamer namer = new FilePartNamer();

    public List<Path> split(Path source, Path outputDir, int partSize) throws IOException {
        if (partSize <= 0) {
            throw new IllegalArgumentException("partSize must be > 0");
        }
        Files.createDirectories(outputDir);

        String baseName = source.getFileName().toString();
        List<Path> parts = new ArrayList<>();

        try (FileChannel in = FileChannel.open(source, StandardOpenOption.READ)) {
            long size = in.size();
            if (size == 0) {
                Path p = namer.partPath(outputDir, baseName, 1);
                Files.write(p, new byte[0]);
                parts.add(p);
                return parts;
            }

            ByteBuffer buffer = ByteBuffer.allocate(partSize);
            int idx = 1;

            while (true) {
                buffer.clear();
                int read = in.read(buffer);
                if (read == -1) break;

                buffer.flip();
                Path part = namer.partPath(outputDir, baseName, idx++);
                try (FileChannel out = FileChannel.open(
                        part,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE)) {
                    while (buffer.hasRemaining()) {
                        out.write(buffer);
                    }
                }
                parts.add(part);
            }
        }

        return parts;
    }
}
