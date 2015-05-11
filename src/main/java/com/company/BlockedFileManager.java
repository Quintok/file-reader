package com.company;

import com.company.caching.ObjectReferenceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Optional;

public class BlockedFileManager {
    private static final Logger logger = LoggerFactory.getLogger(BlockedFileManager.class);
    private static final String ID_STRING = "STRBF";
    private static final int CURRENT_VERSION = 0x101;
    private final int version;
    private final WorkingMode workingMode;
    private final ByteBuffer buffer;
    private DataBlock primaryDataBlock;
    private DataBlock primaryFreeDataBlock;
    private DataBlock secondaryDataBlock;
    private DataBlock secondaryFreeDataBlock;
    private BlockData blockData;
    private FreeBlockManager freeBlockData;

    public BlockedFileManager(ByteBuffer buffer, int version, WorkingMode workingMode, DataBlock dataBlock, DataBlock freeDataBlock) {
        this.buffer = buffer;
        this.version = version;
        this.workingMode = workingMode;
        switch (workingMode) {
            case PRIMARY:
                primaryDataBlock = dataBlock;
                primaryFreeDataBlock = freeDataBlock;
                break;
            case SECONDARY:
                secondaryDataBlock = dataBlock;
                secondaryFreeDataBlock = freeDataBlock;
                break;
            default:
                throw new RuntimeException("Unsupported working mode");
        }
        loadAllocatedBlock(dataBlock);
        loadFreeBlockManager(freeDataBlock);
    }

    static Optional<BlockedFileManager> fromMappedFile(final ByteBuffer buffer) {
        buffer.position(0);
        logger.info("Seeking to {} for opening string", 0);
        final String s = DataConverterByteStream.readString(buffer);
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


        DataBlock dataBlock;
        DataBlock freeDataBlock;

        switch (workingMode) {
            case PRIMARY:
                buffer.position(27);
                logger.info("Seeking to {} for primary workmode blocks", 27);
                dataBlock = DataConverterByteStream.get(buffer);
                logger.info("Primary block {}", dataBlock);
                freeDataBlock = DataConverterByteStream.get(buffer);
                logger.info("Primary free block {}", freeDataBlock);
                break;
            case SECONDARY:
                buffer.position(75);
                logger.info("Seeking to {} for secondary workmode blocks", 75);
                dataBlock = DataConverterByteStream.get(buffer);
                logger.info("Secondary block {}", dataBlock);
                freeDataBlock = DataConverterByteStream.get(buffer);
                logger.info("Secondary free block {}", freeDataBlock);
                break;
            default:
                throw new UnsupportedOperationException("");
        }

        BlockedFileManager blockedFileManager = new BlockedFileManager(buffer, version, workingMode, dataBlock, freeDataBlock);

        return Optional.of(blockedFileManager);
    }

    private void loadFreeBlockManager(DataBlock freeDataBlock) {
        buffer.position((int) freeDataBlock.getOffset());
        this.freeBlockData = DataConverterByteStream.get(buffer);
    }

    private void loadAllocatedBlock(DataBlock dataBlock) {
        buffer.position((int) dataBlock.getOffset());
        this.blockData = DataConverterByteStream.get(buffer);
    }

    public int getVersion() {
        return version;
    }

    public WorkingMode getWorkingMode() {
        return workingMode;
    }

    public BlockInfo getBlock(int i) {
        BlockInfo dataBlock = blockData.getDataBlocks().get(i);
        logger.info("Getting data block {} which is {}", i, dataBlock);
        return dataBlock;
    }
}
