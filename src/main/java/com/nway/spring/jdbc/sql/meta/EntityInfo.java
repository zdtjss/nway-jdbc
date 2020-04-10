package com.nway.spring.jdbc.sql.meta;

import java.util.Map;

public final class EntityInfo {

    private String tableName;

    private ColumnInfo id;

    private Map<String, ColumnInfo> columnList;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public ColumnInfo getId() {
        return id;
    }

    public void setId(ColumnInfo id) {
        this.id = id;
    }

    public Map<String, ColumnInfo> getColumnList() {
        return columnList;
    }

    public void setColumnList(Map<String, ColumnInfo> columnList) {
        this.columnList = columnList;
    }
}
