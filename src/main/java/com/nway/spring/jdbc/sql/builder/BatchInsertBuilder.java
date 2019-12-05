package com.nway.spring.jdbc.sql.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;

public class BatchInsertBuilder implements SqlBuilder {

	private List<String> columns = new ArrayList<>();
	
	private StringBuilder sql = new StringBuilder();
	private List<Object> param = new ArrayList<>();
	private Class beanClass;
	
	public BatchInsertBuilder(Class<?> beanClass) {
		this.beanClass = beanClass;
	}
	
	public BatchInsertBuilder use(List<? extends Object> objList) {
		List<List<Object>> batchParam = new ArrayList<>(objList.size());
		for(int i = 0;i < objList.size(); i++) {
			batchParam.add(new ArrayList<Object>());
		}
		try {
			for (Field field : beanClass.getDeclaredFields()) {
				columns.add(SqlBuilderUtils.getColumnName(field));
				field.setAccessible(true);
				for(int i = 0;i < objList.size(); i++) {
					Object columnValue = SqlBuilderUtils.getColumnValue(field, objList.get(i), SqlType.INSERT);
					batchParam.get(i).add(columnValue);
				}
			}
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
		param.addAll(batchParam);
		return this;
	}
	
	@Override
	public String getSql() {
		sql.append("insert into ")
			.append(SqlBuilderUtils.getTableName(beanClass)).append(" (")
			.append(columns.stream().collect(Collectors.joining(",")))
			.append(") values (")
			.append(columns.stream().map(e -> "?").collect(Collectors.joining(",")))
			.append(")");
		return sql.toString();
	}

	@Override
	public <T> Class<T> getBeanClass() {
		return this.beanClass;
	}

	@Override
	public List<Object> getParam() {
		return this.param;
	}
}
