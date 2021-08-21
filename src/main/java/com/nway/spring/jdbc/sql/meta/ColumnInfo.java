package com.nway.spring.jdbc.sql.meta;

import com.nway.spring.jdbc.sql.fill.FillStrategy;
import com.nway.spring.jdbc.sql.permission.PermissionStrategy;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

public class ColumnInfo {

    private String columnName;
    private FillStrategy fillStrategy;
    private PermissionStrategy permissionStrategy;
    private Field readMethod;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public FillStrategy getFillStrategy() {
        return fillStrategy;
    }

    public void setFillStrategy(FillStrategy fillStrategy) {
        this.fillStrategy = fillStrategy;
    }

    public PermissionStrategy getPermissionStrategy() {
        return permissionStrategy;
    }

    public void setPermissionStrategy(PermissionStrategy permissionStrategy) {
        this.permissionStrategy = permissionStrategy;
    }

    public Field getReadMethod() {
        return readMethod;
    }

    public void setReadMethod(Field readMethod) {
        this.readMethod = readMethod;
    }
}
