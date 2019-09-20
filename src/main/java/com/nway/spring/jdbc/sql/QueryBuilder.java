package com.nway.spring.jdbc.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.annotation.Table;

public class QueryBuilder {

	private Class beanClass;
	private String[] columns;
	private StringBuilder sql = new StringBuilder("select ");
	private List<Object> param = new ArrayList<>();

	public QueryBuilder(Class<?> beanClass, String ... columns) {
		this.beanClass = beanClass;
		this.columns = columns;
		init();
	}
	
	public <T> Class<T> getBeanClass() {
		return beanClass;
	}
	
	private void init() {
		if (columns.length > 0) {
			initColumns(columns);
		}
		else {
			initColumns();
		}
	}

	private void initColumns() {
		Table table = (Table) beanClass.getAnnotation(Table.class);
		for(Field field : beanClass.getDeclaredFields()) {
			Column column = field.getAnnotation(Column.class);
			if(column != null) {
				sql.append(column.name()).append(',');
			}
			else {
				sql.append(fieldToColumn(field.getName())).append(',');
			}
		}
		sql.deleteCharAt(sql.length() - 1).append(" from ").append(table.value()).append(" where ");
	}
	
	private void initColumns(String ... columns) {
		Table table = (Table) beanClass.getAnnotation(Table.class);
		for(String column : columns) {
			sql.append(column).append(',');
		}
		sql.deleteCharAt(sql.length() - 1).append(" from ").append(table.value()).append(" where ");
	}

	public <T> QueryBuilder eq(String column, Supplier<T> val) {
		sql.append(column).append(" = ?");
		param.add(val.get());
		return this;
	}
	
	public <T> QueryBuilder eq(String column, Object val) {
		sql.append(column).append(" = ?");
		param.add(val);
		return this;
	}

	public <T> QueryBuilder ne(String column, Supplier<T> val) {
		sql.append(column).append(" != ?");
		param.add(val.get());
		return this;
	}
	
	public QueryBuilder ne(String column, Object val) {
		sql.append(column).append(" != ?");
		param.add(val);
		return this;
	}

	public <T> QueryBuilder gt(String column, Supplier<T> val) {
		sql.append(column).append(" > ?");
		param.add(val.get());
		return this;
	}
	
	public QueryBuilder gt(String column, Object val) {
		sql.append(column).append(" > ?");
		param.add(val);
		return this;
	}

	public <T> QueryBuilder ge(String column, Supplier<T> val) {
		sql.append(column).append(" >= ?");
		param.add(val.get());
		return this;
	}
	
	public QueryBuilder ge(String column, Object val) {
		sql.append(column).append(" >= ?");
		param.add(val);
		return this;
	}

	public <T> QueryBuilder lt(String column, Supplier<T> val) {
		sql.append(column).append(" < ?");
		param.add(val.get());
		return this;
	}
	
	public QueryBuilder lt(String column, Object val) {
		sql.append(column).append(" < ?");
		param.add(val);
		return this;
	}

	public <T> QueryBuilder le(String column, Supplier<T> val) {
		sql.append(column).append(" <= ?");
		param.add(val.get());
		return this;
	}
	
	public QueryBuilder le(String column, Object val) {
		sql.append(column).append(" <= ?");
		param.add(val);
		return this;
	}

	public QueryBuilder like(String column, Supplier<String> val) {
		sql.append(column).append(" like ?");
		param.add("%" + val.get() + "%");
		return this;
	}
	
	public QueryBuilder like(String column, String val) {
		sql.append(column).append(" like ?");
		param.add("%" + val + "%");
		return this;
	}

	public QueryBuilder notLike(String column, Supplier<String> val) {
		sql.append(column).append(" not like ?");
		param.add("%" + val.get() + "%");
		return this;
	}
	
	public QueryBuilder notLike(String column, Object val) {
		sql.append(column).append(" not like ?");
		param.add("%" + val + "%");
		return this;
	}

	public <T> QueryBuilder likeLeft(String column, Supplier<T> val) {
		sql.append(column).append(" like ?");
		param.add("%" + val.get());
		return this;
	}
	
	public QueryBuilder likeLeft(String column, String val) {
		sql.append(column).append(" like ?");
		param.add("%" + val);
		return this;
	}

	public <T> QueryBuilder likeRight(String column, Supplier<T> val) {
		sql.append(column).append(" like ?");
		param.add(val.get() + "%");
		return this;
	}
	
	public QueryBuilder likeRight(String column, String val) {
		sql.append(column).append(" like ?");
		param.add(val + "%");
		return this;
	}

	public <T> QueryBuilder between(String column, Supplier<T> leftVal, Supplier<T> rightVal) {
		sql.append(column).append(" between").append(" ? and ?");
		param.add(leftVal.get());
		param.add(rightVal.get());
		return this;
	}
	
	public QueryBuilder between(String column, Object leftVal, Object rightVal) {
		sql.append(column).append(" between").append(" ? and ?");
		param.add(leftVal);
		param.add(rightVal);
		return this;
	}

	public <T> QueryBuilder notBetween(String column, Supplier<T> leftVal, Supplier<T> rightVal) {
		sql.append(column).append(" not between").append(" ? and ?");
		param.add(leftVal.get());
		param.add(rightVal.get());
		return this;
	}
	
	public QueryBuilder notBetween(String column, Object leftVal, Object rightVal) {
		sql.append(column).append(" not between").append(" ? and ?");
		param.add(leftVal);
		param.add(rightVal);
		return this;
	}

	public <T> QueryBuilder and(Consumer<QueryBuilder> lambdaQueryBuilder) {
		QueryBuilder lq = new QueryBuilder(beanClass);
		lambdaQueryBuilder.accept(lq);
		sql.append(" and (").append(lq.getSql()).append(")");
		param.addAll(lq.getParam());
		return this;
	}

	public <T> QueryBuilder or(Consumer<QueryBuilder> lambdaQueryBuilder) {
		QueryBuilder lq = new QueryBuilder(beanClass);
		lambdaQueryBuilder.accept(lq);
		sql.append(" or (").append(lq.getSql()).append(")");
		param.addAll(lq.getParam());
		return this;
	}

	public <T> QueryBuilder in(String column, Supplier<T> val) {
		T cls = val.get();
		if(cls instanceof Collection<?>) {
			Collection values = (Collection) cls;
			sql.append(column).append(" in (");
			values.stream().forEach(e -> {
				if(e instanceof Number) {
					param.add(e);
				}
			});
			for (int i = 0; i < values.size(); i++) {
				sql.append("?").append(",");
			}
			if(!values.isEmpty()) {
				sql.setCharAt(sql.length() - 1, ')');
			}
		}
		
		return this;
	}
	
	public <T> QueryBuilder in(String column, Collection<T> val) {
		sql.append(column).append(" in (");
		val.stream().forEach(e -> {
			if (e instanceof Number) {
				param.add(e);
			}
		});
		for (int i = 0; i < val.size(); i++) {
			sql.append("?").append(",");
		}
		if (!val.isEmpty()) {
			sql.setCharAt(sql.length() - 1, ')');
		}

		return this;
	}

	public <T> QueryBuilder notIn(String column, Supplier<T> val) {
		T cls = val.get();
		if(cls instanceof Collection<?>) {
			Collection values = (Collection) cls;
			sql.append(column).append(" not in (");
			values.stream().forEach(e -> {
				if(e instanceof Number) {
					param.add(e);
				}
			});
			for (int i = 0; i < values.size(); i++) {
				sql.append("?").append(",");
			}
			if(!values.isEmpty()) {
				sql.setCharAt(sql.length() - 1, ')');
			}
		}
		return this;
	}
	
	public <T> QueryBuilder notIn(String column, Collection<T> val) {
		sql.append(column).append(" not in (");
		val.stream().forEach(e -> {
			if (e instanceof Number) {
				param.add(e);
			}
		});
		for (int i = 0; i < val.size(); i++) {
			sql.append("?").append(",");
		}
		if (!val.isEmpty()) {
			sql.setCharAt(sql.length() - 1, ')');
		}
		return this;
	}

	public QueryBuilder groupBy(String column) {
		sql.append(" group by ").append(column);
		return this;
	}

	public QueryBuilder orderBy(String column) {
		sql.append(" order by ").append(column);
		return this;
	}
	
	public QueryBuilder orderByDesc(String column) {
		sql.append(" order by ").append(column).append(" desc");
		return this;
	}

	public QueryBuilder having(Supplier<String> ... columnAndValues) {
		sql.append(" having ");
		for(Supplier<String> supplier : columnAndValues) {
			sql.append(supplier.get());
		}
		return this;
	}

	public String getSql() {
		return sql.toString();
	}

	public List<Object> getParam() {
		return param;
	}
	
	private String fieldToColumn(String fieldName) {
		StringBuilder column = new StringBuilder();
		for(char c :fieldName.toCharArray()) {
			if(Character.isUpperCase(c)) {
				column.append('_').append(Character.toLowerCase(c));
			}
			else {
				column.append(c);
			}
		}
		return column.toString();
	}
}
