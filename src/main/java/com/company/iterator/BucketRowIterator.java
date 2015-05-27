package com.company.iterator;

import java.util.List;

public class BucketRowIterator implements RowIterator {

    private List<ColumnDataType> columns;
    private int bucketSize;
    private int currentRowId;

    public BucketRowIterator(List<ColumnDataType> columns, int bucketSize) {

        this.columns = columns;
        this.bucketSize = bucketSize;
        this.currentRowId = 0;
    }

    @Override
    public boolean next() {
        return (++currentRowId != bucketSize);
    }

    @Override
    public boolean getValueAsBool(int column) {
        return columns.get(column).getValueAsBool(currentRowId);
    }

    @Override
    public double getValueAsDouble(int column) {
        return columns.get(column).getValueAsDouble(currentRowId);
    }

    @Override
    public int getValueAsInteger(int column) {
        return columns.get(column).getValueAsInteger(currentRowId);
    }

    @Override
    public String getValueAsString(int column) {
        return columns.get(column).getValueAsString(currentRowId);
    }
}
