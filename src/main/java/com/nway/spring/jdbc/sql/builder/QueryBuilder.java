package com.nway.spring.jdbc.sql.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.function.SFunction;

public class QueryBuilder extends DefaultSqlBuilder {

	private List<String> columns = new ArrayList<>();
	
	public QueryBuilder(Class<?> beanClass) {
		super(beanClass);
	}
	
	public <T, R> QueryBuilder withColumn(SFunction<T, R> field) {
		columns.add(SqlBuilderUtils.getColumn(beanClass, field));
		return this;
	}

	@Override
	public String getSql() {
		try {
			for (Field field : getBeanClass().getDeclaredFields()) {
				String whereCondition = SqlBuilderUtils.getWhereCondition(field);
				if (whereCondition != null && whereCondition.trim().length() > 0) {
					this.where().appendWhereCondition(whereCondition);
				}
			}
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
		return getSelectStmt() + super.getSql();
	}
	
	private String getSelectStmt() {
		StringBuilder sql = new StringBuilder();
		if (columns.size() > 0) {
			sql.append("select ");
			for(String column : columns) {
				sql.append(column).append(',');
			}
			sql.deleteCharAt(sql.length() - 1).append(" from ").append(SqlBuilderUtils.getTableName(beanClass));
		}
		else {
			sql.append("select * from ").append(SqlBuilderUtils.getTableName(beanClass));
		}
		return sql.toString();
	}
	
}
