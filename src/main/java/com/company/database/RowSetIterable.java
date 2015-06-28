package com.company.database;

import com.company.polydata.DataType;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class RowSetIterable implements Iterable<RowSet> {

    private final List<Column> columns;

    public RowSetIterable(final List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public Iterator<RowSet> iterator() {
        return new RowSetIterator(columns);
    }

    private static class RowSetIterator implements Iterator<RowSet> {

        private final List<Column> columns;
        private final int size;
        private int index = 0;

        RowSetIterator(List<Column> columns) {
            this.columns = columns;
            this.size = columns.get(0).getNumRows();
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public RowSet next() {
            index++;
            List<DataType<? extends DataType>> list = columns.stream().map((c) -> c.getRow(index)).collect(Collectors.toList());
            return new RowSet(list);
        }
    }
}
