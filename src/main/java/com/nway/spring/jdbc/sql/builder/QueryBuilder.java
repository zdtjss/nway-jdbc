package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;
import com.nway.spring.jdbc.sql.permission.WhereCondition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueryBuilder extends SqlBuilder {

	protected final Log logger = LogFactory.getLog(QueryBuilder.class);

	private List<String> columns = new ArrayList<>();
	
	public QueryBuilder(Class<?> beanClass) {
		super(beanClass);
	}
	
	public <T, R> QueryBuilder withColumn(SFunction<T, R>... fields) {
		for(SFunction<T, R> field : fields) {
			columns.add(SqlBuilderUtils.getColumn(beanClass, field));
		}
		return this;
	}

	public <T, R> QueryBuilder withColumn(String... columnNames) {
		for(String column : columnNames) {
			columns.add(column);
		}
		return this;
	}

	@Override
	public String getSql() {
		logger.debug("开始构建sql 。。。");
		try {
			EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
			for(ColumnInfo columnInfo : entityInfo.getColumnList().values()) {
				WhereCondition whereCondition = SqlBuilderUtils.getWhereCondition(columnInfo);
				if (whereCondition != null && whereCondition.getColumn().length() > 0) {
					this.where().appendWhereCondition(whereCondition.getColumn() + whereCondition.getExpr());
					if(whereCondition.getValue() instanceof Collection) {
						getParam().addAll((Collection) whereCondition.getValue());
					}
					if(ObjectUtils.isArray(whereCondition.getValue())) {
						getParam().addAll(CollectionUtils.arrayToList(whereCondition.getValue()));
					}
					else {
						getParam().add(whereCondition.getValue());
					}
				}
			}
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
		String sql = getSelectStmt() + super.getSql();
		logger.debug("构建sql完成 。。。");
		return sql;
	}
	
	private String getSelectStmt() {
		StringBuilder sql = new StringBuilder();
		if (columns.size() > 0) {
			sql.append("select ");
			for(String column : columns) {
				sql.append(column).append(',');
			}
			sql.deleteCharAt(sql.length() - 1).append(" from ").append(SqlBuilderUtils.getTableNameFromCache(beanClass));
		}
		else {
			sql.append("select * from ").append(SqlBuilderUtils.getTableNameFromCache(beanClass));
		}
		return sql.toString();
	}
	
}
