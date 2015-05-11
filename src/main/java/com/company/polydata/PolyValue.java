package com.company.polydata;

import com.company.ClassInfo;
import com.company.DataConverterByteStream;

import java.nio.ByteBuffer;

public class PolyValue extends ClassInfo {

    final Type type;
    final DataType result;

    public PolyValue(ByteBuffer input) {
        super(input);
        type = Type.values()[DataConverterByteStream.getInt(input)];
        switch(type) {
            case BINARY:
                BinaryDataType object = DataConverterByteStream.get(input);
                result = DataConverterByteStream.get(input);
                break;
            default:
                result = null;
        }
    }

    enum Type {
        NULL,
        BOOL,
        CHAR,
        INT_8,
        INT_16,
        INT_32,
        INT_64,
        UINT_8,
        UINT_16,
        UINT_32,
        UINT_64,
        FLOAT_32,
        FLOAT_64,
        DATE,
        TIME,
        TIMESTAMP,
        STRING,
        BINARY
    }
}
