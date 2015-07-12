package com.company.database;

import com.company.blockfile.ByteStreamConverter;
import com.company.blockfile.ClassInfo;
import com.company.polydata.PropertySet;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SObjectInfo extends ClassInfo {
    private final Types objectType;
    private final int blockNumber;
    private final PropertySet propertySet;
    private final List<String> subObjectNames;
    private final Map<String, SObjectInfo> children;


    public SObjectInfo(ByteStreamConverter converter) {
        super(converter);
        objectType = Types.valueOf(converter.getString());
        blockNumber = converter.getInt();
        propertySet = converter.get();
        subObjectNames = converter.getStringList();
        children = converter.getStringPointerMap(SObjectInfo.class);
    }

    public Types getObjectType() {
        return objectType;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public PropertySet getPropertySet() {
        return propertySet;
    }

    public List<String> getSubObjectNames() {
        return subObjectNames;
    }

    public Map<String, SObjectInfo> getChildren() {
        return children;
    }

    public Optional<SObjectInfo> getChild(String tables) {
        return Optional.ofNullable(children.get(tables));
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

    enum Types {
        MultiMap,
        Field,
        Table,
        Column,
        AssocTablePolyHash,
        Join,
        Index,
        Folder,
        IndexUnique;
    }
}
