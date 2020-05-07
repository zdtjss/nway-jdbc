package com.nway.spring.jdbc.sql.permission;

public class NonePermissionStrategy implements PermissionStrategy {

    @Override
    public WhereCondition getSqlSegment(String column, Object columnValue) {
        return null;
    }

}
