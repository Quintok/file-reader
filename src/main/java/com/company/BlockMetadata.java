package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static com.google.common.base.Preconditions.checkState;

public class BlockMetadata extends ClassType {
    private static final Logger logger = LoggerFactory.getLogger(BlockMetadata.class);
    final long offset;
    final int size;

    public BlockMetadata(InputStream input) {
        super(input);
        DataConverterByteStream.StreamDataTypeAndLength typeAndLength = DataConverterByteStream.getTypeAndLength(input);
        this.offset = DataConverterByteStream.readCompressedLong(input, typeAndLength.length);
        logger.info("Offset: {} from {}", offset, typeAndLength);
        typeAndLength = DataConverterByteStream.getTypeAndLength(input);
        long sizel = DataConverterByteStream.readCompressedLong(input, typeAndLength.length);
        // There are later presumptions that this is an int.  Practically it can be an int as the blocksize is 16k
        // by default.  However this is a mistake in c_tac
        checkState(sizel <= Integer.MAX_VALUE, "It is a long, but not as we know it jim.");
        this.size = (int) sizel;
        logger.info("Size: {} from {}", size, typeAndLength);
    }

    public long getOffset() {
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
