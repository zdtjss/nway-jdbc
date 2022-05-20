package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;

public class NoneFillStrategy implements FillStrategy {

	@Override
	public boolean isSupport(SqlType sqlType) {
		return false;
	}

	@Override
	public Object getValue(SqlType sqlType, Object val) {
		return "";
	}

}
