package com.nway.spring.jdbc.sql.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.function.SSupplier;

public class DefaultSqlBuilder implements SqlBuilder {

	protected StringBuilder sql = new StringBuilder();
	protected List<Object> param = new ArrayList<>();
	protected Class beanClass;
	
	// 查询条件一定是以where()方法开始，where后的第一个条件不要and
	private boolean canAppendAnd = false;

	protected DefaultSqlBuilder(Class beanClass) {
		this.beanClass = beanClass;
	}

	public <T> Class<T> getBeanClass() {
		return this.beanClass;
	}

	public <T> DefaultSqlBuilder where() {
		sql.append(" where ");
		return this;
	}

	public <T> DefaultSqlBuilder eq(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" = ?");
		param.add(val.get());
		return this;
	}
	
	public <T, R> DefaultSqlBuilder eq(SFunction<T, R> column, Object val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" = ?");
		param.add(val);
		return this;
	}

	public <T> DefaultSqlBuilder eq(String column, Object val) {
		appendAnd();
		sql.append(column).append(" = ?");
		param.add(val);
		return this;
	}

	public <T> DefaultSqlBuilder ne(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" != ?");
		param.add(val.get());
		return this;
	}

	public DefaultSqlBuilder ne(String column, Object val) {
		appendAnd();
		sql.append(column).append(" != ?");
		param.add(val);
		return this;
	}

	public <T> DefaultSqlBuilder gt(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" > ?");
		param.add(val.get());
		return this;
	}

	public DefaultSqlBuilder gt(String column, Object val) {
		appendAnd();
		sql.append(column).append(" > ?");
		param.add(val);
		return this;
	}

	public <T> DefaultSqlBuilder ge(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" >= ?");
		param.add(val.get());
		return this;
	}

	public DefaultSqlBuilder ge(String column, Object val) {
		appendAnd();
		sql.append(column).append(" >= ?");
		param.add(val);
		return this;
	}

	public <T> DefaultSqlBuilder lt(SSupplier<T> val) {
		sql.append(" and ").append(SqlBuilderUtils.getColumn(beanClass, val)).append(" < ?");
		param.add(val.get());
		return this;
	}

	public DefaultSqlBuilder lt(String column, Object val) {
		appendAnd();
		sql.append(column).append(" < ?");
		param.add(val);
		return this;
	}

	public <T> DefaultSqlBuilder le(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" <= ?");
		param.add(val.get());
		return this;
	}

	public DefaultSqlBuilder le(String column, Object val) {
		appendAnd();
		sql.append(column).append(" <= ?");
		param.add(val);
		return this;
	}

	public DefaultSqlBuilder like(SSupplier<String> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" like ?");
		param.add("%" + val.get() + "%");
		return this;
	}

	public DefaultSqlBuilder like(String column, String val) {
		appendAnd();
		sql.append(column).append(" like ?");
		param.add("%" + val + "%");
		return this;
	}

	public DefaultSqlBuilder notLike(SSupplier<String> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" not like ?");
		param.add("%" + val.get() + "%");
		return this;
	}

	public DefaultSqlBuilder notLike(String column, Object val) {
		appendAnd();
		sql.append(column).append(" not like ?");
		param.add("%" + val + "%");
		return this;
	}

	public <T> DefaultSqlBuilder likeLeft(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" like ?");
		param.add("%" + val.get());
		return this;
	}

	public DefaultSqlBuilder likeLeft(String column, String val) {
		appendAnd();
		sql.append(column).append(" like ?");
		param.add("%" + val);
		return this;
	}

	public <T> DefaultSqlBuilder likeRight(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" like ?");
		param.add(val.get() + "%");
		return this;
	}

	public DefaultSqlBuilder likeRight(String column, String val) {
		appendAnd();
		sql.append(column).append(" like ?");
		param.add(val + "%");
		return this;
	}

	public <T> DefaultSqlBuilder between(SSupplier<T> column, Supplier<T> leftVal, Supplier<T> rightVal) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" between").append(" ? and ?");
		param.add(leftVal.get());
		param.add(rightVal.get());
		return this;
	}

	public DefaultSqlBuilder between(String column, Object leftVal, Object rightVal) {
		appendAnd();
		sql.append(column).append(" between").append(" ? and ?");
		param.add(leftVal);
		param.add(rightVal);
		return this;
	}

	public <T> DefaultSqlBuilder notBetween(SSupplier<T> column, Supplier<T> leftVal, Supplier<T> rightVal) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not between").append(" ? and ?");
		param.add(leftVal.get());
		param.add(rightVal.get());
		return this;
	}
	
	public <T> DefaultSqlBuilder notBetween(String column, Supplier<T> leftVal, Supplier<T> rightVal) {
		appendAnd();
		sql.append(column).append(" not between").append(" ? and ?");
		param.add(leftVal.get());
		param.add(rightVal.get());
		return this;
	}

	public DefaultSqlBuilder notBetween(String column, Object leftVal, Object rightVal) {
		appendAnd();
		sql.append(column).append(" not between").append(" ? and ?");
		param.add(leftVal);
		param.add(rightVal);
		return this;
	}

	public <T> DefaultSqlBuilder and(Consumer<DefaultSqlBuilder> lambdaWhereBuilder) {
		DefaultSqlBuilder lq = new DefaultSqlBuilder(beanClass);
		lambdaWhereBuilder.accept(lq);
		// 5代表" and "的长度
		sql.append(" and (").append(lq.getSql()).append(")");
		param.addAll(lq.getParam());
		return this;
	}
	
	public <T> DefaultSqlBuilder or(Consumer<DefaultSqlBuilder> lambdaWhereBuilder) {
		DefaultSqlBuilder lq = new DefaultSqlBuilder(beanClass);
		lambdaWhereBuilder.accept(lq);
		// 5代表" and "的长度
		sql.append(" or (").append(lq.getSql()).append(")");
		param.addAll(lq.getParam());
		return this;
	}
	
	public <T> DefaultSqlBuilder or() {
		sql.append(" or ");
		canAppendAnd = false;
		return this;
	}

	public <T> DefaultSqlBuilder in(SSupplier<T> val) {
		T cls = val.get();
		if (cls instanceof Collection<?>) {
			appendAnd();
			Collection values = (Collection) cls;
			sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" in (");
			values.stream().forEach(e -> param.add(e));
			for (int i = 0; i < values.size(); i++) {
				sql.append("?").append(",");
			}
			if (!values.isEmpty()) {
				sql.setCharAt(sql.length() - 1, ')');
			}
		}
		return this;
	}

	public DefaultSqlBuilder in(String column, Collection<?> val) {
		appendAnd();
		sql.append(column).append(" in (");
		val.stream().forEach(e -> param.add(e));
		for (int i = 0; i < val.size(); i++) {
			sql.append("?").append(",");
		}
		if (!val.isEmpty()) {
			sql.setCharAt(sql.length() - 1, ')');
		}
		return this;
	}
	
	public <T, R> DefaultSqlBuilder in(SFunction<T, R> column, Collection<?> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" in (");
		val.stream().forEach(e -> param.add(e));
		for (int i = 0; i < val.size(); i++) {
			sql.append("?").append(",");
		}
		if (!val.isEmpty()) {
			sql.setCharAt(sql.length() - 1, ')');
		}
		return this;
	}

	public DefaultSqlBuilder notIn(SSupplier<?> val) {
		Object cls = val.get();
		if (cls instanceof Collection<?>) {
			appendAnd();
			Collection values = (Collection) cls;
			sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" not in (");
			values.stream().forEach(e -> param.add(e));
			for (int i = 0; i < values.size(); i++) {
				sql.append("?").append(",");
			}
			if (!values.isEmpty()) {
				sql.setCharAt(sql.length() - 1, ')');
			}
		}
		return this;
	}

	public DefaultSqlBuilder notIn(String column, Collection<?> val) {
		appendAnd();
		sql.append(column).append(" not in (");
		val.stream().forEach(e -> param.add(e));
		for (int i = 0; i < val.size(); i++) {
			sql.append("?").append(",");
		}
		if (!val.isEmpty()) {
			sql.setCharAt(sql.length() - 1, ')');
		}
		return this;
	}

	public <T> DefaultSqlBuilder groupBy(SSupplier<T> column) {
		sql.append(" group by ").append(SqlBuilderUtils.getColumn(beanClass, column));
		return this;
	}

	public <T> DefaultSqlBuilder orderBy(SSupplier<T> column) {
		sql.append(" order by ").append(SqlBuilderUtils.getColumn(beanClass, column));
		return this;
	}

	public <T> DefaultSqlBuilder orderByDesc(SSupplier<T> column) {
		sql.append(" order by ").append(SqlBuilderUtils.getColumn(beanClass, column)).append(" desc");
		return this;
	}

	public DefaultSqlBuilder having(Supplier<String>... columnAndValues) {
		sql.append(" having ");
		for (Supplier<String> supplier : columnAndValues) {
			sql.append(supplier.get());
		}
		return this;
	}

	public <T> DefaultSqlBuilder set(SSupplier<T> val) {
		throw new UnsupportedOperationException("此方法只有update时使用");
	}
	
	private void appendAnd() {
		if (canAppendAnd) {
			sql.append(" and ");
		}
		// canAppendAnd 只失效一次，只有在or()方法后第一次补充条件时，新补充的条件不需要 and
		canAppendAnd = true;
	}

	public String getSql() {
		return sql.toString();
	}

	public List<Object> getParam() {
		return param;
	}
}
