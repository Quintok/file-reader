package com.company.blockfile;

import com.company.caching.ObjectReferenceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.google.common.base.Preconditions.checkState;

public class DataConverterByteStream implements Closeable {
    static final Logger logger = LoggerFactory.getLogger(DataConverterByteStream.class);
    private final ObjectReferenceCache cache;

    // TODO: ByteBuffer needs wrapping for >2gig files due to int position limit.
    // FileChannels can support multiple ByteBuffers so it's not a big deal.
    // Check http://www.kdgregory.com/?page=java.byteBuffer
    private final ByteBuffer buffer;

    public DataConverterByteStream(ByteBuffer buffer) {
        this.buffer = Objects.requireNonNull(buffer);
        this.cache = new ObjectReferenceCache();
    }

    public int getInt() {
        final StreamDataTypeAndLength type = getTypeAndLength();
        switch (type.type) {
            case INT_8:
            case INT_16:
            case INT_32:
                return readCompressedInteger(type.length);
            default:
                logger.error("Unable to match type {} to a type of integral", type);
                throw new RuntimeException("Unmatched type: " + type);
        }
    }

    private int getClassType() {
        final StreamDataTypeAndLength type = getTypeAndLength();
        switch (type.type) {
            case CLASS_ID:
                return readCompressedInteger(type.length);
            default:
                logger.error("Unable to match type {} to a type of ClassIDType", type.type);
                throw new RuntimeException("Unmatched type: " + type.type);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ClassInfo> T get() {
        final int classId = getClassType();
        logger.debug("Searching for classid: {}", classId);
        for (ClassInfo.Type type1 : ClassInfo.Type.values()) {
            if (type1.getClassId() == classId) {
                logger.debug("Found classid {} matches \"{}\"", classId, type1);
                try {
                    T result = (T) type1.getType().getConstructor(DataConverterByteStream.class).newInstance(this);

                    // each class ends in an "CLASS_END" to know the literal end of the class object.
                    byte endClassId = buffer.get();
                    if (StreamDataType.CLASS_END.getKey() != endClassId) {
                        logger.error("Unable to find end of class for {} at position {}.  Chances are the spec changed for this class.", type1, buffer.position());
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

    public <T extends ClassInfo> Map<String, T> getStringMap() {
        int size = getInt();
        Map<String, T> resultMap = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            final String key = getString();
            final T value = get();
            resultMap.put(key, value);
        }

        return resultMap;
    }

    public <T extends ClassInfo> Map<String, T> getStringPointerMap(Class<T> clazz) {
        int size = getInt();
        Map<String, T> resultMap = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            final String key = getString();
            final T value = readPointer(clazz);
            resultMap.put(key, value);
        }
        return resultMap;
    }

    public <T extends ClassInfo> T readPointer(Class<T> clazz) {
        final int type = getClassType();
        final Optional<ClassInfo.Type> typeForId = ClassInfo.Type.getTypeForId(type);
        checkState(typeForId.isPresent());
        checkState(typeForId.get().getType().equals(clazz));
        StreamDataTypeAndLength registeredFlag = getTypeAndLength();
        boolean registered = registeredFlag.length != 0;
        if (registered) {
            int objectId = readCompressedInteger(registeredFlag.length);
            return cache.get(objectId);
        }
        checkState(registeredFlag.type.equals(StreamDataType.OBJECT_ID));
        final int objectId = buffer.getInt(); // this is just raw, not sure why.
        final T result = get();
        cache.put(objectId, result);
        return result;
    }

    public String getString() {
        // this logic is rather nonsensical.
        // If we know the length is zero why do we get the header for the std::string?
        // Also, why do we write the std::string header anyway?  We already know it's a string!
        final int length = getInt();
        final StreamDataTypeAndLength typeAndLength = getTypeAndLength();
        if (length == 0) {
            return "";
        }
        checkState(typeAndLength.type.equals(StreamDataType.BYTE_STREAM));
        buffer.position(buffer.position() + typeAndLength.length); // length is written again as a byte.
        byte[] cString = new byte[length];
        buffer.get(cString, 0, length);

        return new String(cString, StandardCharsets.UTF_8);
    }

    public StreamDataTypeAndLength getTypeAndLength() {
        final int typeIndex = buffer.get() & 0xFF;
        final int length = typeIndex >> 4;
        StreamDataType type = StreamDataType.values()[typeIndex & 0x0F];
        logger.debug("Fetching type {} of length {}", type, length);
        return new StreamDataTypeAndLength(type, length);
    }

    public int readCompressedInteger(final int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            result += (buffer.get() & 0xff) << (8 * i);
        }
        logger.debug("Reading compressed integer of length: {}, result: {}", length, result);
        return result;
    }

    public long readCompressedLong(final int length) {
        long value = 0;
        for (int i = 0; i < length; i++) {
            value += ((long) buffer.get() & 0xffL) << (8 * i);
        }
        logger.debug("Reading compressed long of length: {}, result: {}", length, value);
        return value;
    }

    public List<String> getStringList() {
        final int size = getInt();
        List<String> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(getString());
        }
        return result;
    }

    public double getDouble() {
        final StreamDataTypeAndLength typeAndLength = getTypeAndLength();
        return Double.longBitsToDouble(readCompressedLong(typeAndLength.length));
    }

    public byte getByte() {
        return buffer.get();
    }

    public byte[] getBytes(byte[] string) {
        buffer.get(string);
        return string;
    }

    public long fileSize() {
        return buffer.limit();
    }

    public long position() {
        return buffer.position();
    }

    @Override
    public void close() throws IOException {
        cache.close();
    }

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

    public static class StreamDataTypeAndLength {
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

    public void seek(long position) {
        buffer.position((int)position);
    }
}
