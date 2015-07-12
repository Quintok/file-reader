package com.company.blockfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockData extends ClassInfo {
    static final Logger logger = LoggerFactory.getLogger(BlockData.class);
    private Set<Integer> unusedBlocks;
    private List<BlockInfo> dataBlocks;

    public BlockData(ByteStreamConverter converter) {
        super(converter);
        final int unusedBlocksSize = converter.getInt();
        this.unusedBlocks = new HashSet<>(unusedBlocksSize);
        for (int i = 0; i < unusedBlocksSize; i++) {
            unusedBlocks.add(converter.getInt());
        }
        logger.info("{} of unused blocks in file", unusedBlocksSize);
        final int dataBlocksSize = converter.getInt();
        this.dataBlocks = new ArrayList<>(dataBlocksSize);
        for (int i = 0; i < dataBlocksSize; i++) {
            dataBlocks.add(converter.get());
        }
        logger.info("{} datablocks of file", dataBlocksSize);
    }

    public List<BlockInfo> getDataBlocks() {
        return dataBlocks;
    }
}
