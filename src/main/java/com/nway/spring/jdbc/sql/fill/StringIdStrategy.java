package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.fill.incrementer.IdWorker;

public class StringIdStrategy implements FillStrategy {

	@Override
	public Object getValue(SqlType sqlType) {
		
		return IdWorker.getIdStr();
	}

}
