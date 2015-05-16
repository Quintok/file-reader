package com.company.database;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {
    private final SObjectInfo tableMetadata;
    private final List<Column> columns;
    private final String IS_DELETED_KEY = "STR_IsDeleted";

    public Table(final SObjectInfo tableMetadata) {
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

    public Stream<RowSet> getRows(Column... columns) {
        return getRows(Lists.newArrayList(columns));
    }

    public Stream<RowSet> getRows(List<Column> columns) {
        return Stream.empty();
    }
}
