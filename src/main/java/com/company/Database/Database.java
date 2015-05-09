package com.company.Database;

import com.company.ClassInfo;
import com.company.DataConverterByteStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class Database extends ClassInfo {
    static final Logger logger = LoggerFactory.getLogger(Database.class);
    private final int version;
    public Database(ByteBuffer input) {
        super(input);
        version = DataConverterByteStream.getInt(buffer);
        if(version < 1 || version > 1) {
            throw new UnsupportedOperationException("Database is not supported.");
        }
        logger.error("Wohoo!! {}", version);
    }
}
