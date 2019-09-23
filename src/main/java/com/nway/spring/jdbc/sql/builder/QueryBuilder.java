package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.annotation.Table;

public class QueryBuilder extends SqlBuilder {

	private String[] columns;
	
	public QueryBuilder(Class<?> beanClass, String ... columns) {
		super(beanClass);
		this.columns = columns;
		init();
	}
	
	private void init() {
		if (columns.length > 0) {
			initColumns(columns);
		}
		else {
			initColumns();
		}
	}

	private void initColumns() {
		Table table = (Table) beanClass.getAnnotation(Table.class);
		sql.append("select *  from ").append(table.name());
	}
	
	private void initColumns(String ... columns) {
		Table table = (Table) beanClass.getAnnotation(Table.class);
		sql.append("select ");
		for(String column : columns) {
			sql.append(column).append(',');
		}
		sql.deleteCharAt(sql.length() - 1).append(" from ").append(table.name());
	}
	
}
