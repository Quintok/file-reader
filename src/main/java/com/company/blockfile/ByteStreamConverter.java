package com.company.blockfile;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by nick on 12/07/15.
 */
public interface ByteStreamConverter extends Closeable {
    int getInt();

    @SuppressWarnings("unchecked")
    <T extends ClassInfo> T get();

    <T extends ClassInfo> Map<String, T> getStringMap();

    <T extends ClassInfo> Map<String, T> getStringPointerMap(Class<T> clazz);

    <T extends ClassInfo> T readPointer(Class<T> clazz);

    String getString();

    StreamDataTypeAndLength getTypeAndLength();

    int readCompressedInteger(int length);

    long readCompressedLong(int length);

    List<String> getStringList();

    double getDouble();

    byte getByte();

    byte[] getBytes(byte[] string);

    long fileSize();

    long position();

    @Override
    void close() throws IOException;

    void seek(long position);

    enum StreamDataType {
        INT_8(0x00),
        INT_16(0x01),
        INT_32(0x02),
        INT_64(0x03),
        FLOAT_32(0x04),
        FLOAT_64(0x05),
        BOOL(0x06),
        CLASS_ID(0x07),
        OBJECT_ID(0x08),
        BYTE_STREAM(0x09),
        CLASS_END(0x0A),
        ARRAY(0x0B),
        STRING(0x0C);

        private final byte key;

        StreamDataType(final int key) {
            this.key = (byte) ((byte) key & 0xFF);
        }

        public byte getKey() {
            return key;
        }
    }

    class StreamDataTypeAndLength {
        public final StreamDataType type;
        public final int length;

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
