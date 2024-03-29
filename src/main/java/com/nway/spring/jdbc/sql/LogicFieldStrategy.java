package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.sql.fill.FillStrategy;
import com.nway.spring.jdbc.sql.permission.PermissionStrategy;
import com.nway.spring.jdbc.sql.permission.WhereCondition;

/**
 * 自定义逻辑删除类时，需要继承此类
 */
public class LogicFieldStrategy implements FillStrategy, PermissionStrategy {

    @Override
    public boolean isSupport(SqlType sqlType) {
        return true;
    }

    @Override
    public Object getValue(SqlType sqlType, Object val) {
        if (SqlType.SELECT != sqlType && (val == DEFAULT_NONE || val == null)) {
            return SqlType.DELETE == sqlType;
        }
        return val;
    }

    @Override
    public WhereCondition getSqlSegment(SqlType sqlType, String column) {
        return new WhereCondition(column + " = ? ", false);
    }
}
