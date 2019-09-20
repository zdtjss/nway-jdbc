package com.nway.spring.jdbc.sql;

public class SqlBuilder {

	public static QueryBuilder query(Class<?> beanClass, String ... columns) {
		
		return new QueryBuilder(beanClass, columns);
	}
}
