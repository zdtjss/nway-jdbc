package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;

public class NoneFillStrategy implements FillStrategy {

	@Override
	public Object getValue(SqlType sqlType) {
		
		return IdWorker.getId();
	}

}
