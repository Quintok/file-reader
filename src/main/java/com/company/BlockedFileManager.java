package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Optional;

public class BlockedFileManager {
    private static final Logger logger = LoggerFactory.getLogger(BlockedFileManager.class);
    private static final String ID_STRING = "STRBF";
    private static final int CURRENT_VERSION = 0x101;
    private static final int HEADER_SIZE = 127;
    private final int version;
    private final WorkingMode workingMode;
    private final ByteBuffer buffer;
    private BlockMetadata primaryBlockMetadata;
    private BlockMetadata primaryFreeBlockMetadata;
    private BlockMetadata secondaryBlockMetadata;
    private BlockMetadata secondaryFreeBlockMetadata;

    public BlockedFileManager(ByteBuffer buffer, int version, WorkingMode workingMode, BlockMetadata blockMetadata, BlockMetadata freeBlockMetadata) {
        this.buffer = buffer;
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

    static Optional<BlockedFileManager> fromMappedFile(final ByteBuffer buffer) {
        buffer.position(0);
        logger.info("Seeking to {} for opening string", 0);
        final String s = DataConverterByteStream.readString(buffer.slice());
        logger.info("Found opening string \"{}\"", s);
        if (!ID_STRING.equals(s))
            return Optional.empty();

        buffer.position(6);
        logger.info("Seeking to {} for file format version", 6);
        final int version = DataConverterByteStream.getInt(buffer);
        logger.info("Found version number {}", version);
        if (version > CURRENT_VERSION)
            return Optional.empty();

        buffer.position(26);
        logger.info("Seeking to {} for workmode", 26);
        byte b = buffer.get();
        WorkingMode workingMode = WorkingMode.PRIMARY;
        for (WorkingMode mode : WorkingMode.values()) {
            if (mode.ordinal() == ((int) b)) {
                workingMode = mode;
                continue;
            }

        }
        logger.info("Found workingmode {}", workingMode);


        BlockMetadata blockMetadata;
        BlockMetadata freeBlockMetadata;

        switch (workingMode) {
            case PRIMARY:
                buffer.position(27);
                logger.info("Seeking to {} for primary workmode blocks", 27);
                blockMetadata = DataConverterByteStream.get(buffer);
                logger.info("Primary block {}", blockMetadata);
                freeBlockMetadata = DataConverterByteStream.get(buffer);
                logger.info("Primary free block {}", freeBlockMetadata);
                break;
            case SECONDARY:
                buffer.position(75);
                logger.info("Seeking to {} for secondary workmode blocks", 75);
                blockMetadata = DataConverterByteStream.get(buffer);
                logger.info("Secondary block {}", blockMetadata);
                freeBlockMetadata = DataConverterByteStream.get(buffer);
                logger.info("Secondary free block {}", freeBlockMetadata);
                break;
            default:
                throw new UnsupportedOperationException("");
        }

        BlockedFileManager blockedFileManager = new BlockedFileManager(buffer, version, workingMode, blockMetadata, freeBlockMetadata);

        return Optional.of(blockedFileManager);
    }

    private void loadFreeBlock(BlockMetadata freeBlockMetadata) {
            buffer.position(freeBlockMetadata.getOffset());
            DataConverterByteStream.get(buffer);
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
