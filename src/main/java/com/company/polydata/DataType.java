package com.company.polydata;

import java.nio.ByteBuffer;

/**
 * Ideally this would be an abstract class as the constructor would be used instead of the read method
 * Unfortunately BinaryDataType would require multiple inheritance in that case.
 */
public interface DataType<T extends DataType<T>> {
    T read(ByteBuffer buffer);
}
