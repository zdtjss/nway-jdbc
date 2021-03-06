package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;

public interface FillStrategy {

	/**
	 * 如果返回null，会被忽略 
	 * 
	 * @param sqlType
	 * @return
	 */
	Object getValue(SqlType sqlType);
}
