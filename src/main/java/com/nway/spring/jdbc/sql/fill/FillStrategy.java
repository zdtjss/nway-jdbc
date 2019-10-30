package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;

public interface FillStrategy {

	Object getValue(SqlType sqlType);
}
