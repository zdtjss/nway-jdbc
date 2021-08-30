package com.nway.spring.jdbc.sql.meta;

import java.util.List;
import java.util.Map;

public final class EntityInfo {

    private String tableName;

    private ColumnInfo id;

    private List<String> columnList;

    private Map<String, ColumnInfo> columnMap;

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

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    public Map<String, ColumnInfo> getColumnMap() {
        return columnMap;
    }

    public void setColumnMap(Map<String, ColumnInfo> columnMap) {
        this.columnMap = columnMap;
    }
}
