package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.sql.permission.PermissionStrategy;
import com.nway.spring.jdbc.sql.permission.WhereCondition;

import java.util.Date;

public class MultiColumnPermissionStrategy implements PermissionStrategy {

    @Override
    public WhereCondition getSqlSegment(String column) {
        return new WhereCondition("( " + column + " <> ? or keyboard_id <> ? )", new Object[]{new Date(), "bbb"});
    }

}
