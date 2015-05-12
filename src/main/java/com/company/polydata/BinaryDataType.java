package com.company.polydata;

import com.company.ClassInfo;
import com.company.DataConverterByteStream;

import java.nio.ByteBuffer;

public class BinaryDataType extends ClassInfo implements DataType<BinaryDataType>  {
    private ByteBuffer bytes;

    public BinaryDataType(ByteBuffer input) {
        super(input);
        final DataConverterByteStream.StreamDataTypeAndLength typeAndLength = DataConverterByteStream.getTypeAndLength(buffer);
        final int size = typeAndLength.length;
        bytes = buffer.slice();
        bytes.limit(size);
    }

    @Override
    public BinaryDataType read(ByteBuffer buffer) {
        // binary data type is unique in that it is the only polyval which is itself a ClassInfo type.
        return this;
    }
}
