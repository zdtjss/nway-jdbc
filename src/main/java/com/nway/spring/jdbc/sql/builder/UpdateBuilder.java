package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.fill.NoneFillStrategy;
import com.nway.spring.jdbc.sql.function.SSupplier;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.List;

public class UpdateBuilder extends SqlBuilder {

	private final List<String> sets = new ArrayList<>();

	public UpdateBuilder(Class<?> beanClass) {
		super(beanClass);
	}
	
	@Override
	public <T> SqlBuilder set(SSupplier<T> val) {
		sets.add(SqlBuilderUtils.getColumn(beanClass, val) + " = ?");
		param.add(val.get());
		return this;
	}

	@Override
	public String getSql() {
		initFilled();
		String sql = "update " + SqlBuilderUtils.getTableNameFromCache(beanClass) + " set " +
				String.join(",", sets) + super.getSql();
		return sql;
	}

	private void initFilled() {
		EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
		for (ColumnInfo columnInfo : entityInfo.getColumnList().values()) {
			if (!NoneFillStrategy.class.equals(columnInfo.getFillStrategy().getClass())) {
				Object value = columnInfo.getFillStrategy().getValue(SqlType.INSERT);
				sets.add(columnInfo.getColumnName() + " = ?");
				param.add(value);
			}
		}
	}
}
