package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BlockedFileManager {
    private static final Logger logger = LoggerFactory.getLogger(BlockedFileManager.class);
    private static final String ID_STRING = "STRBF";
    private static final int CURRENT_VERSION = 0x101;
    private final int version;
    private final WorkingMode workingMode;
    private final RandomAccessFile file;
    private BlockMetadata primaryBlockMetadata;
    private BlockMetadata primaryFreeBlockMetadata;
    private BlockMetadata secondaryBlockMetadata;
    private BlockMetadata secondaryFreeBlockMetadata;

    public BlockedFileManager(RandomAccessFile file, int version, WorkingMode workingMode, BlockMetadata blockMetadata, BlockMetadata freeBlockMetadata) {
        this.file = file;
        this.version = version;
        this.workingMode = workingMode;
        switch (workingMode) {
            case PRIMARY:
                primaryBlockMetadata = blockMetadata;
                primaryFreeBlockMetadata = freeBlockMetadata;
                break;
            case SECONDARY:
                secondaryBlockMetadata = blockMetadata;
                secondaryFreeBlockMetadata = freeBlockMetadata;
                break;
            default:
                throw new RuntimeException("Unsupported working mode");
        }
        loadAllocatedBlock(blockMetadata);
        loadFreeBlock(freeBlockMetadata);
    }

    static Optional<BlockedFileManager> fromFile(final RandomAccessFile file) {
        try {
            file.seek(0);
            logger.info("Seeking to {} for opening string", 0);
            byte[] bytes = new byte[ID_STRING.getBytes().length];
            file.read(bytes);
            final String s = new String(bytes, StandardCharsets.UTF_8);
            logger.info("Found opening string \"{}\"", s);
            if (!ID_STRING.equals(s))
                return Optional.empty();

            file.seek(6);
            logger.info("Seeking to {} for file format version", 6);
            final int version = DataConverterByteStream.getInt(Channels.newInputStream(file.getChannel()));
            logger.info("Found version number {}", version);
            if (version > CURRENT_VERSION)
                return Optional.empty();

            file.seek(26);
            logger.info("Seeking to {} for workmode", 26);
            byte b = (byte) file.read();
            WorkingMode workingMode = WorkingMode.PRIMARY;
            for (WorkingMode mode : WorkingMode.values()) {
                if (mode.ordinal() == ((int) b))
                    workingMode = mode;
            }
            logger.info("Found workingmode {}", workingMode);


            BlockMetadata blockMetadata;
            BlockMetadata freeBlockMetadata;

            switch (workingMode) {
                case PRIMARY:
                    file.seek(27);
                    logger.info("Seeking to {} for primary workmode blocks", 27);
                    blockMetadata = DataConverterByteStream.get(Channels.newInputStream(file.getChannel()));
                    logger.info("Primary block {}", blockMetadata);
                    freeBlockMetadata = DataConverterByteStream.get(Channels.newInputStream(file.getChannel()));
                    logger.info("Primary free block {}", freeBlockMetadata);
                    break;
                case SECONDARY:
                    file.seek(75);
                    logger.info("Seeking to {} for secondary workmode blocks", 75);
                    blockMetadata = DataConverterByteStream.get(Channels.newInputStream(file.getChannel()));
                    logger.info("Secondary block {}", blockMetadata);
                    freeBlockMetadata = DataConverterByteStream.get(Channels.newInputStream(file.getChannel()));
                    logger.info("Secondary free block {}", freeBlockMetadata);
                    break;
                default:
                    throw new UnsupportedOperationException("");
            }

            BlockedFileManager blockedFileManager = new BlockedFileManager(file, version, workingMode, blockMetadata, freeBlockMetadata);

            return Optional.of(blockedFileManager);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private void loadFreeBlock(BlockMetadata freeBlockMetadata) {
        try {
            byte[] block = new byte[freeBlockMetadata.getSize()];
            file.seek(freeBlockMetadata.getOffset());
            file.readFully(block);
            ByteArrayInputStream bais = new ByteArrayInputStream(block);

            DataConverterByteStream.get(bais);
        } catch (IOException e) {
            logger.error("Unable to seek to block {}", freeBlockMetadata);
            throw new RuntimeException(e);
        }

    }

    private void loadAllocatedBlock(BlockMetadata blockMetadata) {

    }

    public int getVersion() {
        return version;
    }

    public WorkingMode getWorkingMode() {
        return workingMode;
    }
}
