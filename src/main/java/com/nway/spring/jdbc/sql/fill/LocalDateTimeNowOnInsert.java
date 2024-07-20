package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;

import java.time.LocalDateTime;

public class LocalDateTimeNowOnInsert implements FillStrategy {

    @Override
    public boolean isSupport(SqlType sqlType) {
        return SqlType.INSERT == sqlType;
    }

    @Override
    public Object getValue(SqlType sqlType, Object val) {
        if (val == DEFAULT_NONE || val == null) {
            return LocalDateTime.now();
        }
        return val;
    }

}
