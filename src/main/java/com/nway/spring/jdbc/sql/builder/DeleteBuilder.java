package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;

public class DeleteBuilder extends DefaultSqlBuilder {

	private Table table;
	
	public DeleteBuilder(Class<?> beanClass) {
		super(beanClass);
		this.table = (Table) beanClass.getAnnotation(Table.class);
	}

	@Override
	public String getSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(SqlBuilderUtils.getTableName(table)).append(" ")
				.append(super.getSql());
		return sql.toString();
	}

}
