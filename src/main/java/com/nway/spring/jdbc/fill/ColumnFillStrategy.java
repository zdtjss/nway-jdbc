package com.nway.spring.jdbc.fill;

import com.nway.spring.jdbc.sql.SqlType;

public interface ColumnFillStrategy<T> {

	T getValue();

	boolean isSupport(SqlType sqlType, String table, String column);
}
