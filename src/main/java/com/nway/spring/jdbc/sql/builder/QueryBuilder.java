package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;

public class QueryBuilder extends DefaultSqlBuilder {

	private String[] columns;
	
	public QueryBuilder(Class<?> beanClass, String ... columns) {
		super(beanClass);
		this.columns = columns;
		init();
	}
	
	private void init() {
		if (columns.length > 0) {
			initSql(columns);
		}
		else {
			initSql();
		}
	}

	private void initSql() {
		Table table = (Table) beanClass.getAnnotation(Table.class);
		sql.append("select *  from ").append(SqlBuilderUtils.getTableName(table));
	}
	
	private void initSql(String ... columns) {
		Table table = (Table) beanClass.getAnnotation(Table.class);
		sql.append("select ");
		for(String column : columns) {
			sql.append(column).append(',');
		}
		sql.deleteCharAt(sql.length() - 1).append(" from ").append(SqlBuilderUtils.getTableName(table));
	}
	
}
