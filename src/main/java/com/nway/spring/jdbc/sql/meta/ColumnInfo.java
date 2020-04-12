package com.nway.spring.jdbc.sql.meta;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.nway.spring.jdbc.sql.fill.FillStrategy;
import com.nway.spring.jdbc.sql.permission.PermissionStrategy;

import java.lang.invoke.MethodHandle;

public class ColumnInfo {

    private String columnName;
    private FillStrategy fillStrategy;
    private PermissionStrategy permissionStrategy;
    private int readIndex;
    private MethodAccess methodHandle;

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

    public int getReadIndex() {
        return readIndex;
    }

    public void setReadIndex(int readIndex) {
        this.readIndex = readIndex;
    }

    public MethodAccess getMethodHandle() {
        return methodHandle;
    }

    public void setMethodHandle(MethodAccess methodHandle) {
        this.methodHandle = methodHandle;
    }
}
