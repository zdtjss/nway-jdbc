package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.fill.incrementer.IdWorker;

public class StringIdStrategy implements FillStrategy {

    @Override
    public boolean isSupport(SqlType sqlType) {
        return SqlType.INSERT.equals(sqlType);
    }

    @Override
    public Object getValue(SqlType sqlType, Object val) {
        if (val == DEFAULT_NONE || val == null || "".equals(val)) {
            return IdWorker.getIdStr();
        }
        return val;
    }

}
