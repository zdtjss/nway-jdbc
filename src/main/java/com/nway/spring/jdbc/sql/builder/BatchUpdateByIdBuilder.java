package com.nway.spring.jdbc.sql.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.annotation.enums.ColumnType;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;

public class BatchUpdateByIdBuilder implements SqlBuilder {

	protected StringBuilder sql = new StringBuilder();
	protected List<Object> param = new ArrayList<>();
	private List<String> sets = new ArrayList<>();
	private List<Object> idVals = new ArrayList<>();
	protected Class beanClass;
	private String idName = "";
	
	public BatchUpdateByIdBuilder(Class<?> beanClass) {
		this.beanClass = beanClass;
	}
	
	public SqlBuilder use(List<?> params) {
		List<List<Object>> batchParam = new ArrayList<>(params.size());
		for(int i = 0;i < params.size(); i++) {
			batchParam.add(new ArrayList<>());
		}
		try {
			for (Field field : getBeanClass().getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if (column != null && ColumnType.ID.equals(column.type())) {
					for(int i = 0;i < params.size(); i++) {
						idVals.add(SqlBuilderUtils.getColumnValue(field, params.get(i), SqlType.UPDATE));
					}
					idName = SqlBuilderUtils.getColumnName(field);
					continue;
				}
				Object value = SqlBuilderUtils.getColumnValue(field, params.get(0), SqlType.UPDATE);
				field.setAccessible(true);
				if (value == null || value.equals(field.get(getBeanClass().newInstance()))) {
					continue;
				}
				sets.add(SqlBuilderUtils.getColumnName(field) + " = ?");
				for (int i = 0; i < params.size(); i++) {
					Object columnValue = SqlBuilderUtils.getColumnValue(field, params.get(i), SqlType.UPDATE);
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
		sql.append("update ").append(SqlBuilderUtils.getTableName(beanClass)).append(" set ")
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
