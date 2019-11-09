package com.nway.spring.jdbc.sql.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;

public class BatchUpdateBuilder extends DefaultSqlBuilder {

	private List<String> sets = new ArrayList<>();
	private Table table;
	
	public BatchUpdateBuilder(Class<?> beanClass) {
		super(beanClass);
		table = (Table) beanClass.getAnnotation(Table.class);
	}
	
	public DefaultSqlBuilder use(List<Object> params) {
		List<List<Object>> batchParam = new ArrayList<>(params.size());
		for(int i = 0;i < params.size(); i++) {
			batchParam.add(new ArrayList<Object>());
		}
		try {
			for (Field field : getBeanClass().getDeclaredFields()) {
				sets.add(SqlBuilderUtils.getColumnName(field) + " = ?");
				for(int i = 0;i < params.size(); i++) {
					Object columnValue = SqlBuilderUtils.getColumnValue(field, params.get(i), SqlType.UPDATE);
					batchParam.get(i).add(columnValue);
				}
			}
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
		return this;
	}
	
	@Override
	public String getSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(SqlBuilderUtils.getTableName(table)).append(" set ")
				.append(sets.stream().collect(Collectors.joining(","))).append(super.getSql());
		return sql.toString();
	}
}
