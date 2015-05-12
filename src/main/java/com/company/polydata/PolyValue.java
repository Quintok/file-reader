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
            case NULL:
                result = new NullDataType();
                break;
            case BOOL:
                BoolDataType dataType = new BoolDataType();
                result = dataType.read(input);
                break;
            case BINARY:
                BinaryDataType binaryDataType = new BinaryDataType(input);
                result = binaryDataType.read(input);
                break;
            case INT_32:
                IntegerDataType integerDataType = new IntegerDataType();
                result = integerDataType.read(input);
                break;
            case FLOAT_64:
                DoubleDataType doubleDataType = new DoubleDataType();
                result = doubleDataType.read(input);
                break;
            case STRING:
                StringDataType stringDataType = new StringDataType();
                result = stringDataType.read(input);
                break;
            default:
                throw new UnsupportedOperationException("unsupported polyvalue type." + type);
        }
    }

    @Override
    public String toString() {
        return result.toString();
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
