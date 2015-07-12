package com.company.blockfile;

import com.company.caching.ObjectReferenceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.google.common.base.Preconditions.checkState;

public class DataConverterByteStream implements ByteStreamConverter {
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

    @Override
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

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ClassInfo> T get() {
        final int classId = getClassType();
        logger.debug("Searching for classid: {}", classId);
        for (ClassInfo.Type type1 : ClassInfo.Type.values()) {
            if (type1.getClassId() == classId) {
                logger.debug("Found classid {} matches \"{}\"", classId, type1);
                try {
                    T result = (T) type1.getType().getConstructor(ByteStreamConverter.class).newInstance(this);

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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public StreamDataTypeAndLength getTypeAndLength() {
        final int typeIndex = buffer.get() & 0xFF;
        final int length = typeIndex >> 4;
        StreamDataType type = StreamDataType.values()[typeIndex & 0x0F];
        logger.debug("Fetching type {} of length {}", type, length);
        return new StreamDataTypeAndLength(type, length);
    }

    @Override
    public int readCompressedInteger(final int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            result += (buffer.get() & 0xff) << (8 * i);
        }
        logger.debug("Reading compressed integer of length: {}, result: {}", length, result);
        return result;
    }

    @Override
    public long readCompressedLong(final int length) {
        long value = 0;
        for (int i = 0; i < length; i++) {
            value += ((long) buffer.get() & 0xffL) << (8 * i);
        }
        logger.debug("Reading compressed long of length: {}, result: {}", length, value);
        return value;
    }

    @Override
    public List<String> getStringList() {
        final int size = getInt();
        List<String> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(getString());
        }
        return result;
    }

    @Override
    public double getDouble() {
        final StreamDataTypeAndLength typeAndLength = getTypeAndLength();
        return Double.longBitsToDouble(readCompressedLong(typeAndLength.length));
    }

    @Override
    public byte getByte() {
        return buffer.get();
    }

    @Override
    public byte[] getBytes(byte[] string) {
        buffer.get(string);
        return string;
    }

    @Override
    public long fileSize() {
        return buffer.limit();
    }

    @Override
    public long position() {
        return buffer.position();
    }

    @Override
    public void close() throws IOException {
        cache.close();
    }

    @Override
    public void seek(long position) {
        buffer.position((int) position);
    }
}
