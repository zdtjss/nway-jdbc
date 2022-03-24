package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.sql.fill.FillStrategy;
import com.nway.spring.jdbc.sql.permission.PermissionStrategy;
import com.nway.spring.jdbc.sql.permission.WhereCondition;

/**
 * 逻辑删除
 */
public class LogicFieldStrategy implements FillStrategy, PermissionStrategy {

    @Override
    public boolean isSupport(SqlType sqlType) {
        return SqlType.INSERT == sqlType || SqlType.DELETE == sqlType;
    }

    @Override
    public Object getValue(SqlType sqlType) {
        return SqlType.INSERT == sqlType ? 0 : 1;
    }

    @Override
    public WhereCondition getSqlSegment(String column) {
        return new WhereCondition(column + " = ? ", 0);
    }
}
