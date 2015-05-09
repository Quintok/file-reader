package com.company;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Optional;

public class FileLoader {
    private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);
    @Parameter(names = "-file", description="File to load", required = true)
    private String filename;

    @Parameter(names = "-help", description="This help text", help = true, hidden = true)
    private boolean help;

    public void loadFile(JCommander jCommander) throws IOException {
        if (help) {
            jCommander.usage();
            return;
        }
        logger.info("Loading file {}", filename);
        RandomAccessFile file = new RandomAccessFile(new File(filename), "r");
        final Optional<BlockedFileManager> headerOpt = BlockedFileManager.fromFile(file);
        BlockedFileManager blockedFileManager = headerOpt.get();
        System.out.println("File header version: " + blockedFileManager.getVersion());
        System.out.println("File working mode: " + blockedFileManager.getWorkingMode());
    }
}
