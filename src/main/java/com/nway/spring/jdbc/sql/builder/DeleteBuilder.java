package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;

public class DeleteBuilder extends SqlBuilder {

	public DeleteBuilder(Class<?> beanClass) {
		super(beanClass);
	}

	@Override
	public String getSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(SqlBuilderUtils.getTableNameFromCache(beanClass)).append(" ")
				.append(super.getSql());
		return sql.toString();
	}

}
