package com.company.database;

import com.company.polydata.DataType;
import com.company.polydata.PropertySet;

public class Column {
    private final Table parent;
    private final String name;
    private final SObjectInfo value;
    private final boolean isNullable;
    private final DataType<? extends DataType> defaultValue;
    private final int bucketId;
    private int numRows;

    public Column(final Table parent, final String name, final SObjectInfo value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
        this.defaultValue = value.getPropertySet().getProperties().get(PropertySet.Key.DefaultValue);
        this.isNullable = value.getPropertySet().getProperties().get(PropertySet.Key.IsNullable).asBoolean().get();
        this.bucketId = value.getPropertySet().getProperties().get(PropertySet.Key.BucketID).asInteger().get();
        this.numRows = value.getPropertySet().getProperties().get(PropertySet.Key.TotalRows).asInteger().get();
        parent.getDatabase().getBlockFileManager().getBlock(bucketId);
    }

    public int getNumRows() {
        return numRows;
    }

    public DataType<? extends DataType> getRow(final int index) {
        return defaultValue;
    }
}
