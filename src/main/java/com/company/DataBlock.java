package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class DataBlock extends ClassInfo {
    private static final Logger logger = LoggerFactory.getLogger(DataBlock.class);

    long offset;
    final long size;

    public DataBlock(ByteBuffer buffer) {
        super(buffer);
        DataConverterByteStream.StreamDataTypeAndLength typeAndLength = DataConverterByteStream.getTypeAndLength(buffer);
        offset = DataConverterByteStream.readCompressedLong(buffer, typeAndLength.length);
        logger.debug("Offset: {} from {}", offset, typeAndLength);
        typeAndLength = DataConverterByteStream.getTypeAndLength(buffer);
        this.size = DataConverterByteStream.readCompressedLong(buffer, typeAndLength.length);
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
                ", size=" + size +
                '}';
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
