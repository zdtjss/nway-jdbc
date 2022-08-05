package com.nway.spring.jdbc.sql.permission;

import com.nway.spring.jdbc.sql.SqlType;

public class NonePermissionStrategy implements PermissionStrategy {

    @Override
    public WhereCondition getSqlSegment(SqlType sqlType, String column) {
        return null;
    }

}
