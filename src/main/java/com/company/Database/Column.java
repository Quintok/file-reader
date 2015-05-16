package com.company.database;

public class Column {
    private final Table parent;
    private final String name;
    private final SObjectInfo value;

    public Column(final Table parent, final String name, final SObjectInfo value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
    }
}
