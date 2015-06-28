package com.company.polydata;

import com.company.blockfile.DataConverterByteStream;

import java.nio.charset.StandardCharsets;

public class StringDataType implements DataType<StringDataType>{
    // maximum number of bytes to use before switching from CHARS type to String type.
    private static final int CHARS_SIZE = 8;
    private String value;

    @Override
    public StringDataType read(DataConverterByteStream converter) {
        this.value = converter.getString();
        return this;
    }

    @Override
    public String toString() {
        return "StringDataType{" +
                "value='" + value + '\'' +
                '}';
    }

    public StringDataType readChars(DataConverterByteStream converter) {
        byte[] bytes = new byte[CHARS_SIZE];
        final DataConverterByteStream.StreamDataTypeAndLength typeAndLength = converter.getTypeAndLength();
        final int i = converter.readCompressedInteger(typeAndLength.length);
        converter.getBytes(bytes);
        this.value = new String(bytes, StandardCharsets.US_ASCII).trim();
        return this;
    }
}
