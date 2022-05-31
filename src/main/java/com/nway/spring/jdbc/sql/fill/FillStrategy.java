package com.nway.spring.jdbc.sql.fill;

import com.nway.spring.jdbc.sql.SqlType;

public interface FillStrategy {

	Object DEFAULT_NONE = new Object();

	boolean isSupport(SqlType sqlType);

	/**
	 *
	 *
	 * @param sqlType
	 * @return
	 */
	Object getValue(SqlType sqlType, Object val);
}
