package com.company.database;

import com.company.polydata.DataType;

import java.util.List;

public class RowSet {
    private List<DataType<? extends DataType>> values;

    public RowSet(List<DataType<? extends DataType>> values) {
        this.values = values;
    }

    public DataType<? extends DataType> get(final int columnOffset) {
        return values.get(columnOffset);
    }
}
