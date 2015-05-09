package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.google.common.base.Preconditions.checkState;

public class DataConverterByteStream {
    static final Logger logger = LoggerFactory.getLogger(DataConverterByteStream.class);

    public static int getInt(InputStream file) {
        try {
            final StreamDataTypeAndLength type = getTypeAndLength(file);
            ByteBuffer buffer;
            switch (type.type) {
                case StreamInt8Type:
                    return file.read();
                case StreamInt16Type:
                    buffer = fetchBuffer(file, Short.BYTES);
                    return buffer.getShort();
                case StreamInt32Type:
                    buffer = fetchBuffer(file, Integer.BYTES);
                    return buffer.getInt();
                default:
                    logger.error("Unable to match type {} to a type of integral", type);
                    throw new RuntimeException("Unmatched type: " + type);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ByteBuffer fetchBuffer(InputStream file, int bytes) {
        try {
            byte[] shortBytes = new byte[bytes];
            logger.info("Creating byte buffer to walk over {} bits", bytes * Byte.SIZE);
            final int readBytes = file.read(shortBytes);
            checkState(readBytes == shortBytes.length);
            return ByteBuffer.wrap(shortBytes).order(ByteOrder.LITTLE_ENDIAN);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getClassType(final InputStream file) {
        final StreamDataTypeAndLength type = getTypeAndLength(file);
        switch (type.type) {
            case StreamClassIDType:
                return readCompressedInteger(file, type.length);
            default:
                logger.error("Unable to match type {} to a type of ClassIDType", type.type);
                throw new RuntimeException("Unmatched type: " + type.type);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends ClassType> T get(InputStream file) {
        final int classId = getClassType(file);
        logger.debug("Searching for classid: {}", classId);
        for (ClassType.Type type1 : ClassType.Type.values()) {
            if (type1.getClassId() == classId) {
                logger.info("Found classid {} matches \"{}\"", classId, type1);
                try {
                    T result = (T) type1.getType().getConstructor(InputStream.class).newInstance(file);
                    // each class ends in an "StreamClassEndType" to know the literal end of the class object.
                    byte endClassId = (byte) file.read();
                    if (StreamDataType.StreamClassEndType.getKey() != endClassId) {
                        logger.error("Unable to find end of class for {}.  Chances are the spec changed for this class.", type1);
                        throw new RuntimeException("Unable to find end of class.");
                    }
                    return result;
                } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("Unable to find class type: " + classId);
    }

    public static StreamDataTypeAndLength getTypeAndLength(InputStream file) {
        try {
            final byte typeIndex = (byte) file.read();
            final int length = typeIndex >> 4;
            StreamDataType type = StreamDataType.values()[typeIndex & 0x0F];
            logger.info("Fetching type {}", type);
            return new StreamDataTypeAndLength(type, length);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int readCompressedInteger(InputStream file, final int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            try {
                result += (file.read() & 0xff) << (8 * i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("Length: {}, result: {}", length, result);
        return result;
    }

    public static long readCompressedLong(InputStream file, final int length) {
        try {
            long value = 0;
            for (int i = 0; i < length; i++) {
                value += ((long) file.read() & 0xffL) << (8 * i);
            }
            logger.info("Length: {}, result: {}", length, value);
            return value;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    enum StreamDataType {
        StreamInt8Type(0x00),
        StreamInt16Type(0x01),
        StreamInt32Type(0x02),
        StreamInt64Type(0x03),
        StreamFloat32Type(0x04),
        StreamFloat64Type(0x05),
        StreamBoolType(0x06),
        StreamClassIDType(0x07),
        StreamObjectIDType(0x08),
        StreamByteStreamType(0x09),
        StreamClassEndType(0x0A),
        StreamArrayType(0x0B),
        StreamStringType(0x0C);

        private final byte key;

        private StreamDataType(final int key) {
            this.key = (byte) ((byte) key & 0xFF);
        }

        public byte getKey() {
            return key;
        }
    }

    static class StreamDataTypeAndLength {
        final StreamDataType type;
        final int length;

        public StreamDataTypeAndLength(final StreamDataType type, final int length) {
            this.type = type;
            this.length = length;
        }

        @Override
        public String toString() {
            return "StreamDataTypeAndLength{" +
                    "type=" + type +
                    ", length=" + length +
                    '}';
        }
    }
}
