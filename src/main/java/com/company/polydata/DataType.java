package com.company.polydata;

import com.company.blockfile.ByteStreamConverter;

import java.io.Serializable;
import java.util.Optional;

/**
 * Ideally this would be an abstract class as the constructor would be used instead of the read method
 * Unfortunately BinaryDataType would require multiple inheritance in that case.
 */
public interface DataType<T extends DataType<T>> extends Serializable {
    T read(ByteStreamConverter converter);

    default Optional<Boolean> asBoolean() {
        return Optional.empty();
    }

    default Optional<Integer> asInteger() {
        return Optional.empty();
    }
}
