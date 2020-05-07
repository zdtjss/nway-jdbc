package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.fill.incrementer.IdWorker;

public class NumberIdStrategy implements FillStrategy {

    @Override
    public boolean isSupport(SqlType sqlType) {
        return SqlType.INSERT.equals(sqlType);
    }

    @Override
    public Object getValue(Object fieldValue, SqlType sqlType) {

        return IdWorker.getId();
    }

}
