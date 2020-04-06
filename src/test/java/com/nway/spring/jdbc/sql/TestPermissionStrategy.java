package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.sql.permission.PermissionStrategy;
import com.nway.spring.jdbc.sql.permission.WhereCondition;

import java.util.Date;

public class TestPermissionStrategy implements PermissionStrategy {

    @Override
    public WhereCondition getSqlSegment(String column) {
        return new WhereCondition(column, new Date[]{new Date(), new Date()}, " between ? and ? ");
    }

}
