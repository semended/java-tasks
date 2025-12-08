package com.mipt.semengolodniuk.hw10io;

import java.nio.file.Path;

public final class FilePartNamer {

    public Path partPath(Path outputDir, String baseName, int index) {
        return outputDir.resolve(baseName + ".part" + index);
    }
}
