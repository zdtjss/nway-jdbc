package com.nway.spring.jdbc.sql.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;

public class InsertBuilder implements SqlBuilder {

	private List<String> columns = new ArrayList<>();
	
	private StringBuilder sql = new StringBuilder();
	private List<Object> param = new ArrayList<>();
	private Class beanClass;
	private Table table;
	
	public InsertBuilder(Class<?> beanClass) {
		this.beanClass = beanClass;
		table = (Table) beanClass.getAnnotation(Table.class);
	}
	
	public InsertBuilder use(Object obj) {
		try {
			for (Field field : beanClass.getDeclaredFields()) {
				columns.add(SqlBuilderUtils.getColumnName(field));
				field.setAccessible(true);
				param.add(field.get(obj));
			}
		} catch (Exception e) {
			// TODO
			return null;
		}
		return this;
	}
	
	@Override
	public String getSql() {
		sql.append("insert into ")
			.append(SqlBuilderUtils.getTableName(table)).append(" (")
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
