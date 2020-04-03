package com.nway.spring.jdbc.sql.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.annotation.enums.ColumnType;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.fill.NoneFillStrategy;
import com.nway.spring.jdbc.sql.function.SSupplier;

public class UpdateBuilder extends SqlBuilder {

	private List<String> sets = new ArrayList<>();
	
	public UpdateBuilder(Class<?> beanClass) {
		super(beanClass);
	}
	
	public <T> SqlBuilder set(SSupplier<T> val) {
		sets.add(SqlBuilderUtils.getColumn(beanClass, val) + " = ?");
		param.add(val.get());
		return this;
	}
	
	@Override
	public String getSql() {
		initFilled();
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(SqlBuilderUtils.getTableName(beanClass)).append(" set ")
				.append(sets.stream().collect(Collectors.joining(","))).append(super.getSql());
		return sql.toString();
	}
	
	private void initFilled() {
		try {
			for (Field field : getBeanClass().getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if (column != null && ColumnType.ID.equals(column.type())) {
					continue;
				}
				if (column != null && !column.fillStrategy().equals(NoneFillStrategy.class)) {
					Object value = SqlBuilderUtils.getColumnValue(column.fillStrategy(), SqlType.UPDATE);
					sets.add(SqlBuilderUtils.getColumnName(field) + " = ?");
					param.add(value);
				}
			}
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
	}
}
