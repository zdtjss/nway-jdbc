package com.nway.spring.jdbc.sql.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.function.SSupplier;

public class UpdateBuilder extends DefaultSqlBuilder {

	private List<String> sets = new ArrayList<>();
	
	public UpdateBuilder(Class<?> beanClass) {
		super(beanClass);
	}
	
	public <T> DefaultSqlBuilder set(SSupplier<T> val) {
		sets.add(SqlBuilderUtils.getColumn(beanClass, val) + " = ?");
		param.add(val.get());
		return this;
	}
	
	@Override
	public String getSql() {

		Table table = (Table) beanClass.getAnnotation(Table.class);

		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(table.value().length() > 0 ? table.value() : table.name()).append(" set ")
				.append(sets.stream().collect(Collectors.joining(","))).append(super.getSql());

		return sql.toString();
	}
}
