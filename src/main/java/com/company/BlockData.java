package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.*;

public class BlockData extends ClassInfo{
    static final Logger logger = LoggerFactory.getLogger(BlockData.class);
    private Set<Integer> unusedBlocks;
    private List<BlockInfo> dataBlocks;
    public BlockData(ByteBuffer input) {
        super(input);
        final int unusedBlocksSize = DataConverterByteStream.getInt(input);
        this.unusedBlocks = new HashSet<>(unusedBlocksSize);
        for(int i = 0; i < unusedBlocksSize; i++) {
            unusedBlocks.add(DataConverterByteStream.getInt(input));
        }
        logger.info("{} of unused blocks in file", unusedBlocksSize);
        final int dataBlocksSize = DataConverterByteStream.getInt(input);
        this.dataBlocks = new ArrayList<>(dataBlocksSize);
        for(int i = 0; i < dataBlocksSize; i++) {
            dataBlocks.add(DataConverterByteStream.get(input));
        }
        logger.info("{} datablocks of file", dataBlocksSize);
    }

    public List<BlockInfo> getDataBlocks() {
        return dataBlocks;
    }
}
