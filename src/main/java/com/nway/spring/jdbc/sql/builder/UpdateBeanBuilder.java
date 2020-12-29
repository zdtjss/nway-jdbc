package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.List;

public class UpdateBeanBuilder extends SqlBuilder {

	private final List<String> sets = new ArrayList<>();
	private final List<Object> setsParam = new ArrayList<>();
	private final Object obj;
	
	public UpdateBeanBuilder(Object obj) {
		super(obj.getClass());
		this.obj = obj;
	}

	private void init() {
		Object defaultObj;
		try {
			defaultObj = getBeanClass().getConstructor().newInstance();
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
		EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
		for (ColumnInfo columnInfo : entityInfo.getColumnList().values()) {
			if (columnInfo == entityInfo.getId()) {
				continue;
			}
			Object value = SqlBuilderUtils.getColumnValue(columnInfo, obj, SqlType.UPDATE);
			if (value != null && !value.equals(columnInfo.getMethodHandle().invoke(defaultObj, columnInfo.getReadIndex()))) {
				sets.add(columnInfo.getColumnName() + " = ?");
				setsParam.add(value);
			}
		}
	}
	
	@Override
	public String getSql() {
		init();
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(SqlBuilderUtils.getTableNameFromCache(beanClass)).append(" set ")
				.append(String.join(",", sets)).append(super.getSql());
		return sql.toString();
	}
	
	@Override
	public List<Object> getParam() {
		setsParam.addAll(super.getParam());
		return setsParam;
	}
}
