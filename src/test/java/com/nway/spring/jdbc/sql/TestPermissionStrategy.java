package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.sql.permission.PermissionStrategy;

public class TestPermissionStrategy implements PermissionStrategy {

	@Override
	public String getSqlSegment(String column) {
		return column + " = 1";
	}

}
