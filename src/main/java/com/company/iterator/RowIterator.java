package com.company.iterator;

public interface RowIterator {

    boolean next();

    boolean getValueAsBool(int column);

    double getValueAsDouble(int column);

    int getValueAsInteger(int column);

    String getValueAsString(int column);
}
