package com.company.polydata;

import java.nio.ByteBuffer;

public class BoolDataType implements DataType<BoolDataType> {
    boolean value;
    @Override
    public BoolDataType read(ByteBuffer buffer) {
        this.value = buffer.get() > 0;
        return this;
    }
}
