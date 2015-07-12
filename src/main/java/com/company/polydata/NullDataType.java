package com.company.polydata;

import com.company.blockfile.ByteStreamConverter;

public class NullDataType implements DataType<NullDataType> {

    @Override
    public NullDataType read(ByteStreamConverter converter) {
        return this;
    }

    @Override
    public String toString() {
        return "NullDataType{}";
    }
}
