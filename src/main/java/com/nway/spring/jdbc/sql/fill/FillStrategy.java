package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;

public interface FillStrategy {

	boolean isSupport(SqlType sqlType);

	/**
	 *
	 *
	 * @param fieldValue {@link NoneValue} 表示没有初始值
	 * @param sqlType
	 * @return
	 */
	Object getValue(Object fieldValue, SqlType sqlType);
}
