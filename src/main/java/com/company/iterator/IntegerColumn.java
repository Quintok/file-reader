package com.company.iterator;

import com.company.blockfile.DataConverterByteStream;
import com.google.common.base.Preconditions;

public class IntegerColumn implements ColumnDataType<IntegerColumn> {

    // or perhaps even better, have this be a ByteBuffer and do unpacking on demand?
    private int[] values;
    private int used;

    public IntegerColumn(final int bucketSize, final int used) {
        this.used = used;
        values = new int[bucketSize];
    }

    @Override
    public IntegerColumn read(DataConverterByteStream converter) {
        for (int i = 0; i < values.length; i++)
            values[i] = converter.getInt();
        return this;
    }

    @Override
    public String toString() {
        return "IntegerColumn{" +
                "values.length=" + values.length +
                "values=" + values +
                '}';
    }

    @Override
    public boolean getValueAsBool(int currentRowId) {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support data conversion to boolean.");
    }

    @Override
    public double getValueAsDouble(int currentRowId) {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support data conversion to double.");
    }

    @Override
    public int getValueAsInteger(int currentRowId) {
        Preconditions.checkArgument(currentRowId < values.length, "rowId: " + currentRowId + ", out of range for values array, length: " + values.length);
        return values[currentRowId];
    }

    @Override
    public String getValueAsString(int currentRowId) {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support data conversion to String.");
    }
}
