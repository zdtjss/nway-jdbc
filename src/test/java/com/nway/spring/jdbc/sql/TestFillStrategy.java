package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.sql.fill.FillStrategy;

public class TestFillStrategy implements FillStrategy {

    @Override
    public boolean isSupport(SqlType sqlType) {
        return SqlType.INSERT == sqlType || SqlType.UPDATE == sqlType || SqlType.SELECT == sqlType;
    }

    @Override
    public Object getValue(SqlType sqlType, Object val) {
        Object v;
        switch (sqlType) {
            case INSERT:
                v = (int) (Math.random() * 10000);
                break;
            case UPDATE:
                v = Integer.parseInt(val.toString()) + 1;
                break;
            case SELECT:
                v = Integer.parseInt(val.toString()) + 10;
                break;
            default:
                v = null;
        }
        return v;
    }

}
