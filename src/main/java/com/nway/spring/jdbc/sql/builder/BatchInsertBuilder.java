package com.nway.spring.jdbc.sql.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;

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
					batchParam.get(i).add(field.get(objList.get(i)));
				}
			}
		} catch (Exception e) {
			// TODO
			return null;
		}
		param.addAll(batchParam.stream().map(e -> e.toArray()).collect(Collectors.toList()));
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
