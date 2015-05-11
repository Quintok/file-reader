package com.company.polydata;

import java.nio.ByteBuffer;

public class NullDataType implements DataType<NullDataType> {
    @Override
    public NullDataType read(ByteBuffer buffer) {
        return this;
    }
}
