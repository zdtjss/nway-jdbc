package com.nway.spring.jdbc.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SqlBuilder {

	protected StringBuilder sql = new StringBuilder();
	protected List<Object> param = new ArrayList<>();
	protected Class beanClass;
	
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
		sql.append(ReflectUtils.getColumn(beanClass, val)).append(" = ?");
		param.add(val.get());
		return this;
	}

	public <T> SqlBuilder eq(String column, Object val) {
		sql.append(column).append(" = ?");
		param.add(val);
		return this;
	}

	public <T> SqlBuilder ne(SSupplier<T> val) {
		sql.append(ReflectUtils.getColumn(beanClass, val)).append(" != ?");
		param.add(val.get());
		return this;
	}

	public SqlBuilder ne(String column, Object val) {
		sql.append(column).append(" != ?");
		param.add(val);
		return this;
	}

	public <T> SqlBuilder gt(SSupplier<T> val) {
		sql.append(ReflectUtils.getColumn(beanClass, val)).append(" > ?");
		param.add(val.get());
		return this;
	}

	public SqlBuilder gt(String column, Object val) {
		sql.append(column).append(" > ?");
		param.add(val);
		return this;
	}

	public <T> SqlBuilder ge(SSupplier<T> val) {
		sql.append(ReflectUtils.getColumn(beanClass, val)).append(" >= ?");
		param.add(val.get());
		return this;
	}

	public SqlBuilder ge(String column, Object val) {
		sql.append(column).append(" >= ?");
		param.add(val);
		return this;
	}

	public <T> SqlBuilder lt(SSupplier<T> val) {
		sql.append(ReflectUtils.getColumn(beanClass, val)).append(" < ?");
		param.add(val.get());
		return this;
	}

	public SqlBuilder lt(String column, Object val) {
		sql.append(column).append(" < ?");
		param.add(val);
		return this;
	}

	public <T> SqlBuilder le(SSupplier<T> val) {
		sql.append(ReflectUtils.getColumn(beanClass, val)).append(" <= ?");
		param.add(val.get());
		return this;
	}

	public SqlBuilder le(String column, Object val) {
		sql.append(column).append(" <= ?");
		param.add(val);
		return this;
	}

	public SqlBuilder like(SSupplier<String> val) {
		sql.append(ReflectUtils.getColumn(beanClass, val)).append(" like ?");
		param.add("%" + val.get() + "%");
		return this;
	}

	public SqlBuilder like(String column, String val) {
		sql.append(column).append(" like ?");
		param.add("%" + val + "%");
		return this;
	}

	public SqlBuilder notLike(SSupplier<String> val) {
		sql.append(ReflectUtils.getColumn(beanClass, val)).append(" not like ?");
		param.add("%" + val.get() + "%");
		return this;
	}

	public SqlBuilder notLike(String column, Object val) {
		sql.append(column).append(" not like ?");
		param.add("%" + val + "%");
		return this;
	}

	public <T> SqlBuilder likeLeft(SSupplier<T> val) {
		sql.append(ReflectUtils.getColumn(beanClass, val)).append(" like ?");
		param.add("%" + val.get());
		return this;
	}

	public SqlBuilder likeLeft(String column, String val) {
		sql.append(column).append(" like ?");
		param.add("%" + val);
		return this;
	}

	public <T> SqlBuilder likeRight(SSupplier<T> val) {
		sql.append(ReflectUtils.getColumn(beanClass, val)).append(" like ?");
		param.add(val.get() + "%");
		return this;
	}

	public SqlBuilder likeRight(String column, String val) {
		sql.append(column).append(" like ?");
		param.add(val + "%");
		return this;
	}

	public <T> SqlBuilder between(String column, Supplier<T> leftVal, Supplier<T> rightVal) {
		sql.append(column).append(" between").append(" ? and ?");
		param.add(leftVal.get());
		param.add(rightVal.get());
		return this;
	}

	public SqlBuilder between(String column, Object leftVal, Object rightVal) {
		sql.append(column).append(" between").append(" ? and ?");
		param.add(leftVal);
		param.add(rightVal);
		return this;
	}

	public <T> SqlBuilder notBetween(String column, Supplier<T> leftVal, Supplier<T> rightVal) {
		sql.append(column).append(" not between").append(" ? and ?");
		param.add(leftVal.get());
		param.add(rightVal.get());
		return this;
	}

	public SqlBuilder notBetween(String column, Object leftVal, Object rightVal) {
		sql.append(column).append(" not between").append(" ? and ?");
		param.add(leftVal);
		param.add(rightVal);
		return this;
	}

	public <T> SqlBuilder and(Consumer<SqlBuilder> lambdaWhereBuilder) {
		SqlBuilder lq = new SqlBuilder(beanClass);
		lambdaWhereBuilder.accept(lq);
		sql.append(" and (").append(lq.getSql()).append(")");
		param.addAll(lq.getParam());
		return this;
	}

	public <T> SqlBuilder or(Consumer<SqlBuilder> lambdaWhereBuilder) {
		SqlBuilder lq = new SqlBuilder(beanClass);
		lambdaWhereBuilder.accept(lq);
		sql.append(" or (").append(lq.getSql()).append(")");
		param.addAll(lq.getParam());
		return this;
	}

	public <T> SqlBuilder in(SSupplier<T> val) {
		T cls = val.get();
		if (cls instanceof Collection<?>) {
			Collection values = (Collection) cls;
			sql.append(ReflectUtils.getColumn(beanClass, val)).append(" in (");
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
			Collection values = (Collection) cls;
			sql.append(ReflectUtils.getColumn(beanClass, val)).append(" not in (");
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
		sql.append(" group by ").append(ReflectUtils.getColumn(beanClass, column));
		return this;
	}

	public <T> SqlBuilder orderBy(SSupplier<T> column) {
		sql.append(" order by ").append(ReflectUtils.getColumn(beanClass, column));
		return this;
	}

	public <T> SqlBuilder orderByDesc(SSupplier<T> column) {
		sql.append(" order by ").append(ReflectUtils.getColumn(beanClass, column)).append(" desc");
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

	public String getSql() {
		return sql.toString();
	}

	public List<Object> getParam() {
		return param;
	}
}
