package com.nway.spring.jdbc.bean;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DynamicClassUtils {

	private static final String DYNAMIC_BEAN_PACKAGE = "com.nway.spring.jdbc.bean.";

	/**
	 * 获取查询的所有列名，而非列的别名
	 *
	 * @param rsmd
	 *            {@link java.sql.ResultSetMetaData}
	 * @return 大写的所有列名（没有间隔符）
	 * @throws SQLException
	 */
	public static String makeCacheKey(ResultSet rs, String className) throws SQLException {

		ResultSetMetaData rsmd = rs.getMetaData();
		
		StringBuilder columnNames = new StringBuilder(64);

		for (int i = 1; i <= rsmd.getColumnCount(); i++) {

			columnNames.append(rsmd.getColumnLabel(i));
		}
		
		return columnNames.toString() + className;
	}

	public static String getProcessorName(Class<?> type) {

		return DYNAMIC_BEAN_PACKAGE + type.getSimpleName() + System.nanoTime();
	}
}
