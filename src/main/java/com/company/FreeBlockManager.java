package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FreeBlockManager extends ClassInfo {
    static final Logger logger = LoggerFactory.getLogger(FreeBlockManager.class);
    private final List<DataBlock> blocks;

    public FreeBlockManager(ByteBuffer buffer) {
        super(buffer);
        final int size = DataConverterByteStream.getInt(buffer);
        logger.info("Free block manager of size {} bits, size of free blocks is {}", buffer.limit(), size);
        blocks = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            DataBlock db = DataConverterByteStream.get(buffer);
            blocks.add(db);
            logger.debug("Adding block to list in position {} is {}", i, db);
        }
    }

    public List<DataBlock> getBlocks() {
        return blocks;
    }
}
