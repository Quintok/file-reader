package com.company.polydata;

import com.company.blockfile.ByteStreamConverter;

import java.nio.charset.StandardCharsets;

import static com.google.common.base.Preconditions.checkState;

public class StringDataType implements DataType<StringDataType> {
    // maximum number of bytes to use before switching from CHARS type to String type.
    private static final int CHARS_SIZE = 8;
    private String value;

    @Override
    public StringDataType read(ByteStreamConverter converter) {
        this.value = converter.getString();
        return this;
    }

    @Override
    public String toString() {
        return "StringDataType{" +
                "value='" + value + '\'' +
                '}';
    }

    public StringDataType readChars(ByteStreamConverter converter) {
        byte[] bytes = new byte[CHARS_SIZE];
        final ByteStreamConverter.StreamDataTypeAndLength typeAndLength = converter.getTypeAndLength();
        final int i = converter.readCompressedInteger(typeAndLength.length);
        checkState(i == CHARS_SIZE);
        converter.getBytes(bytes);
        this.value = new String(bytes, StandardCharsets.US_ASCII).trim();
        return this;
    }
}
