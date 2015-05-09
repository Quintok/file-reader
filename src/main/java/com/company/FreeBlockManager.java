package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FreeBlockManager extends ClassInfo {
    static final Logger logger = LoggerFactory.getLogger(FreeBlockManager.class);
    private final List<DataBlock> blockList;

    public FreeBlockManager(ByteBuffer buffer) {
        super(buffer);
        final int size = DataConverterByteStream.getInt(buffer);
        logger.info("Free block manager of size {} bits, size of free blocks is {}", buffer.limit(), size);
        blockList = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            DataBlock db = DataConverterByteStream.get(buffer);
            blockList.add(db);
            logger.debug("Adding block to list in position {} is {}", i, db);
        }
    }
}
