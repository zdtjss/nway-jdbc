package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.fill.NoneFillStrategy;
import com.nway.spring.jdbc.sql.function.SSupplier;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
		sql.append("update ").append(SqlBuilderUtils.getTableNameFromCache(beanClass)).append(" set ")
				.append(sets.stream().collect(Collectors.joining(","))).append(super.getSql());
		return sql.toString();
	}
	
	private void initFilled() {
		try {
			EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
			for(ColumnInfo columnInfo : entityInfo.getColumnList().values()) {
				if (!NoneFillStrategy.class.equals(columnInfo.getFillStrategy().getClass())) {
					Object value = columnInfo.getFillStrategy().getValue(SqlType.UPDATE);
					sets.add(columnInfo.getColumnName()  + " = ?");
					param.add(value);
				}
			}
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
	}
}
