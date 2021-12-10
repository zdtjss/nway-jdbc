package com.nway.spring.jdbc.sql.builder;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.function.SSupplier;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;
import com.nway.spring.jdbc.sql.permission.NonePermissionStrategy;
import com.nway.spring.jdbc.sql.permission.WhereCondition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class SqlBuilder implements ISqlBuilder {

    protected StringBuilder sql = new StringBuilder();
    protected List<Object> param = new ArrayList<>();
    protected Class beanClass;

    // 查询条件一定是以where()方法开始，where后的第一个条件不要and
    private boolean canAppendAnd = false;
    private boolean canAppendWhere = true;
    private boolean ignoreInvalid = true;

    protected SqlBuilder(Class beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public <T> Class<T> getBeanClass() {
        return this.beanClass;
    }

    public SqlBuilder distinct() {
        sql.append(" distinct ");
        return this;
    }

    public SqlBuilder where() {
        if (canAppendWhere) {
            sql.append(" where ");
        }
        canAppendWhere = false;
        return this;
    }

    /**
     * 设置为true后，需要自己判断参数是否为空
     */
    public SqlBuilder setIgnoreInvalid(boolean ignoreInvalid) {
        this.ignoreInvalid = ignoreInvalid;
        return this;
    }

    public <T> SqlBuilder eq(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(val, " = ?");
        return this;
    }

    public <T, R> SqlBuilder eq(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " = ?");
        return this;
    }

    public <T> SqlBuilder eq(String column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " = ?");
        return this;
    }

    public <T> SqlBuilder ne(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(val, " != ?");
        return this;
    }

    public SqlBuilder ne(String column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " != ?");
        return this;
    }

    public <T, R> SqlBuilder ne(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " != ?");
        return this;
    }

    public <T> SqlBuilder gt(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(val, " > ?");
        return this;
    }

    public SqlBuilder gt(String column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " > ?");
        return this;
    }

    public <T, R> SqlBuilder gt(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " > ?");
        return this;
    }

    public <T> SqlBuilder ge(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(val, " >= ?");
        return this;
    }

    public SqlBuilder ge(String column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " >= ?");
        return this;
    }

    public <T, R> SqlBuilder ge(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " >= ?");
        return this;
    }

    public <T> SqlBuilder lt(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendCondition( val, " < ?");
        return this;
    }

    public SqlBuilder lt(String column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " < ?");
        return this;
    }

    public <T, R> SqlBuilder lt(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " < ?");
        return this;
    }

    public <T> SqlBuilder le(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(val, " <= ?");
        return this;
    }

    public SqlBuilder le(String column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " <= ?");
        return this;
    }

    public <T, R> SqlBuilder le(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " <= ?");
        return this;
    }

    public SqlBuilder like(SSupplier<String> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, val), "%" + val.get() + "%", " like ?");
        return this;
    }

    public SqlBuilder like(String column, String val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, "%" + val + "%", " like ?");
        return this;
    }

    public <T, R> SqlBuilder like(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val + "%", " like ?");
        return this;
    }

    public <T, R> SqlBuilder like(SFunction<T, R> column, Supplier<String> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val.get() + "%", " like ?");
        return this;
    }

    public SqlBuilder notLike(SSupplier<String> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, val), "%" + val.get() + "%", " not like ?");
        return this;
    }

    public SqlBuilder notLike(String column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, "%" + val + "%", " not like ?");
        return this;
    }

    public <T, R> SqlBuilder notLike(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val + "%", " not like ?");
        return this;
    }

    public <T, R> SqlBuilder notLike(SFunction<T, R> column, Supplier<String> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val.get() + "%", " not like ?");
        return this;
    }

    public <T> SqlBuilder likeLeft(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, val), "%" + val.get(), " like ?");
        return this;
    }

    public SqlBuilder likeLeft(String column, String val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, "%" + val, " like ?");
        return this;
    }

    public <T, R> SqlBuilder likeLeft(SFunction<T, R> column, String val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val, " like ?");
        return this;
    }

    public <T, R> SqlBuilder likeLeft(SFunction<T, R> column, SSupplier<String> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val, " like ?");
        return this;
    }

    public SqlBuilder likeRight(SSupplier<String> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, val), val + "%", " like ?");
        return this;
    }

    public SqlBuilder likeRight(String column, String val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val + "%", " like ?");
        return this;
    }

    public <T, R> SqlBuilder likeRight(SFunction<T, R> column, String val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), val + "%", " like ?");
        return this;
    }

    public <T, R> SqlBuilder likeRight(SFunction<T, R> column, SSupplier<String> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), val + "%", " like ?");
        return this;
    }

    public <T, X> SqlBuilder between(SSupplier<T> column, Supplier<X> leftVal, Supplier<X> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return this;
        }
        appendAnd();
        sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return this;
    }

    public <T, R> SqlBuilder between(SFunction<T, R> column, Object leftVal, Object rightVal) {
        if(isInvalid(leftVal) && isInvalid(rightVal)) {
            return this;
        }
        appendAnd();
        sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" between").append(" ? and ?");
        param.add(leftVal);
        param.add(rightVal);
        return this;
    }

    public <T, R, X> SqlBuilder between(SFunction<T, R> column, Supplier<X> leftVal, Supplier<X> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return this;
        }
        appendAnd();
        sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return this;
    }

    public SqlBuilder between(String column, Object leftVal, Object rightVal) {
        if(isInvalid(leftVal) && isInvalid(rightVal)) {
            return this;
        }
        appendAnd();
        sql.append(column).append(" between").append(" ? and ?");
        param.add(leftVal);
        param.add(rightVal);
        return this;
    }

    public <X> SqlBuilder between(String column, Supplier<X> leftVal, Supplier<X> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return this;
        }
        appendAnd();
        sql.append(column).append(" between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return this;
    }

    public <T, X> SqlBuilder notBetween(SSupplier<T> column, Supplier<X> leftVal, Supplier<X> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return this;
        }
        appendAnd();
        sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return this;
    }

    public <X> SqlBuilder notBetween(String column, Supplier<X> leftVal, Supplier<X> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return this;
        }
        appendAnd();
        sql.append(column).append(" not between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return this;
    }

    public SqlBuilder notBetween(String column, Object leftVal, Object rightVal) {
        if(isInvalid(leftVal) && isInvalid(rightVal)) {
            return this;
        }
        appendAnd();
        sql.append(column).append(" not between").append(" ? and ?");
        param.add(leftVal);
        param.add(rightVal);
        return this;
    }

    public <T, R, X> SqlBuilder notBetween(SFunction<T, R> column, Supplier<X> leftVal, Supplier<X> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return this;
        }
        appendAnd();
        sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return this;
    }

    public <T, R> SqlBuilder notBetween(SFunction<T, R> column, Object leftVal, Object rightVal) {
        if(isInvalid(leftVal) && isInvalid(rightVal)) {
            return this;
        }
        appendAnd();
        sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not between").append(" ? and ?");
        param.add(leftVal);
        param.add(rightVal);
        return this;
    }

    public SqlBuilder and(Consumer<SqlBuilder> lambdaWhereBuilder) {
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

    public SqlBuilder in(String column, Collection<?> val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        sql.append(column).append(" in (");
        param.addAll(val);
        for (int i = 0; i < val.size(); i++) {
            sql.append("?,");
        }
        if (!val.isEmpty()) {
            sql.setCharAt(sql.length() - 1, ')');
        }
        return this;
    }

    public <T, R> SqlBuilder in(SFunction<T, R> column, Collection<?> val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" in (");
        param.addAll(val);
        for (int i = 0; i < val.size(); i++) {
            sql.append("?,");
        }
        if (!val.isEmpty()) {
            sql.setCharAt(sql.length() - 1, ')');
        }
        return this;
    }

    public SqlBuilder notIn(String column, Collection<?> val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        sql.append(column).append(" not in (");
        param.addAll(val);
        for (int i = 0; i < val.size(); i++) {
            sql.append("?,");
        }
        if (!val.isEmpty()) {
            sql.setCharAt(sql.length() - 1, ')');
        }
        return this;
    }

    public <T, R> SqlBuilder notIn(SFunction<T, R> column, Collection<?> val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not in (");
        param.addAll(val);
        for (int i = 0; i < val.size(); i++) {
            sql.append("?,");
        }
        if (!val.isEmpty()) {
            sql.setCharAt(sql.length() - 1, ')');
        }
        return this;
    }

    public <T> SqlBuilder groupBy(String... column) {
        sql.append(" group by ").append(Arrays.stream(column).collect(Collectors.joining(",")));
        return this;
    }

    @SafeVarargs
    public final <T, R> SqlBuilder groupBy(SFunction<T, R>... columns) {
        sql.append(" group by ");
        for (SFunction<T, R> column : columns) {
            sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        return this;
    }

    @SafeVarargs
    public final <T, R> SqlBuilder orderBy(SFunction<T, R>... columns) {
        sql.append(" order by ");
        for (SFunction<T, R> column : columns) {
            sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        return this;
    }

    public SqlBuilder orderBy(String... columns) {
        sql.append(" order by ");
        for (String column : columns) {
            sql.append(column).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        return this;
    }

    public <T, R> SqlBuilder andOrderByAsc(SFunction<T, R> column) {
        sql.append(",").append(SqlBuilderUtils.getColumn(beanClass, column)).append(" asc");
        return this;
    }

    public SqlBuilder andOrderByAsc(String... columns) {
        for (String column : columns) {
            sql.append(",").append(column).append(" asc");
        }
        return this;
    }

    @SafeVarargs
    public final <T, R> SqlBuilder orderByDesc(SFunction<T, R>... columns) {
        sql.append(" order by ");
        for (SFunction<T, R> column : columns) {
            sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" desc,");
        }
        sql.deleteCharAt(sql.length() - 1);
        return this;
    }

    public SqlBuilder orderByDesc(String... columns) {
        sql.append(" order by ");
        for (String column : columns) {
            sql.append(column).append(" desc,");
        }
        sql.deleteCharAt(sql.length() - 1);
        return this;
    }

    public <T, R> SqlBuilder andOrderByDesc(SFunction<T, R> column) {
        sql.append(",").append(SqlBuilderUtils.getColumn(beanClass, column)).append(" desc");
        return this;
    }

    public SqlBuilder andOrderByDesc(String... columns) {
        for (String column : columns) {
            sql.append(",").append(column).append(" desc");
        }
        return this;
    }

    public <T> SqlBuilder having(Consumer<SqlBuilder> lambdaWhereBuilder) {
        SqlBuilder lq = new SqlBuilder(beanClass);
        lambdaWhereBuilder.accept(lq);
        sql.append(" having ").append(lq.getSql());
        param.addAll(lq.getParam());
        return this;
    }

    public <T> SqlBuilder set(SSupplier<T> val) {
        throw new UnsupportedOperationException("此方法只有update时使用");
    }

    public <T, R> SqlBuilder set(SFunction<T, R> val) {
        throw new UnsupportedOperationException("此方法只有update时使用");
    }

    private void appendAnd() {
        if (canAppendAnd) {
            sql.append(" and ");
        }
        // canAppendAnd 只失效一次，只有在or()方法后第一次补充条件时，新补充的条件不需要 and
        canAppendAnd = true;
    }

    public void appendWhereCondition(String whereCondition) {
        appendAnd();
        sql.append(whereCondition);
    }

    private <T> void appendCondition(SSupplier<T> val, String op) {
        sql.append(SqlBuilderUtils.getColumn(beanClass, val)).append(op);
        param.add(val.get());
    }

    private <T, R> void appendCondition(SFunction<T, R> column, Object val,String op) {
        sql.append(SqlBuilderUtils.getColumn(beanClass, column)).append(op);
        param.add(val);
    }

    private void appendCondition(String column, Object val, String op) {
        sql.append(column).append(op);
        param.add(val);
    }

    private boolean isInvalid(Object val) {
        if (!ignoreInvalid) {
            return false;
        }
        if (val instanceof String && !StringUtils.hasText((String) val)) {
            return true;
        }
        else if (val instanceof Collection && !CollectionUtils.isEmpty((Collection) val)) {
            return true;
        }
        return val == null;
    }

    protected void initPermission() {
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
        for (ColumnInfo columnInfo : entityInfo.getColumnMap().values()) {
            if (columnInfo.getPermissionStrategy().getClass() == NonePermissionStrategy.class) {
                continue;
            }
            WhereCondition whereCondition = SqlBuilderUtils.getWhereCondition(columnInfo);
            if (whereCondition != null && whereCondition.getExpr().length() > 0) {
                this.appendWhereCondition(whereCondition.getExpr());
                if (whereCondition.getValue() instanceof Collection) {
                    getParam().addAll((Collection) whereCondition.getValue());
                }
                if (ObjectUtils.isArray(whereCondition.getValue())) {
                    getParam().addAll(CollectionUtils.arrayToList(whereCondition.getValue()));
                } else {
                    getParam().add(whereCondition.getValue());
                }
            }
        }
    }

    @Override
    public String getSql() {
        return sql.toString();
    }

    @Override
    public List<Object> getParam() {
        return param;
    }
}
