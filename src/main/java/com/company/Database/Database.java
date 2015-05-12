package com.company.Database;

import com.company.ClassInfo;
import com.company.DataConverterByteStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Map;

public class Database extends ClassInfo {
    static final Logger logger = LoggerFactory.getLogger(Database.class);
    private final int version;
    private final Map<String, SObjectInfo> subObjects;
    public Database(ByteBuffer input) {
        super(input);
        version = DataConverterByteStream.getInt(input);
        if(version < 1 || version > 1) {
            throw new UnsupportedOperationException("Database is not supported.");
        }
        subObjects = DataConverterByteStream.getStringPointerMap(SObjectInfo.class, input);
        logger.error("Wohoo!! {}", version);
    }

    @Override
    public String toString() {
        return "Database{" +
                "version=" + version +
                ", subObjects=" + subObjects +
                '}';
    }
}
