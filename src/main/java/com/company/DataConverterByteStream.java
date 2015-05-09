package com.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DataConverterByteStream {
    static final Logger logger = LoggerFactory.getLogger(DataConverterByteStream.class);

    public static int getInt(ByteBuffer buffer) {
        final StreamDataTypeAndLength type = getTypeAndLength(buffer);
        switch (type.type) {
            case StreamInt8Type:
            case StreamInt16Type:
            case StreamInt32Type:
                return readCompressedInteger(buffer, type.length);
            default:
                logger.error("Unable to match type {} to a type of integral", type);
                throw new RuntimeException("Unmatched type: " + type);
        }
    }

    private static int getClassType(final ByteBuffer buffer) {
        final StreamDataTypeAndLength type = getTypeAndLength(buffer);
        switch (type.type) {
            case StreamClassIDType:
                return readCompressedInteger(buffer, type.length);
            default:
                logger.error("Unable to match type {} to a type of ClassIDType", type.type);
                throw new RuntimeException("Unmatched type: " + type.type);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends ClassInfo> T get(ByteBuffer file) {
        final int classId = getClassType(file);
        logger.debug("Searching for classid: {}", classId);
        for (ClassInfo.Type type1 : ClassInfo.Type.values()) {
            if (type1.getClassId() == classId) {
                logger.debug("Found classid {} matches \"{}\"", classId, type1);
                try {
                    T result = (T) type1.getType().getConstructor(ByteBuffer.class).newInstance(file);

                    // each class ends in an "StreamClassEndType" to know the literal end of the class object.
                    byte endClassId = file.get();
                    if (StreamDataType.StreamClassEndType.getKey() != endClassId) {
                        logger.error("Unable to find end of class for {} at position {}.  Chances are the spec changed for this class.", type1, file.position());
                        throw new RuntimeException("Unable to find end of class.");
                    }
                    return result;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("Unable to find class type: " + classId);
    }

    public static StreamDataTypeAndLength getTypeAndLength(ByteBuffer buffer) {
        final int typeIndex = buffer.get() & 0xFF;
        final int length = typeIndex >> 4;
        StreamDataType type = StreamDataType.values()[typeIndex & 0x0F];
        logger.debug("Fetching type {}", type);
        return new StreamDataTypeAndLength(type, length);
    }

    public static int readCompressedInteger(ByteBuffer buffer, final int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            result += (buffer.get() & 0xff) << (8 * i);
        }
        logger.debug("Reading compressed integer of length: {}, result: {}", length, result);
        return result;
    }

    public static long readCompressedLong(ByteBuffer buffer, final int length) {
        long value = 0;
        for (int i = 0; i < length; i++) {
            value += ((long) buffer.get() & 0xffL) << (8 * i);
        }
        logger.debug("Reading compressed long of length: {}, result: {}", length, value);
        return value;
    }

    /**
     * Reads ascii string from current bytebuffer position.
     * Will execute a rewind on the buffer.
     *
     * @return ASCII string from position.
     */
    public static String readString(ByteBuffer bytes) {
        int size = 0;
        while (bytes.getChar() != '\0') {
            size++;
        }
        bytes.rewind();

        byte[] string = new byte[size];
        bytes.get(string);

        return new String(string, StandardCharsets.US_ASCII);
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
