package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.sql.fill.FillStrategy;

public class TestFillStrategy implements FillStrategy {

    @Override
    public boolean isSupport(SqlType sqlType) {
        return SqlType.INSERT == sqlType;
    }

    @Override
    public Object getValue(SqlType sqlType, Object val) {

        return (int) (Math.random() * 10000);
    }

}
