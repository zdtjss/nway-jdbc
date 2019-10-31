package com.nway.spring.jdbc.sql.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;

public class BatchInsertBuilder implements SqlBuilder {

	private List<String> columns = new ArrayList<>();
	
	private StringBuilder sql = new StringBuilder();
	private List<Object> param = new ArrayList<>();
	private Class beanClass;
	private Table table;
	
	public BatchInsertBuilder(Class<?> beanClass) {
		this.beanClass = beanClass;
		this.table = (Table) beanClass.getAnnotation(Table.class);
	}
	
	public BatchInsertBuilder use(List<Object> objList) {
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
			.append(table.value().length() > 0 ? table.value() : table.name()).append(" (")
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
