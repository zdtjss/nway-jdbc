package com.nway.spring.jdbc.sql.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;

public class InsertBuilder extends DefaultSqlBuilder {

	private List<String> columns = new ArrayList<>();
	
	public InsertBuilder(Class<?> beanClass) {
		super(beanClass);
	}
	
	public InsertBuilder use(Object obj) {
		try {
			for (Field field : beanClass.getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					columns.add(column.value().length() > 0 ? column.value() : column.name());
				}
				else {
					columns.add(SqlBuilderUtils.fieldToColumn(field));
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
		sql.append("insert into ")
			.append(table.value().length() > 0 ? table.value() : table.name()).append(" (")
			.append(columns.stream().collect(Collectors.joining(",")))
			.append(") values (")
			.append(columns.stream().map(e -> "?").collect(Collectors.joining(",")))
			.append(")");
		return sql.toString();
	}
}
