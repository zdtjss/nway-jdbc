package com.nway.spring.jdbc.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.annotation.Table;

public class InsertBuilder extends SqlBuilder {

	private List<String> columns = new ArrayList<>();
	
	InsertBuilder(Class<?> beanClass) {
		super(beanClass);
	}
	
	public InsertBuilder use(Object obj) {
		Table table = obj.getClass().getAnnotation(Table.class);
		try {
			for (Field field : beanClass.getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					columns.add(column.name());
				}
				else {
					columns.add(ReflectUtils.fieldToColumn(field));
				}
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
		Table table = (Table) beanClass.getAnnotation(Table.class);
		sql.append("insert into ").append(table.name()).append(" (")
				.append(columns.stream().collect(Collectors.joining(",")))
				.append(") values (")
				.append(columns.stream().map(e -> "?").collect(Collectors.joining(",")))
				.append(")");
		return sql.toString();
	}
}
