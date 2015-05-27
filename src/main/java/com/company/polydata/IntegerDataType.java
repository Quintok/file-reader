package com.company.polydata;

import com.company.blockfile.DataConverterByteStream;


public class IntegerDataType implements DataType<IntegerDataType> {

    private int value;

    @Override
    public IntegerDataType read(DataConverterByteStream converter) {
        this.value = converter.getInt();
        return this;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "IntegerColumn{" +
                "value=" + value +
                '}';
    }
}
