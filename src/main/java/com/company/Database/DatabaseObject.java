package com.company.database;

import com.company.blockfile.ClassInfo;
import com.company.blockfile.DataConverterByteStream;

public abstract class DatabaseObject extends ClassInfo {
    private final String type;

    public DatabaseObject(DataConverterByteStream converter) {
        super(converter);
        type = converter.getString();
    }
}
