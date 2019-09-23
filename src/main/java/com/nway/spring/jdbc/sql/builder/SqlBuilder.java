package com.nway.spring.jdbc.sql.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SSupplier;

public class SqlBuilder {

	protected StringBuilder sql = new StringBuilder();
	protected List<Object> param = new ArrayList<>();
	protected Class beanClass;
	
	// 查询条件一定是以where()方法开始，where后的第一个条件不要and
	private boolean canAppendAnd = false;

	protected SqlBuilder(Class beanClass) {
		this.beanClass = beanClass;
	}

	public <T> Class<T> getBeanClass() {
		return this.beanClass;
	}

	public <T> SqlBuilder where() {
		sql.append(" where ");
		return this;
	}

	public <T> SqlBuilder eq(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" = ?");
		param.add(val.get());
		return this;
	}

	public <T> SqlBuilder eq(String column, Object val) {
		appendAnd();
		sql.append(column).append(" = ?");
		param.add(val);
		return this;
	}

	public <T> SqlBuilder ne(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" != ?");
		param.add(val.get());
		return this;
	}

	public SqlBuilder ne(String column, Object val) {
		appendAnd();
		sql.append(column).append(" != ?");
		param.add(val);
		return this;
	}

	public <T> SqlBuilder gt(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" > ?");
		param.add(val.get());
		return this;
	}

	public SqlBuilder gt(String column, Object val) {
		appendAnd();
		sql.append(column).append(" > ?");
		param.add(val);
		return this;
	}

	public <T> SqlBuilder ge(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" >= ?");
		param.add(val.get());
		return this;
	}

	public SqlBuilder ge(String column, Object val) {
		appendAnd();
		sql.append(column).append(" >= ?");
		param.add(val);
		return this;
	}

	public <T> SqlBuilder lt(SSupplier<T> val) {
		sql.append(" and ").append(SqlBuilderUtils.getColumn(beanClass, val)).append(" < ?");
		param.add(val.get());
		return this;
	}

	public SqlBuilder lt(String column, Object val) {
		appendAnd();
		sql.append(column).append(" < ?");
		param.add(val);
		return this;
	}

	public <T> SqlBuilder le(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" <= ?");
		param.add(val.get());
		return this;
	}

	public SqlBuilder le(String column, Object val) {
		appendAnd();
		sql.append(column).append(" <= ?");
		param.add(val);
		return this;
	}

	public SqlBuilder like(SSupplier<String> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" like ?");
		param.add("%" + val.get() + "%");
		return this;
	}

	public SqlBuilder like(String column, String val) {
		appendAnd();
		sql.append(column).append(" like ?");
		param.add("%" + val + "%");
		return this;
	}

	public SqlBuilder notLike(SSupplier<String> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" not like ?");
		param.add("%" + val.get() + "%");
		return this;
	}

	public SqlBuilder notLike(String column, Object val) {
		appendAnd();
		sql.append(column).append(" not like ?");
		param.add("%" + val + "%");
		return this;
	}

	public <T> SqlBuilder likeLeft(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" like ?");
		param.add("%" + val.get());
		return this;
	}

	public SqlBuilder likeLeft(String column, String val) {
		appendAnd();
		sql.append(column).append(" like ?");
		param.add("%" + val);
		return this;
	}

	public <T> SqlBuilder likeRight(SSupplier<T> val) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" like ?");
		param.add(val.get() + "%");
		return this;
	}

	public SqlBuilder likeRight(String column, String val) {
		appendAnd();
		sql.append(column).append(" like ?");
		param.add(val + "%");
		return this;
	}

	public <T> SqlBuilder between(SSupplier<T> column, Supplier<T> leftVal, Supplier<T> rightVal) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" between").append(" ? and ?");
		param.add(leftVal.get());
		param.add(rightVal.get());
		return this;
	}

	public SqlBuilder between(String column, Object leftVal, Object rightVal) {
		appendAnd();
		sql.append(column).append(" between").append(" ? and ?");
		param.add(leftVal);
		param.add(rightVal);
		return this;
	}

	public <T> SqlBuilder notBetween(SSupplier<T> column, Supplier<T> leftVal, Supplier<T> rightVal) {
		appendAnd();
		sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not between").append(" ? and ?");
		param.add(leftVal.get());
		param.add(rightVal.get());
		return this;
	}
	
	public <T> SqlBuilder notBetween(String column, Supplier<T> leftVal, Supplier<T> rightVal) {
		appendAnd();
		sql.append(column).append(" not between").append(" ? and ?");
		param.add(leftVal.get());
		param.add(rightVal.get());
		return this;
	}

	public SqlBuilder notBetween(String column, Object leftVal, Object rightVal) {
		appendAnd();
		sql.append(column).append(" not between").append(" ? and ?");
		param.add(leftVal);
		param.add(rightVal);
		return this;
	}

	public <T> SqlBuilder and(Consumer<SqlBuilder> lambdaWhereBuilder) {
		SqlBuilder lq = new SqlBuilder(beanClass);
		lambdaWhereBuilder.accept(lq);
		// 5代表" and "的长度
		sql.append(" and (").append(lq.getSql()).append(")");
		param.addAll(lq.getParam());
		return this;
	}
	
	public <T> SqlBuilder or(Consumer<SqlBuilder> lambdaWhereBuilder) {
		SqlBuilder lq = new SqlBuilder(beanClass);
		lambdaWhereBuilder.accept(lq);
		// 5代表" and "的长度
		sql.append(" or (").append(lq.getSql()).append(")");
		param.addAll(lq.getParam());
		return this;
	}
	
	public <T> SqlBuilder or() {
		sql.append(" or ");
		canAppendAnd = false;
		return this;
	}

	public <T> SqlBuilder in(SSupplier<T> val) {
		T cls = val.get();
		if (cls instanceof Collection<?>) {
			appendAnd();
			Collection values = (Collection) cls;
			sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" in (");
			values.stream().forEach(e -> {
				if (e instanceof Number) {
					param.add(e);
				}
			});
			for (int i = 0; i < values.size(); i++) {
				sql.append("?").append(",");
			}
			if (!values.isEmpty()) {
				sql.setCharAt(sql.length() - 1, ')');
			}
		}

		return this;
	}

	public <T> SqlBuilder in(String column, Collection<T> val) {
		appendAnd();
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

	public <T> SqlBuilder notIn(SSupplier<T> val) {
		T cls = val.get();
		if (cls instanceof Collection<?>) {
			appendAnd();
			Collection values = (Collection) cls;
			sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(" not in (");
			values.stream().forEach(e -> {
				if (e instanceof Number) {
					param.add(e);
				}
			});
			for (int i = 0; i < values.size(); i++) {
				sql.append("?").append(",");
			}
			if (!values.isEmpty()) {
				sql.setCharAt(sql.length() - 1, ')');
			}
		}
		return this;
	}

	public <T> SqlBuilder notIn(String column, Collection<T> val) {
		appendAnd();
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

	public <T> SqlBuilder groupBy(SSupplier<T> column) {
		sql.append(" group by ").append(SqlBuilderUtils.getColumn(beanClass, column));
		return this;
	}

	public <T> SqlBuilder orderBy(SSupplier<T> column) {
		sql.append(" order by ").append(SqlBuilderUtils.getColumn(beanClass, column));
		return this;
	}

	public <T> SqlBuilder orderByDesc(SSupplier<T> column) {
		sql.append(" order by ").append(SqlBuilderUtils.getColumn(beanClass, column)).append(" desc");
		return this;
	}

	public SqlBuilder having(Supplier<String>... columnAndValues) {
		sql.append(" having ");
		for (Supplier<String> supplier : columnAndValues) {
			sql.append(supplier.get());
		}
		return this;
	}

	public <T> SqlBuilder set(SSupplier<T> val) {
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
