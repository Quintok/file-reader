package com.company.iterator;

import com.company.blockfile.DataConverterByteStream;

public interface ColumnDataType<T extends ColumnDataType<T>> {

    T read(DataConverterByteStream converter);

    boolean getValueAsBool(final int currentRowId);

    double getValueAsDouble(final int currentRowId);

    int getValueAsInteger(final int currentRowId);

    String getValueAsString(final int currentRowId);
}
