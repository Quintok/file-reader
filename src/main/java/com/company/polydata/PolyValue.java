package com.company.polydata;

import com.company.blockfile.ClassInfo;
import com.company.blockfile.DataConverterByteStream;

public class PolyValue extends ClassInfo {

    private final DataType result;

    public PolyValue(DataConverterByteStream converter) {
        super(converter);
        StorageType type = StorageType.fromValue(converter.getInt());
        switch(type) {
            case ERROR:
                result = new NullDataType();
                break;
            case BOOL:
                final BoolDataType boolDataType = new BoolDataType();
                result = boolDataType.read(converter);
                break;
            case CHARS:
                final StringDataType charsDataType = new StringDataType();
                result = charsDataType.readChars(converter);
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

    @Override
    public String toString() {
        return result.toString();
    }

    public DataType getData() {
        return result;
    }

    enum StorageType {
        ERROR(0),
        BOOL(1),
        CHAR(2),
        INT_8(3),
        INT_16(4),
        INT_32(5),
        INT_64(6),
        UINT_8(7),
        UINT_16(8),
        UINT_32(9),
        UINT_64(10),
        FLOAT_32(11),
        FLOAT_64(12),
        STRING(16),
        CHARS(17),
        BITBOOL(18),
        DATE(19),
        TIME(20),
        TIMESTAMP(21),
        BINARY(23);

        private final int value;

        StorageType(final int value) {
            this.value = value;
        }

        public static StorageType fromValue(final int value) {
            for (StorageType type : StorageType.values()) {
                if(type.value == value)
                    return type;
            }

            throw new IllegalArgumentException("Unknown storage value type: " + value);

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
