package com.company.polydata;

import com.company.blockfile.DataConverterByteStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class BoolDataType implements DataType<BoolDataType> {
    Logger logger = LoggerFactory.getLogger(BoolDataType.class);
    boolean value;
    @Override
    public BoolDataType read(DataConverterByteStream converter) {
        byte b = converter.getByte();

        // TODO: Work out what the hell is going on here.
        if(b == 22) {
            byte oldB = b;
            b = converter.getByte();
            logger.warn("Re reading boolean polyVal for no obvious reason.  b={}, oldB={}, position={}", b, oldB, converter.position());
        }
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
