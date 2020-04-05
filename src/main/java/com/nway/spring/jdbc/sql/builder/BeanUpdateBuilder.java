package com.nway.spring.jdbc.sql.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.annotation.enums.ColumnType;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;

public class BeanUpdateBuilder extends SqlBuilder {

	private List<String> sets = new ArrayList<>();
	private List<Object> setsParam = new ArrayList<>();
	private Object obj;
	
	public BeanUpdateBuilder(Object obj) {
		super(obj.getClass());
		this.obj = obj;
	}
	
	private void init() {
		try {
			for (Field field : getBeanClass().getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if (column != null && ColumnType.ID.equals(column.type())) {
					continue;
				}
				Object value = SqlBuilderUtils.getColumnValue(field, obj, SqlType.UPDATE);
				field.setAccessible(true);
				if (value != null && !value.equals(field.get(getBeanClass().getDeclaredConstructor().newInstance()))) {
					sets.add(SqlBuilderUtils.getColumnName(field) + " = ?");
					setsParam.add(value);
				}
			}
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
	}
	
	@Override
	public String getSql() {
		init();
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(SqlBuilderUtils.getTableName(beanClass)).append(" set ")
				.append(sets.stream().collect(Collectors.joining(","))).append(super.getSql());
		return sql.toString();
	}
	
	@Override
	public List<Object> getParam() {
		setsParam.addAll(super.getParam());
		return setsParam;
	}
}
