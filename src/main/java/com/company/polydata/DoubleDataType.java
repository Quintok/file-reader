package com.company.polydata;

import com.company.blockfile.ByteStreamConverter;

public class DoubleDataType implements DataType<DoubleDataType> {
    private double value;

    @Override
    public DoubleDataType read(ByteStreamConverter converter) {
        value = converter.getDouble();
        return this;
    }

    @Override
    public String toString() {
        return "DoubleDataType{" +
                "value=" + value +
                '}';
    }
}
