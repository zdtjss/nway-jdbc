package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.fill.NoneValue;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;
import com.nway.spring.jdbc.sql.permission.NonePermissionStrategy;
import com.nway.spring.jdbc.sql.permission.WhereCondition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QueryBuilder extends SqlBuilder {

	protected final Log logger = LogFactory.getLog(QueryBuilder.class);

	private final List<String> columns = new ArrayList<>();

	public QueryBuilder(Class<?> beanClass) {
		super(beanClass);
	}
	
	public <T, R> QueryBuilder withColumn(SFunction<T, R>... fields) {
		for(SFunction<T, R> field : fields) {
			columns.add(SqlBuilderUtils.getColumn(beanClass, field));
		}
		return this;
	}

	public <T, R> QueryBuilder withColumn(String... columnNames) {
		columns.addAll(Arrays.asList(columnNames));
		return this;
	}

	@Override
	public <T> SqlBuilder where() {
		initPermission();
		return super.where();
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
