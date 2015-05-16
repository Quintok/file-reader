package com.company.polydata;

import com.company.blockfile.ClassInfo;
import com.company.blockfile.DataConverterByteStream;

import java.util.Arrays;

public class BinaryDataType extends ClassInfo implements DataType<BinaryDataType> {
    private byte[] value;

    public BinaryDataType(DataConverterByteStream converter) {
        super(converter);
    }

    @Override
    public BinaryDataType read(DataConverterByteStream converter) {
        // Integer is mis-serialized as a bytestream.
        final DataConverterByteStream.StreamDataTypeAndLength length = converter.getTypeAndLength();
        final int i = converter.readCompressedInteger(length.length);
        value = new byte[i];
        converter.getBytes(value);

        return this;
    }

    @Override
    public String toString() {
        return "BinaryDataType{" +
                "value=" + Arrays.toString(value) +
                '}';
    }
}
