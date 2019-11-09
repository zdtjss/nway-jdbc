package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.sql.fill.FillStrategy;

public class TestFillStrategy implements FillStrategy {

	@Override
	public Object getValue(SqlType sqlType) {
		return Integer.MAX_VALUE;
	}

}
