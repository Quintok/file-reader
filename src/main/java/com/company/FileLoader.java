package com.company;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.company.blockfile.*;
import com.company.database.Database;
import com.company.database.RowSet;
import com.company.database.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.stream.Stream;

public class FileLoader {
    private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);
    @Parameter(names = "-file", description = "File to load", required = true)
    private String filename;

    @Parameter(names = "-bfview", description = "Display bfview output")
    private boolean bfview;

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
            try(DataConverterByteStream converter = new DataConverterByteStream(buffer)) {
                final Optional<BlockedFileManager> headerOpt = BlockedFileManager.fromMappedFile(converter);
                BlockedFileManager blockedFileManager = headerOpt.get();
                final BlockInfo block = blockedFileManager.getBlock(0);
                buffer.position((int) block.getDataBlock().getOffset());
                Database d = converter.get();
                Table t = d.getTable("__SUPER_CHANNEL__").get();
                final Stream<RowSet> rows = t.getRows(t.getColumns());
                rows.forEach(row -> System.out.println(String.format("%s = %s", row.get(0), row.get(1))));

                if(bfview)
                    bfView(blockedFileManager);
            }

        }
    }

    private static void bfView(BlockedFileManager manager) {
        final String line = "\n---------------------------------------------------------------------\n";
        System.out.print(line);
        System.out.println("XXXXXXXX");
        System.out.println(String.format("Filesize = %s bytes", manager.getSize()));
        System.out.println(String.format("IDString = %s", manager.getIdString()));
        System.out.println(String.format("Version = %s", manager.getVersion()));
        System.out.println(String.format("mode indicator flag = %s", manager.getWorkingMode()));
        System.out.print(line);
        printBlock("PRIMARY   Block List Entry", manager.getPrimaryDataBlock());
        printBlock("PRIMARY   Free Block Entry", manager.getPrimaryFreeDataBlock());
        printBlock("SECONDARY   Block List Entry", manager.getSecondaryDataBlock());
        printBlock("SECONDARY   Free Block Entry", manager.getSecondaryFreeDataBlock());
        System.out.print(line);
        System.out.println(String.format("Total blocks = %s", manager.getNumBlocks()));
        System.out.print(line);
        System.out.println("Data Block Information");
        System.out.print(line);

        long bytesAllocated = 0L;
        long bytesFree = 0L;

        int i = 0;
        for(BlockInfo block : manager.getBlocks()) {
            System.out.print(String.format("Block #%s ", i));
            switch(block.getFlag()) {
                case ALLOCATED:
                    System.out.println("{allocated}");
                    bytesAllocated += block.getSize();
                    break;
                case UNUSED:
                    System.out.println("{unused}");
                    bytesFree += block.getSize();
            }
            printBlock("", block.getDataBlock());
            i++;
        }

        System.out.print(line);
        System.out.println("Free Block Information");
        System.out.print(line);

        final FreeBlockManager freeBlockManager = manager.getFreeBlockManager();
        i = 0;
        for(DataBlock block : freeBlockManager.getBlocks()) {
            System.out.println(String.format("Free Block #%s", i));
            printBlock("", block);
            bytesFree += block.getSize();
            i++;
        }
        System.out.println(String.format("Total Bytes used %s", bytesAllocated));
        System.out.println(String.format("Total Bytes free %s", bytesFree));
        System.out.println(String.format("Utalisation: %f", (double)bytesAllocated / (double)manager.getSize()));
    }

    private static void printBlock(String s, DataBlock dataBlock) {
        System.out.println(String.format("%s: offset = %s, length = %s", s, dataBlock.getOffset(), dataBlock.getSize()));
    }
}
