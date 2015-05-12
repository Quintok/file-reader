package com.company;

import com.company.caching.ObjectReferenceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.google.common.base.Preconditions.checkState;

public class DataConverterByteStream {
    static final Logger logger = LoggerFactory.getLogger(DataConverterByteStream.class);

    public static int getInt(ByteBuffer buffer) {
        final StreamDataTypeAndLength type = getTypeAndLength(buffer);
        switch (type.type) {
            case INT_8:
            case INT_16:
            case INT_32:
                return readCompressedInteger(buffer, type.length);
            default:
                logger.error("Unable to match type {} to a type of integral", type);
                throw new RuntimeException("Unmatched type: " + type);
        }
    }

    private static int getClassType(final ByteBuffer buffer) {
        final StreamDataTypeAndLength type = getTypeAndLength(buffer);
        switch (type.type) {
            case CLASS_ID:
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

                    // each class ends in an "CLASS_END" to know the literal end of the class object.
                    byte endClassId = file.get();
                    if (StreamDataType.CLASS_END.getKey() != endClassId) {
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

    public static <T extends ClassInfo> Map<String, T> getStringMap(ByteBuffer buffer) {
        int size = getInt(buffer);
        Map<String, T> resultMap = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            final String key = getString(buffer);
            final T value = get(buffer);
            resultMap.put(key, value);
        }

        return resultMap;
    }

    public static <T extends ClassInfo> Map<String, T> getStringPointerMap(Class<T> clazz, ByteBuffer buffer) {
        int size = getInt(buffer);
        Map<String, T> resultMap = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            final String key = getString(buffer);
            final T value = readPointer(clazz, buffer);
            resultMap.put(key, value);
        }
        return resultMap;
    }

    public static <T extends ClassInfo> T readPointer(Class<T> clazz, ByteBuffer buffer) {
        final int type = getClassType(buffer);
        final Optional<ClassInfo.Type> typeForId = ClassInfo.Type.getTypeForId(type);
        checkState(typeForId.isPresent());
        checkState(typeForId.get().getType().equals(clazz));
        StreamDataTypeAndLength registeredFlag = getTypeAndLength(buffer);
        boolean registered = registeredFlag.length != 0;
        if (registered) {
            int objectId = readCompressedInteger(buffer, registeredFlag.length);
            return ObjectReferenceCache.get().get(objectId);
        }
        checkState(registeredFlag.type.equals(StreamDataType.OBJECT_ID));
        final int objectId = buffer.getInt(); // this is just raw, not sure why.
        final T result = get(buffer);
        ObjectReferenceCache.get().put(objectId, result);
        return result;
    }

    public static String getString(ByteBuffer buffer) {
        // this logic is rather nonsensical.
        // If we know the length is zero why do we get the header for the std::string?
        // Also, why do we write the std::string header anyway?  We already know it's a string!
        final int length = getInt(buffer);
        final StreamDataTypeAndLength typeAndLength = getTypeAndLength(buffer);
        if (length == 0) {
            return new String("");
        }
        checkState(typeAndLength.type.equals(StreamDataType.BYTE_STREAM));
        buffer.position(buffer.position() + typeAndLength.length); // length is written again as a byte.
        byte[] cString = new byte[length];
        buffer.get(cString, 0, length);

        return new String(cString, StandardCharsets.UTF_8);
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

    public static List<String> getStringList(ByteBuffer buffer) {
        final int size = getInt(buffer);
        List<String> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(getString(buffer));
        }
        return result;
    }

    public static double getDouble(final ByteBuffer buffer) {
        final StreamDataTypeAndLength typeAndLength = getTypeAndLength(buffer);
        return Double.longBitsToDouble(readCompressedLong(buffer, typeAndLength.length));
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

        private StreamDataType(final int key) {
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
}
