package com.company.blockfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FreeBlockManager extends ClassInfo {
    static final Logger logger = LoggerFactory.getLogger(FreeBlockManager.class);
    private final List<DataBlock> blocks;

    public FreeBlockManager(DataConverterByteStream converter) {
        super(converter);
        final int size = converter.getInt();
        logger.info("Free block manager of fileSize {} bits, fileSize of free blocks is {}", converter.fileSize(), size);
        blocks = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            DataBlock db = converter.get();
            blocks.add(db);
            logger.debug("Adding block to list in position {} is {}", i, db);
        }
    }

    public List<DataBlock> getBlocks() {
        return blocks;
    }
}
