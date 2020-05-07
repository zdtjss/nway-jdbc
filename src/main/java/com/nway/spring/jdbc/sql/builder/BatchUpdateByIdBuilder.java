package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BatchUpdateByIdBuilder implements ISqlBuilder {

	protected StringBuilder sql = new StringBuilder();
	protected List<Object> param = new ArrayList<>();
	private final List<String> sets = new ArrayList<>();
	private final List<Object> idVals = new ArrayList<>();
	protected Class beanClass;
	private String idName = "";
	
	public BatchUpdateByIdBuilder(Class<?> beanClass) {
		this.beanClass = beanClass;
	}
	
	public ISqlBuilder use(List<?> params) {
		List<List<Object>> batchParam = new ArrayList<>(params.size());
		for(int i = 0;i < params.size(); i++) {
			batchParam.add(new ArrayList<>());
		}
		try {
			EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
			idName = entityInfo.getId().getColumnName();
			for(int i = 0;i < params.size(); i++) {
				Object columnValue = SqlBuilderUtils.getColumnValue(entityInfo.getId(), params.get(i), SqlType.UPDATE);
				idVals.add(columnValue);
			}
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
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(SqlBuilderUtils.getTableNameFromCache(beanClass)).append(" set ")
				.append(sets.stream().collect(Collectors.joining(",")));
		sql.append(" where ").append(idName).append(" = ?");
		for (int i = 0; i < getParam().size(); i++) {
			Object idVal = idVals.get(i);
			if(idVal == null) {
				throw new SqlBuilderException("批量更新时存在主键为空的数据，更新失败。");
			}
			((List) getParam().get(i)).add(idVal);
		}
		return sql.toString();
	}

	@Override
	public <T> Class<T> getBeanClass() {
		return beanClass;
	}

	@Override
	public List<Object> getParam() {
		return param;
	}
}
