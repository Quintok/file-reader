package com.company.blockfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataBlock extends ClassInfo {
    private static final Logger logger = LoggerFactory.getLogger(DataBlock.class);

    long offset;
    final long size;

    public DataBlock(DataConverterByteStream converter) {
        super(converter);
        DataConverterByteStream.StreamDataTypeAndLength typeAndLength = converter.getTypeAndLength();
        offset = converter.readCompressedLong(typeAndLength.length);
        logger.debug("Offset: {} from {}", offset, typeAndLength);
        typeAndLength = converter.getTypeAndLength();
        this.size = converter.readCompressedLong(typeAndLength.length);
        logger.debug("Size: {} from {}", size, typeAndLength);
    }

    public long getOffset() {
        return offset;
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

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
