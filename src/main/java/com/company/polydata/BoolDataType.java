package com.company.polydata;

import com.company.blockfile.ByteStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class BoolDataType implements DataType<BoolDataType> {
    Logger logger = LoggerFactory.getLogger(BoolDataType.class);
    boolean value;

    @Override
    public BoolDataType read(ByteStreamConverter converter) {
        final ByteStreamConverter.StreamDataTypeAndLength typeAndLength = converter.getTypeAndLength();
        // if the length is zero it is false.
        if (typeAndLength.length == 0) {
            this.value = false;
            return this;
        }

        byte b = converter.getByte();

        this.value = b > 0;
        return this;
    }

    @Override
    public Optional<Boolean> asBoolean() {
        return Optional.of(value);
    }

    @Override
    public String toString() {
        return "BoolDataType{" +
                "value=" + value +
                '}';
    }
}
