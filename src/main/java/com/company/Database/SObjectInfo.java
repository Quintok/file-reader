package com.company.Database;

import com.company.ClassInfo;
import com.company.DataConverterByteStream;
import com.company.polydata.PropertySet;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class SObjectInfo extends ClassInfo {
    private final String objectType;
    private final int blockNumber;
    private final PropertySet propertySet;
    private final List<String> subObjectNames;
    private final Map<String, SObjectInfo> children;

    public SObjectInfo(ByteBuffer input) {
        super(input);
        objectType = DataConverterByteStream.getString(input);
        blockNumber = DataConverterByteStream.getInt(input);
        propertySet = DataConverterByteStream.get(input);
        subObjectNames = DataConverterByteStream.getStringList(input);
        children = DataConverterByteStream.getStringPointerMap(SObjectInfo.class, input);
    }
}
