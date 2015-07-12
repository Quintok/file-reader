package com.company.blockfile;

import com.company.WorkingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class BlockedFileManager {
    private static final Logger logger = LoggerFactory.getLogger(BlockedFileManager.class);
    private static final String ID_STRING = "STRBF";
    private static final int CURRENT_VERSION = 0x101;
    private final ByteStreamConverter converter;
    private final int version;
    private final WorkingMode workingMode;
    private DataBlock primaryDataBlock;
    private DataBlock primaryFreeDataBlock;
    private DataBlock secondaryDataBlock;
    private DataBlock secondaryFreeDataBlock;
    private BlockData blockData;
    private FreeBlockManager freeBlockManager;

    public BlockedFileManager(
            ByteStreamConverter converter,
            int version,
            WorkingMode workingMode,
            DataBlock primaryDataBlock,
            DataBlock primaryFreeDataBlock,
            DataBlock secondaryDataBlock,
            DataBlock secondaryFreeDataBlock) {
        this.converter = converter;
        this.version = version;
        this.workingMode = workingMode;
        this.primaryDataBlock = primaryDataBlock;
        this.primaryFreeDataBlock = primaryFreeDataBlock;
        this.secondaryDataBlock = primaryDataBlock;
        this.secondaryFreeDataBlock = primaryFreeDataBlock;
        switch (workingMode) {
            case PRIMARY:
                loadAllocatedBlock(primaryDataBlock);
                loadFreeBlockManager(primaryFreeDataBlock);
                break;
            case SECONDARY:
                loadAllocatedBlock(secondaryDataBlock);
                loadFreeBlockManager(secondaryFreeDataBlock);
        }
    }

    public static Optional<BlockedFileManager> fromMappedFile(ByteStreamConverter converter) {
        converter.seek(0);
        logger.info("Seeking to {} for opening string", 0);
        final String s = readString(converter);
        logger.info("Found opening string \"{}\"", s);
        if (!ID_STRING.equals(s))
            return Optional.empty();

        converter.seek(6);
        logger.info("Seeking to {} for file format version", 6);
        final int version = converter.getInt();
        logger.info("Found version number {}", version);
        if (version > CURRENT_VERSION)
            return Optional.empty();

        converter.seek(26);
        logger.info("Seeking to {} for workmode", 26);
        byte b = converter.getByte();
        WorkingMode workingMode = WorkingMode.PRIMARY;
        for (WorkingMode mode : WorkingMode.values()) {
            if (mode.ordinal() == ((int) b)) {
                workingMode = mode;
                break;
            }

        }
        logger.info("Found workingmode {}", workingMode);


        DataBlock primaryDataBlock;
        DataBlock primaryFreeDataBlock;
        DataBlock secondaryDataBlock;
        DataBlock secondFreeDataBlock;

        converter.seek(27);
        logger.info("Seeking to {} for primary workmode blocks", 27);
        primaryDataBlock = converter.get();
        logger.info("Primary block {}", primaryDataBlock);
        primaryFreeDataBlock = converter.get();
        logger.info("Primary free block {}", primaryFreeDataBlock);
        converter.seek(75);
        logger.info("Seeking to {} for secondary workmode blocks", 75);
        secondaryDataBlock = converter.get();
        logger.info("Secondary block {}", secondaryDataBlock);
        secondFreeDataBlock = converter.get();
        logger.info("Secondary free block {}", secondFreeDataBlock);
        BlockedFileManager blockedFileManager = new BlockedFileManager(converter,
                version,
                workingMode,
                primaryDataBlock,
                primaryFreeDataBlock,
                secondaryDataBlock,
                secondFreeDataBlock);
        return Optional.of(blockedFileManager);
    }

    /**
     * Reads ascii string from current bytebuffer position.
     * Will execute a rewind on the buffer.
     *
     * @return ASCII string from position.
     */
    private static String readString(ByteStreamConverter bytes) {
        byte[] string = new byte[ID_STRING.length()];
        bytes.getBytes(string);
        return new String(string, StandardCharsets.US_ASCII);
    }

    private void loadFreeBlockManager(DataBlock freeDataBlock) {
        converter.seek((int) freeDataBlock.getOffset());
        this.freeBlockManager = converter.get();
    }

    private void loadAllocatedBlock(DataBlock dataBlock) {
        converter.seek(dataBlock.getOffset());
        this.blockData = converter.get();
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

    public long getSize() {
        return converter.fileSize();
    }

    public String getIdString() {
        return ID_STRING;
    }

    public DataBlock getPrimaryDataBlock() {
        return primaryDataBlock;
    }

    public DataBlock getPrimaryFreeDataBlock() {
        return primaryFreeDataBlock;
    }

    public DataBlock getSecondaryDataBlock() {
        return secondaryDataBlock;
    }

    public DataBlock getSecondaryFreeDataBlock() {
        return secondaryFreeDataBlock;
    }

    public int getNumBlocks() {
        return this.blockData.getDataBlocks().size();
    }

    public List<BlockInfo> getBlocks() {
        return blockData.getDataBlocks();
    }

    public FreeBlockManager getFreeBlockManager() {
        return freeBlockManager;
    }
}
