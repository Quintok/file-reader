package com.company.polydata;

import com.company.blockfile.DataConverterByteStream;

public class DoubleDataType implements DataType<DoubleDataType>{
    private double value;

    @Override
    public DoubleDataType read(DataConverterByteStream converter) {
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
