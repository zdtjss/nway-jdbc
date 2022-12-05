package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.sql.fill.FillStrategy;
import com.nway.spring.jdbc.sql.permission.PermissionStrategy;
import com.nway.spring.jdbc.sql.permission.WhereCondition;

/**
 * 逻辑删除
 */
public class LogicFieldIntStrategy implements FillStrategy, PermissionStrategy {

    @Override
    public boolean isSupport(SqlType sqlType) {
        return true;
    }

    @Override
    public Object getValue(SqlType sqlType, Object val) {
        if (SqlType.SELECT != sqlType && (val == DEFAULT_NONE || val == null)) {
            return SqlType.DELETE == sqlType ? 1 : 0;
        }
        return val;
    }

    @Override
    public WhereCondition getSqlSegment(SqlType sqlType, String column) {
        return new WhereCondition(column + " = ? ", 0);
    }
}
