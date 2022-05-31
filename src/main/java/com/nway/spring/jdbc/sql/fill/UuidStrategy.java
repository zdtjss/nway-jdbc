package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;

import java.util.UUID;

public class UuidStrategy implements FillStrategy {

    @Override
    public boolean isSupport(SqlType sqlType) {
        return SqlType.INSERT.equals(sqlType);
    }

    @Override
    public Object getValue(SqlType sqlType, Object val) {
        if (val == DEFAULT_NONE || val == null || "".equals(val)) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        return val;
    }

}
