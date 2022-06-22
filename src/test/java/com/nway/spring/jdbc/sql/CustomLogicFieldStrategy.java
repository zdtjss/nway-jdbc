package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.sql.permission.WhereCondition;

public class CustomLogicFieldStrategy extends LogicFieldStrategy {

    @Override
    public boolean isSupport(SqlType sqlType) {
        return true;
    }

    @Override
    public Object getValue(SqlType sqlType, Object val) {
        if (val == DEFAULT_NONE || val == null) {
            return SqlType.DELETE == sqlType ? 1 : 0;
        }
        return val;
    }

    @Override
    public WhereCondition getSqlSegment(String column) {
        return new WhereCondition(column + " = ? ", 0);
    }
}
