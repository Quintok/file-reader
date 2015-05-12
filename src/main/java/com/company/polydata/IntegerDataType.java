package com.company.polydata;

import com.company.DataConverterByteStream;

import java.nio.ByteBuffer;


public class IntegerDataType implements DataType<IntegerDataType> {

    private int value;

    @Override
    public IntegerDataType read(ByteBuffer buffer) {
        this.value = DataConverterByteStream.getInt(buffer);
        return this;
    }
}
