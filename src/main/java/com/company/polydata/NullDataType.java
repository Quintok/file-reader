package com.company.polydata;

import com.company.blockfile.DataConverterByteStream;

public class NullDataType implements DataType<NullDataType> {

    @Override
    public NullDataType read(DataConverterByteStream converter) {
        return this;
    }

    @Override
    public String toString() {
        return "NullDataType{}";
    }
}
