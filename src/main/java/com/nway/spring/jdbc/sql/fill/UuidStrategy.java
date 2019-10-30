package com.nway.spring.jdbc.sql.fill;

import java.util.UUID;

import com.nway.spring.jdbc.sql.SqlType;

public class UuidStrategy implements FillStrategy {

	@Override
	public Object getValue(SqlType sqlType) {

		return UUID.randomUUID().toString().replace("-", "");
	}

}
