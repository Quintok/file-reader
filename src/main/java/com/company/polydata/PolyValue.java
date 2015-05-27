package com.company.polydata;

import com.company.blockfile.ClassInfo;
import com.company.blockfile.DataConverterByteStream;

public class PolyValue extends ClassInfo {

    final DataType result;
    final Type type;

    public PolyValue(DataConverterByteStream converter) {
        super(converter);
        type = Type.values()[converter.getInt()];
        switch(type) {
            case NULL:
                result = new NullDataType();
                break;
            case BOOL:
                BoolDataType dataType = new BoolDataType();
                result = dataType.read(converter);
                break;
            case BINARY:
                BinaryDataType binaryDataType = new BinaryDataType(converter);
                result = binaryDataType.read(converter);
                break;
            case INT_32:
                IntegerDataType integerDataType = new IntegerDataType();
                result = integerDataType.read(converter);
                break;
            case FLOAT_64:
                DoubleDataType doubleDataType = new DoubleDataType();
                result = doubleDataType.read(converter);
                break;
            case STRING:
                StringDataType stringDataType = new StringDataType();
                result = stringDataType.read(converter);
                break;
            default:
                throw new UnsupportedOperationException("unsupported polyvalue type." + type);
        }
    }

    public Type getType() {
        return type;
    }

    public DataType getResult() {
        return result;
    }

    @Override
    public String toString() {
        return result.toString();
    }

    public enum Type {
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
