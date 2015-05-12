package com.company.polydata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class BoolDataType implements DataType<BoolDataType> {
    Logger logger = LoggerFactory.getLogger(BoolDataType.class);
    boolean value;
    @Override
    public BoolDataType read(ByteBuffer buffer) {
        byte b = buffer.get();

        // TODO: Work out what the hell is going on here.
        if(b == 22) {
            byte oldB = b;
            b = buffer.get();
            logger.error("Re reading boolean polyVal for no obvious reason.  b={}, oldB={}, position={}", b, oldB, buffer.position());
        }
        this.value = b > 0;
        return this;
    }

    @Override
    public String toString() {
        return "BoolDataType{" +
                "value=" + value +
                '}';
    }
}
