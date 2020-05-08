package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BatchUpdateBuilder extends SqlBuilder {

	private final List<String> sets = new ArrayList<>();

	public BatchUpdateBuilder(Class<?> beanClass) {
		super(beanClass);
	}

	public SqlBuilder use(List<Object> params) {
		List<List<Object>> batchParam = new ArrayList<>(params.size());
		for (int i = 0; i < params.size(); i++) {
			batchParam.add(new ArrayList<Object>());
		}
		try {
			EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
			for(ColumnInfo columnInfo : entityInfo.getColumnList().values()) {
				sets.add(columnInfo.getColumnName() + " = ?");
				for(int i = 0;i < params.size(); i++) {
					Object columnValue = SqlBuilderUtils.getColumnValue(columnInfo, params.get(i), SqlType.UPDATE);
					batchParam.get(i).add(columnValue);
				}
			}
			getParam().addAll(batchParam);
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
		return this;
	}

	@Override
	public String getSql() {
		if (this.where().getSql().trim().endsWith(" where")) {
			throw new SqlBuilderException("请明确where条件");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(SqlBuilderUtils.getTableNameFromCache(beanClass)).append(" set ")
				.append(String.join(",", sets)).append(super.getSql());
		return sql.toString();
	}
}
