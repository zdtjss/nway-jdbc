package com.nway.spring.jdbc.sql.permission;

public class NonePermissionStrategy implements PermissionStrategy {

	@Override
	public String getSqlSegment(String column) {
		return "";
	}

}
