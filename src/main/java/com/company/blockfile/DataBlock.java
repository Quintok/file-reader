package com.company.blockfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataBlock extends ClassInfo {
    private static final Logger logger = LoggerFactory.getLogger(DataBlock.class);
    final long size;
    long offset;

    public DataBlock(ByteStreamConverter converter) {
        super(converter);
        ByteStreamConverter.StreamDataTypeAndLength typeAndLength = converter.getTypeAndLength();
        offset = converter.readCompressedLong(typeAndLength.length);
        logger.debug("Offset: {} from {}", offset, typeAndLength);
        typeAndLength = converter.getTypeAndLength();
        this.size = converter.readCompressedLong(typeAndLength.length);
        logger.debug("Size: {} from {}", size, typeAndLength);
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "DataBlock{" +
                "offset=" + offset +
                ", fileSize=" + size +
                '}';
    }
}
