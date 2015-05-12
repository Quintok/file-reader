package com.company;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.company.Database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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
        try (FileChannel fileChannel = FileChannel.open(Paths.get(filename), StandardOpenOption.READ)) {
            MappedByteBuffer buffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            final Optional<BlockedFileManager> headerOpt = BlockedFileManager.fromMappedFile(buffer);
            BlockedFileManager blockedFileManager = headerOpt.get();
            final BlockInfo block = blockedFileManager.getBlock(0);
            buffer.position((int) block.getDataBlock().getOffset());
            Database d = DataConverterByteStream.get(buffer);


            PrintWriter out
                    = new PrintWriter(new BufferedWriter(new FileWriter("foo.out", false)));

            out.write(d.toString());
            out.flush();
            out.close();
            System.out.println("Complete.");
        }
    }
}
