package com.company.database;

import com.company.blockfile.ByteStreamConverter;
import com.company.blockfile.ClassInfo;

public abstract class DatabaseObject extends ClassInfo {
    private final String type;

    public DatabaseObject(ByteStreamConverter converter) {
        super(converter);
        type = converter.getString();
    }
}
