package com.company.iterator;

import com.company.database.Column;
import com.company.database.Table;
import com.company.polydata.IntegerDataType;
import com.company.polydata.PolyValue;
import com.company.polydata.PropertySet;

import java.util.ArrayList;
import java.util.List;

import static com.company.polydata.PolyValue.Type.INT_32;

public class TableRowIterator implements RowIterator {

    private BucketRowIterator currentBucketRowIterator;
    private final Table table;
    private final int totalRows;
    private int currentRowId;


    public TableRowIterator (final Table table, final int totalRows, final int bucketSize) {

        this.table = table;
        // should really come from here:
        PolyValue totalRowsPolyVal = table.getProperty(PropertySet.Key.TotalRows);
        assert(totalRowsPolyVal.getType() == INT_32);
        this.totalRows = ((IntegerDataType)totalRowsPolyVal.getResult()).getValue();

        List<Column> columns = table.getColumns();
        List<ColumnDataType> columnBuffers = new ArrayList<>();
        int initialBucketSize = Math.min(bucketSize, totalRows);

        for (Column column : columns) {
            PolyValue.Type type = column.getProperty(PropertySet.Key.Type).getType();
            switch (type) {
                case INT_32: {
                    columnBuffers.add(new IntegerColumn(bucketSize, initialBucketSize));
                    break;
                }
            }
        }
        // don't know if this is safe to assume, perhaps we should read block by block...
        currentBucketRowIterator = new BucketRowIterator(columnBuffers, bucketSize);
    }

    @Override
    public boolean next() {
        if (++currentRowId == totalRows) {
            return false;
        }
        else if (currentBucketRowIterator.next()) {
            return true;
        }
        else {
            currentBucketRowIterator = getNextBucketRowIterator();
            if (currentBucketRowIterator != null)
                return currentBucketRowIterator.next();
            else
                return false;
        }
    }

    private BucketRowIterator getNextBucketRowIterator() {
        // TO DO move all the column buffers to the next bucket of rows
        return null;
    }

    @Override
    public boolean getValueAsBool(int column) {
        return currentBucketRowIterator.getValueAsBool(column);
    }

    @Override
    public double getValueAsDouble(int column) {
        return currentBucketRowIterator.getValueAsDouble(column);
    }

    @Override
    public int getValueAsInteger(int column) {
        return currentBucketRowIterator.getValueAsInteger(column);
    }

    @Override
    public String getValueAsString(int column) {
        return currentBucketRowIterator.getValueAsString(column);
    }
}
