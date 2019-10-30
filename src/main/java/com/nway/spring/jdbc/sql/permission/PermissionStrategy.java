package com.nway.spring.jdbc.sql.permission;

/**
 * 数据权限，基于数据库的数据权限，通常是使用where条件进行限制的
 * 
 * @author zhangdetao
 * @since 2019-10-30
 */
public interface PermissionStrategy {

	/**
	 * 
	 * @param table  表名
	 * @param column 字段名
	 * @param value  字段值
	 * @return 不支持时此表和字段时，请返回null
	 */
	String getSqlSegment(String column, Object value);
}
