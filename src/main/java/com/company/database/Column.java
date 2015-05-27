package com.company.database;

import com.company.polydata.PolyValue;
import com.company.polydata.PropertySet;

public class Column {
    private final Table parent;
    private final String name;
    private final SObjectInfo value;

    public Column(final Table parent, final String name, final SObjectInfo value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
    }

    public PolyValue getProperty(PropertySet.Key key) {
        return value.getPropertySet().getProperties().get(key);
    }
}
