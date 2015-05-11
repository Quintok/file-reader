package com.company.polydata;

import java.nio.ByteBuffer;

public interface DataType<T extends DataType<T>> {
    T read(ByteBuffer buffer);
}
