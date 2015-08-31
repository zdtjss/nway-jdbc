package com.nway.spring.jdbc.bean;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

class DynamicBeanUtils {

	private static final String DYNAMIC_BEAN_PACKAGE = "com.nway.spring.jdbc.bean.";

	/**
	 * 
	 * 
	 * @param rsMetaData
	 * @param querying
	 *            {@link com.nway.spring.jdbc.SqlExecutor#extractQueryingColumn}
	 * @param className
	 * @return
	 * @throws SQLException
	 */
	static String makeCacheKey(ResultSet rs, String querying, String className) throws SQLException {

		String cacheKey = null;

		if (querying == null) {

			cacheKey = getColunmNames(rs.getMetaData()) + className;
		} else {

			cacheKey = querying + className;
		}

		return cacheKey;
	}

	/**
	 * 获取查询的所有列名，而非列的别名
	 *
	 * @param rsmd
	 *            {@link java.sql.ResultSetMetaData}
	 * @return 大写的所有列名（没有间隔符）
	 * @throws SQLException
	 */
	private static String getColunmNames(ResultSetMetaData rsmd) throws SQLException {

		StringBuilder columnNames = new StringBuilder(64);

		for (int i = 1; i <= rsmd.getColumnCount(); i++) {

			columnNames.append(rsmd.getColumnLabel(i));
		}

		return columnNames.toString();
	}

	static String getProcessorName(Class<?> type) {

		return DYNAMIC_BEAN_PACKAGE + type.getSimpleName() + System.nanoTime();
	}
}
