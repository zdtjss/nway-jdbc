package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.function.SFunction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryBuilder<T> extends SqlBuilder {

	protected final Log logger = LogFactory.getLog(QueryBuilder.class);

	private final List<String> columns = new ArrayList<>();

	public QueryBuilder(Class<T> beanClass) {
		super(beanClass);
	}

	@SafeVarargs
	public final QueryBuilder<T> withColumn(SFunction<T, ?>... fields) {
		for(SFunction<T, ?> field : fields) {
			columns.add(SqlBuilderUtils.getColumn(beanClass, field));
		}
		return this;
	}

	public QueryBuilder<T> withColumn(String... columnNames) {
		columns.addAll(Arrays.asList(columnNames));
		return this;
	}

	@Override
	public SqlBuilder where() {
		super.where();
		initPermission();
		return this;
	}

	@Override
	public String getSql() {
		return getSelectStmt() + super.getSql();
	}

	private String getSelectStmt() {
		StringBuilder sql = new StringBuilder();
		if (columns.size() > 0) {
			sql.append("select ");
			for(String column : columns) {
				sql.append(column).append(',');
			}
			sql.deleteCharAt(sql.length() - 1).append(" from ").append(SqlBuilderUtils.getTableNameFromCache(beanClass));
		}
		else {
			sql.append("select * from ").append(SqlBuilderUtils.getTableNameFromCache(beanClass));
		}
		return sql.toString();
	}
	
}
