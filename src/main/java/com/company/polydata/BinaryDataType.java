package com.company.polydata;

import com.company.ClassInfo;
import com.company.DataConverterByteStream;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class BinaryDataType extends ClassInfo implements DataType<BinaryDataType> {
    private byte[] value;

    public BinaryDataType(ByteBuffer input) {
        super(input);
    }

    @Override
    public BinaryDataType read(ByteBuffer buffer) {
        // Integer is mis-serialized as a bytestream.
        final DataConverterByteStream.StreamDataTypeAndLength length = DataConverterByteStream.getTypeAndLength(buffer);
        final int i = DataConverterByteStream.readCompressedInteger(buffer, length.length);
        value = new byte[i];
        buffer.get(value);

        return this;
    }

    @Override
    public String toString() {
        return "BinaryDataType{" +
                "value=" + Arrays.toString(value) +
                '}';
    }
}
