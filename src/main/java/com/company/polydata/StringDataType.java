package com.company.polydata;

import com.company.blockfile.DataConverterByteStream;

public class StringDataType implements DataType<StringDataType>{
    private String value;

    @Override
    public StringDataType read(DataConverterByteStream converter) {
        this.value = converter.getString();
        return this;
    }

    @Override
    public String toString() {
        return "StringDataType{" +
                "value='" + value + '\'' +
                '}';
    }
}
