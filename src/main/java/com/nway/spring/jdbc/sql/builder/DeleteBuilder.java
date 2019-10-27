package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;

public class DeleteBuilder extends DefaultSqlBuilder {

	public DeleteBuilder(Class<?> beanClass) {
		super(beanClass);
	}

	@Override
	public String getSql() {

		Table table = (Table) beanClass.getAnnotation(Table.class);

		StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(SqlBuilderUtils.getTableName(table)).append(" ")
				.append(super.getSql());

		return sql.toString();
	}

}
