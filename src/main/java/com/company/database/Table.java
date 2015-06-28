package com.company.database;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {
    private final SObjectInfo tableMetadata;
    private final List<Column> columns;
    private static final String IS_DELETED_KEY = "STR_IsDeleted";
    private final Database database;

    public Table(final Database database, final SObjectInfo tableMetadata) {
        this.database = database;
        Objects.requireNonNull(tableMetadata);
        this.tableMetadata = tableMetadata;
        final Map<String, SObjectInfo> columnMetadata = tableMetadata.getChild("Columns").get().getChildren();
        final Stream<Column> columnStream = columnMetadata.entrySet()
                .parallelStream()
                // We strip STR_IsDeleted because that's an (currently unused) internal column
                .filter(entry -> !entry.getKey().equals(IS_DELETED_KEY))
                .map(entry -> new Column(this, entry.getKey(), entry.getValue()));

        this.columns = columnStream.collect(Collectors.toList());
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Iterable<RowSet> getRows(Column... columns) {
        return getRows(Lists.newArrayList(columns));
    }

    public Iterable<RowSet> getRows(List<Column> columns) {
        return new RowSetIterable(columns);
    }

    public Database getDatabase() {
        return database;
    }
}
