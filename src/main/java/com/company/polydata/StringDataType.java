package com.company.polydata;

import com.company.DataConverterByteStream;

import java.nio.ByteBuffer;

public class StringDataType implements DataType<StringDataType>{
    private String value;

    @Override
    public StringDataType read(ByteBuffer buffer) {
        this.value = DataConverterByteStream.getString(buffer);
        return this;
    }

    @Override
    public String toString() {
        return "StringDataType{" +
                "value='" + value + '\'' +
                '}';
    }
}
