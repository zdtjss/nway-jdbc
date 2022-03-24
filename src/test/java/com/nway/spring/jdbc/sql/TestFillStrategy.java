package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.sql.fill.FillStrategy;

public class TestFillStrategy implements FillStrategy {

    @Override
    public boolean isSupport(SqlType sqlType) {
        return SqlType.INSERT == sqlType || SqlType.UPDATE == sqlType;
    }

    @Override
    public Object getValue(SqlType sqlType) {
        return (int) (Math.random() * 10000);
    }

}
