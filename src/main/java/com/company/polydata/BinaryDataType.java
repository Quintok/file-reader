package com.company.polydata;

import com.company.ClassInfo;
import com.company.DataConverterByteStream;

import java.nio.ByteBuffer;

public class BinaryDataType extends ClassInfo implements DataType<BinaryDataType> {
    private ByteBuffer bytes;

    public BinaryDataType(ByteBuffer input) {
        super(input);
    }

    @Override
    public BinaryDataType read(ByteBuffer buffer) {
        // Integer is mis-serialized as a bytestream.
        final DataConverterByteStream.StreamDataTypeAndLength length = DataConverterByteStream.getTypeAndLength(buffer);
        final int i = DataConverterByteStream.readCompressedInteger(buffer, length.length);
        bytes = buffer.slice();
        bytes.limit(i);
        buffer.position(buffer.position() + i);

        return this;
    }

    @Override
    public String toString() {
        return "BinaryDataType{" +
                "bytes=" + bytes +
                '}';
    }
}
