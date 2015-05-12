package com.company.polydata;

import com.company.DataConverterByteStream;

import java.nio.ByteBuffer;

public class DoubleDataType implements DataType<DoubleDataType>{
    private double value;

    @Override
    public DoubleDataType read(ByteBuffer buffer) {
        value = DataConverterByteStream.getDouble(buffer);
        return this;
    }

    @Override
    public String toString() {
        return "DoubleDataType{" +
                "value=" + value +
                '}';
    }
}
