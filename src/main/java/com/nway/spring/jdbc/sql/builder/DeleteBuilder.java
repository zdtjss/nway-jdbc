package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.annotation.Table;

public class DeleteBuilder extends DefaultSqlBuilder {

	public DeleteBuilder(Class<?> beanClass) {
		super(beanClass);
	}
	
	@Override
	public String getSql() {

		Table table = (Table) beanClass.getAnnotation(Table.class);

		StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(table.name()).append(" ").append(super.getSql());

		return sql.toString();
	}
}
