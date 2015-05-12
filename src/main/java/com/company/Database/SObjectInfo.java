package com.company.Database;

import com.company.ClassInfo;
import com.company.DataConverterByteStream;
import com.company.polydata.PropertySet;

import java.nio.ByteBuffer;
import java.util.*;

public class SObjectInfo extends ClassInfo {
    private final Types objectType;
    private final int blockNumber;
    private final PropertySet propertySet;
    private final List<String> subObjectNames;
    private final Map<String, SObjectInfo> children;

    enum Types {
        MultiMap,
        Field,
        Table,
        Column,
        AssocTablePolyHash,
        Join,
        Index,
        Folder,
        IndexUnique
    }

    public SObjectInfo(ByteBuffer input) {
        super(input);
        objectType = Types.valueOf(DataConverterByteStream.getString(input));
        blockNumber = DataConverterByteStream.getInt(input);
        propertySet = DataConverterByteStream.get(input);
        subObjectNames = DataConverterByteStream.getStringList(input);
        children = DataConverterByteStream.getStringPointerMap(SObjectInfo.class, input);
    }

    @Override
    public String toString() {
        return "SObjectInfo{" +
                "objectType='" + objectType + '\'' +
                ", blockNumber=" + blockNumber +
                ", propertySet=" + propertySet +
                ", subObjectNames=" + subObjectNames +
                ", children=" + children +
                '}';
    }
}
