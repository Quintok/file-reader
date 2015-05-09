package com.company;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public class FileLoader {
    private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);
    @Parameter(names = "-file", description = "File to load", required = true)
    private String filename;

    @Parameter(names = "-help", description = "This help text", help = true, hidden = true)
    private boolean help;

    public void loadFile(JCommander jCommander) throws IOException {
        if (help) {
            jCommander.usage();
            return;
        }
        logger.info("Loading file {}", filename);
        try(FileChannel fileChannel = FileChannel.open(Paths.get(filename), StandardOpenOption.READ)) {
            try(FileLock lock = fileChannel.tryLock()) {
                if (null == lock) {
                    // Failed to get file lock.  Disconnecting.
                    throw new IllegalStateException("Unable to lock the file for reading.");
                }
                MappedByteBuffer buffer = fileChannel.map(
                        FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                final Optional<BlockedFileManager> headerOpt = BlockedFileManager.fromMappedFile(buffer);
                BlockedFileManager blockedFileManager = headerOpt.get();
                System.out.println("File header version: " + blockedFileManager.getVersion());
                System.out.println("File working mode: " + blockedFileManager.getWorkingMode());
            }
        }
    }
}
