package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.fill.incrementer.IdWorker;

public class NumberIdStrategy implements FillStrategy {

	@Override
	public Object getValue(SqlType sqlType) {
		
		return IdWorker.getId();
	}

}
