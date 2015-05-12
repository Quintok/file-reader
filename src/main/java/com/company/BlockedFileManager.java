package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
    private FreeBlockManager freeBlockManager;

    public BlockedFileManager(ByteBuffer buffer,
                              int version,
                              WorkingMode workingMode,
                              DataBlock primaryDataBlock,
                              DataBlock primaryFreeDataBlock,
                              DataBlock secondaryDataBlock,
                              DataBlock secondaryFreeDataBlock) {
        this.buffer = buffer;
        this.version = version;
        this.workingMode = workingMode;
        this.primaryDataBlock = primaryDataBlock;
        this.primaryFreeDataBlock = primaryFreeDataBlock;
        this.secondaryDataBlock = primaryDataBlock;
        this.secondaryFreeDataBlock = primaryFreeDataBlock;
        switch(workingMode) {
            case PRIMARY:
                loadAllocatedBlock(primaryDataBlock);
                loadFreeBlockManager(primaryFreeDataBlock);
                break;
            case SECONDARY:
                loadAllocatedBlock(secondaryDataBlock);
                loadFreeBlockManager(secondaryFreeDataBlock);
        }
    }

    static Optional<BlockedFileManager> fromMappedFile(final ByteBuffer buffer) {
        buffer.position(0);
        logger.info("Seeking to {} for opening string", 0);
        final String s = readString(buffer);
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


        DataBlock primaryDataBlock;
        DataBlock primaryFreeDataBlock;
        DataBlock secondaryDataBlock;
        DataBlock secondFreeDataBlock;

        buffer.position(27);
        logger.info("Seeking to {} for primary workmode blocks", 27);
        primaryDataBlock = DataConverterByteStream.get(buffer);
        logger.info("Primary block {}", primaryDataBlock);
        primaryFreeDataBlock = DataConverterByteStream.get(buffer);
        logger.info("Primary free block {}", primaryFreeDataBlock);
        buffer.position(75);
        logger.info("Seeking to {} for secondary workmode blocks", 75);
        secondaryDataBlock = DataConverterByteStream.get(buffer);
        logger.info("Secondary block {}", secondaryDataBlock);
        secondFreeDataBlock = DataConverterByteStream.get(buffer);
        logger.info("Secondary free block {}", secondFreeDataBlock);
        BlockedFileManager blockedFileManager = new BlockedFileManager(buffer,
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
    private static String readString(ByteBuffer bytes) {
        int size = 0;
        bytes.mark();
        while (bytes.getChar() != '\0') {
            size++;
        }
        bytes.reset();

        byte[] string = new byte[size];
        bytes.get(string);

        return new String(string, StandardCharsets.US_ASCII);
    }

    private void loadFreeBlockManager(DataBlock freeDataBlock) {
        buffer.position((int) freeDataBlock.getOffset());
        this.freeBlockManager = DataConverterByteStream.get(buffer);
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

    public long getSize() {
        return buffer.limit();
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
