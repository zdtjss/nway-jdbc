package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
			Object obj = getBeanClass().getDeclaredConstructor().newInstance();
			EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
			for(ColumnInfo columnInfo : entityInfo.getColumnList().values()) {
				Object value = SqlBuilderUtils.getColumnValue(columnInfo, obj, SqlType.INSERT);
				if (value != null && !value.equals(columnInfo.getMethodHandle().invoke(obj))) {
					sets.add(columnInfo.getColumnName() + " = ?");
					setsParam.add(value);
				}
			}
		} catch (Throwable e) {
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
