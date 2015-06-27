package com.company.polydata;

import com.company.blockfile.DataConverterByteStream;

import java.util.Optional;


public class IntegerDataType implements DataType<IntegerDataType> {

    private int value;

    @Override
    public IntegerDataType read(DataConverterByteStream converter) {
        this.value = converter.getInt();
        return this;
    }

    @Override
    public Optional<Integer> asInteger() {
        return Optional.of(value);
    }

    @Override
    public String toString() {
        return "IntegerDataType{" +
                "value=" + value +
                '}';
    }
}
