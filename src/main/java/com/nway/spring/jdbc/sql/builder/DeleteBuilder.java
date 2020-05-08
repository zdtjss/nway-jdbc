package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;

public class DeleteBuilder extends SqlBuilder {

	public DeleteBuilder(Class<?> beanClass) {
		super(beanClass);
	}

	@Override
	public String getSql() {
		return "delete from " + SqlBuilderUtils.getTableNameFromCache(beanClass)
				+ " " + super.getSql();
	}

}
