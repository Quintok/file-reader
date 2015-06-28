package com.company.database;

import com.company.blockfile.BlockedFileManager;
import com.company.blockfile.ClassInfo;
import com.company.blockfile.DataConverterByteStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class Database extends ClassInfo {
    static final Logger logger = LoggerFactory.getLogger(Database.class);
    private final int version;
    private final Map<String, SObjectInfo> subObjects;
    private BlockedFileManager blockFileManager;

    public Database(DataConverterByteStream converter) {
        super(converter);
        version = converter.getInt();
        if(version < 1 || version > 1) {
            throw new UnsupportedOperationException("Database is not supported.");
        }
        subObjects = converter.getStringPointerMap(SObjectInfo.class);
    }

    public Optional<Table> getTable(final String name) {
        final Optional<SObjectInfo> tables = subObjects.get("").getChild("Tables")
                .flatMap(sObjectInfo -> sObjectInfo.getChild(name));
        return tables.map((sobject) -> new Table(this, sobject));
    }

    @Override
    public String toString() {
        return "Database{" +
                "version=" + version +
                ", subObjects=" + subObjects +
                '}';
    }

    public void setBlockFileManager(BlockedFileManager blockFileManager) {
        this.blockFileManager = blockFileManager;
    }

    public BlockedFileManager getBlockFileManager() {
        return blockFileManager;
    }
}
