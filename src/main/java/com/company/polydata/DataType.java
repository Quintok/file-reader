package com.company.polydata;

import com.company.blockfile.DataConverterByteStream;

import java.io.Serializable;

/**
 * Ideally this would be an abstract class as the constructor would be used instead of the read method
 * Unfortunately BinaryDataType would require multiple inheritance in that case.
 */
public interface DataType<T extends DataType<T>> extends Serializable {
    T read(DataConverterByteStream converter);
}
