package com.nway.spring.jdbc.sql.builder;

import java.util.List;

public interface SqlBuilder {
	
	<T> Class<T> getBeanClass();
	
	String getSql();
	
	List<Object> getParam();
}
