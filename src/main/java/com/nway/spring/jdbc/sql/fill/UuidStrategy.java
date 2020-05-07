package com.nway.spring.jdbc.sql.fill;

import java.util.UUID;

import com.nway.spring.jdbc.sql.SqlType;

public class UuidStrategy implements FillStrategy {

    @Override
    public boolean isSupport(SqlType sqlType) {
        return SqlType.INSERT.equals(sqlType);
    }

    @Override
    public Object getValue(Object fieldValue, SqlType sqlType) {

        return UUID.randomUUID().toString().replace("-", "");
    }

}
