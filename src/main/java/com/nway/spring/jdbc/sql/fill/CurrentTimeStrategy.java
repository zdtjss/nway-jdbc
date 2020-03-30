package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;

import java.util.Date;

public class CurrentTimeStrategy implements FillStrategy {

	@Override
	public Object getValue(SqlType sqlType) {
		
		return new Date();
	}

}
