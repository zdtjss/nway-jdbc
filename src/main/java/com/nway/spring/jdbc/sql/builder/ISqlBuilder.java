package com.nway.spring.jdbc.sql.builder;

import java.util.List;

public interface ISqlBuilder {
	
	<T> Class<T> getBeanClass();
	
	String getSql();
	
	List<Object> getParam();

	void setTableName(String tableName);
}
