package com.nway.spring.jdbc.sql.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.annotation.enums.ColumnType;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;

public class BatchUpdateBuilder extends DefaultSqlBuilder {

	private List<String> sets = new ArrayList<>();

	public BatchUpdateBuilder(Class<?> beanClass) {
		super(beanClass);
	}

	public DefaultSqlBuilder use(List<Object> params) {
		List<List<Object>> batchParam = new ArrayList<>(params.size());
		for (int i = 0; i < params.size(); i++) {
			batchParam.add(new ArrayList<Object>());
		}
		try {
			for (Field field : getBeanClass().getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if (column != null && ColumnType.ID.equals(column.type())) {
					continue;
				}
				sets.add(SqlBuilderUtils.getColumnName(field) + " = ?");
				for (int i = 0; i < params.size(); i++) {
					Object columnValue = SqlBuilderUtils.getColumnValue(field, params.get(i), SqlType.UPDATE);
					batchParam.get(i).add(columnValue);
				}
			}
			getParam().addAll(batchParam);
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
		return this;
	}

	@Override
	public String getSql() {
		if (this.where().getSql().trim().endsWith(" where")) {
			throw new SqlBuilderException("请明确where条件");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(SqlBuilderUtils.getTableName(beanClass)).append(" set ")
				.append(sets.stream().collect(Collectors.joining(","))).append(super.getSql());
		return sql.toString();
	}
}
