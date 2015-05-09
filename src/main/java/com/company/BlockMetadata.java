package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static com.google.common.base.Preconditions.checkState;

public class BlockMetadata extends ClassType {
    private static final Logger logger = LoggerFactory.getLogger(BlockMetadata.class);

    // There are later presumptions that this is an int.  Practically it can be an int as the blocksize is 16k
    // by default.  However this is a mistake in c_tac
    final int offset;
    final int size;

    public BlockMetadata(ByteBuffer buffer) {
        super(buffer);
        DataConverterByteStream.StreamDataTypeAndLength typeAndLength = DataConverterByteStream.getTypeAndLength(buffer);
        long offsetl = DataConverterByteStream.readCompressedLong(buffer, typeAndLength.length);
        checkState(offsetl <= Integer.MAX_VALUE, "It is a long, but not as we know it jim.");
        this.offset = (int) offsetl;
        logger.info("Offset: {} from {}", offset, typeAndLength);
        typeAndLength = DataConverterByteStream.getTypeAndLength(buffer);
        long sizel = DataConverterByteStream.readCompressedLong(buffer, typeAndLength.length);
        checkState(sizel <= Integer.MAX_VALUE, "It is a long, but not as we know it jim.");
        this.size = (int) sizel;
        logger.info("Size: {} from {}", size, typeAndLength);
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "BlockMetadata{" +
                "offset=" + offset +
                ", size=" + size +
                '}';
    }
}
