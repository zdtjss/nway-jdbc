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

    protected StringBuilder preWhere = new StringBuilder(128);
    protected StringBuilder afterWhere = new StringBuilder(128);
    protected List<Object> param = new ArrayList<>();
    protected Class beanClass;

    // 查询条件一定是以where()方法开始，where后的第一个条件不要and
    private boolean canAppendAnd = false;
    private boolean canAppendWhere = true;
    // false 表示不糊了无效值，即要在代码中使用if判断是否作为查询条件
    private boolean ignoreInvalid = false;

    protected SqlBuilder(Class beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public <T> Class<T> getBeanClass() {
        return this.beanClass;
    }

    protected SqlBuilder where() {
        if (canAppendWhere) {
            preWhere.append(" where ");
        }
        canAppendWhere = false;
        return this;
    }

    /**
     * 设置为true后，需要自己判断参数是否为空
     */
    public SqlBuilder ignoreInvalid(boolean ignoreInvalid) {
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

    public SqlBuilder eq(String column, Object val) {
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
        appendCondition(val, " <> ?");
        return this;
    }

    public SqlBuilder ne(String column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " <> ?");
        return this;
    }

    public <T, R> SqlBuilder ne(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        appendCondition(column, val, " <> ?");
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
        appendAnd();
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
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val.get(), " like ?");
        return this;
    }

    public SqlBuilder likeRight(SSupplier<String> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, val), val.get() + "%", " like ?");
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
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), val.get() + "%", " like ?");
        return this;
    }

    public <T, X> SqlBuilder between(SSupplier<T> column, Supplier<X> leftVal, Supplier<X> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return this;
        }
        appendAnd();
        preWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return this;
    }

    public <T, R> SqlBuilder between(SFunction<T, R> column, Object leftVal, Object rightVal) {
        if(isInvalid(leftVal) && isInvalid(rightVal)) {
            return this;
        }
        appendAnd();
        preWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" between").append(" ? and ?");
        param.add(leftVal);
        param.add(rightVal);
        return this;
    }

    public <T, R, X> SqlBuilder between(SFunction<T, R> column, Supplier<X> leftVal, Supplier<X> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return this;
        }
        appendAnd();
        preWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return this;
    }

    public SqlBuilder between(String column, Object leftVal, Object rightVal) {
        if(isInvalid(leftVal) && isInvalid(rightVal)) {
            return this;
        }
        appendAnd();
        preWhere.append(column).append(" between").append(" ? and ?");
        param.add(leftVal);
        param.add(rightVal);
        return this;
    }

    public <X> SqlBuilder between(String column, Supplier<X> leftVal, Supplier<X> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return this;
        }
        appendAnd();
        preWhere.append(column).append(" between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return this;
    }

    public <T, X> SqlBuilder notBetween(SSupplier<T> column, Supplier<X> leftVal, Supplier<X> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return this;
        }
        appendAnd();
        preWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return this;
    }

    public <X> SqlBuilder notBetween(String column, Supplier<X> leftVal, Supplier<X> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return this;
        }
        appendAnd();
        preWhere.append(column).append(" not between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return this;
    }

    public SqlBuilder notBetween(String column, Object leftVal, Object rightVal) {
        if(isInvalid(leftVal) && isInvalid(rightVal)) {
            return this;
        }
        appendAnd();
        preWhere.append(column).append(" not between").append(" ? and ?");
        param.add(leftVal);
        param.add(rightVal);
        return this;
    }

    public <T, R, X> SqlBuilder notBetween(SFunction<T, R> column, Supplier<X> leftVal, Supplier<X> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return this;
        }
        appendAnd();
        preWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return this;
    }

    public <T, R> SqlBuilder notBetween(SFunction<T, R> column, Object leftVal, Object rightVal) {
        if(isInvalid(leftVal) && isInvalid(rightVal)) {
            return this;
        }
        appendAnd();
        preWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not between").append(" ? and ?");
        param.add(leftVal);
        param.add(rightVal);
        return this;
    }

    public SqlBuilder and(Consumer<SqlBuilder> lambdaWhereBuilder) {
        SqlBuilder lq = new SqlBuilder(beanClass);
        lambdaWhereBuilder.accept(lq);
        String sql = lq.getSql();
        if (sql.length() > 7) {
            appendAnd();
            // where() 方法添加的 " where " 长度
            if(preWhere.length() == 7) {
                preWhere.append(sql.substring(6));
            }
            else {
                preWhere.append(" (").append(sql.substring(6)).append(")");
            }
            param.addAll(lq.getParam());
        }
        return this;
    }

    public <T> SqlBuilder or(Consumer<SqlBuilder> lambdaWhereBuilder) {
        SqlBuilder lq = new SqlBuilder(beanClass);
        lambdaWhereBuilder.accept(lq);
        String sql = lq.getSql();
        // where 的长度
        if (sql.length() > 7) {
            where();
            canAppendAnd = true;
            // where() 方法添加的 " where " 长度
            if(preWhere.length() == 7) {
                preWhere.append("(").append(sql.substring(6)).append(")");
            }
            else {
                preWhere.append(" or (").append(sql.substring(6)).append(")");
            }
            param.addAll(lq.getParam());
        }
        return this;
    }

    public <T> SqlBuilder or() {
        preWhere.append(" or ");
        canAppendAnd = false;
        return this;
    }

    public SqlBuilder in(String column, Collection<?> val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        preWhere.append(column).append(" in (");
        param.addAll(val);
        for (int i = 0; i < val.size(); i++) {
            preWhere.append("?,");
        }
        if (!val.isEmpty()) {
            preWhere.setCharAt(preWhere.length() - 1, ')');
        }
        return this;
    }

    public <T, R> SqlBuilder in(SFunction<T, R> column, Collection<?> val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        preWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" in (");
        param.addAll(val);
        for (int i = 0; i < val.size(); i++) {
            preWhere.append("?,");
        }
        if (!val.isEmpty()) {
            preWhere.setCharAt(preWhere.length() - 1, ')');
        }
        return this;
    }

    public SqlBuilder notIn(String column, Collection<?> val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        preWhere.append(column).append(" not in (");
        param.addAll(val);
        for (int i = 0; i < val.size(); i++) {
            preWhere.append("?,");
        }
        if (!val.isEmpty()) {
            preWhere.setCharAt(preWhere.length() - 1, ')');
        }
        return this;
    }

    public <T, R> SqlBuilder notIn(SFunction<T, R> column, Collection<?> val) {
        if(isInvalid(val)) {
            return this;
        }
        appendAnd();
        preWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not in (");
        param.addAll(val);
        for (int i = 0; i < val.size(); i++) {
            preWhere.append("?,");
        }
        if (!val.isEmpty()) {
            preWhere.setCharAt(preWhere.length() - 1, ')');
        }
        return this;
    }

    public <T> SqlBuilder groupBy(String... column) {
        afterWhere.append(" group by ").append(Arrays.stream(column).collect(Collectors.joining(",")));
        return this;
    }

    @SafeVarargs
    public final <T, R> SqlBuilder groupBy(SFunction<T, R>... columns) {
        afterWhere.append(" group by ");
        for (SFunction<T, R> column : columns) {
            afterWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(",");
        }
        afterWhere.deleteCharAt(afterWhere.length() - 1);
        return this;
    }

    @SafeVarargs
    public final <T, R> SqlBuilder orderBy(SFunction<T, R>... columns) {
        afterWhere.append(" order by ");
        for (SFunction<T, R> column : columns) {
            afterWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(",");
        }
        afterWhere.deleteCharAt(afterWhere.length() - 1);
        return this;
    }

    public SqlBuilder orderBy(String... columns) {
        afterWhere.append(" order by ");
        for (String column : columns) {
            afterWhere.append(column).append(",");
        }
        afterWhere.deleteCharAt(afterWhere.length() - 1);
        return this;
    }

    public <T, R> SqlBuilder andOrderByAsc(SFunction<T, R> column) {
        afterWhere.append(",").append(SqlBuilderUtils.getColumn(beanClass, column)).append(" asc");
        return this;
    }

    public SqlBuilder andOrderByAsc(String... columns) {
        for (String column : columns) {
            afterWhere.append(",").append(column).append(" asc");
        }
        return this;
    }

    @SafeVarargs
    public final <T, R> SqlBuilder orderByDesc(SFunction<T, R>... columns) {
        afterWhere.append(" order by ");
        for (SFunction<T, R> column : columns) {
            afterWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" desc,");
        }
        afterWhere.deleteCharAt(afterWhere.length() - 1);
        return this;
    }

    public SqlBuilder orderByDesc(String... columns) {
        afterWhere.append(" order by ");
        for (String column : columns) {
            afterWhere.append(column).append(" desc,");
        }
        afterWhere.deleteCharAt(afterWhere.length() - 1);
        return this;
    }

    public <T, R> SqlBuilder andOrderByDesc(SFunction<T, R> column) {
        afterWhere.append(",").append(SqlBuilderUtils.getColumn(beanClass, column)).append(" desc");
        return this;
    }

    public SqlBuilder andOrderByDesc(String... columns) {
        for (String column : columns) {
            afterWhere.append(",").append(column).append(" desc");
        }
        return this;
    }

    public <T> SqlBuilder having(Consumer<SqlBuilder> lambdaWhereBuilder) {
        SqlBuilder lq = new SqlBuilder(beanClass);
        lambdaWhereBuilder.accept(lq);
        String sql = lq.getSql();
        if (sql.length() > 7) {
            afterWhere.append(" having ").append(lq.getSql().substring(7));
            param.addAll(lq.getParam());
        }
        return this;
    }

    public <T> SqlBuilder set(SSupplier<T> val) {
        throw new UnsupportedOperationException("此方法只有update时使用");
    }

    public <T, R> SqlBuilder set(SFunction<T, R> column, Object val) {
        throw new UnsupportedOperationException("此方法只有update时使用");
    }

    private void appendAnd() {
        if (canAppendAnd) {
            preWhere.append(" and ");
        } else {
            where();
        }
        // canAppendAnd 只失效一次，只有在or()方法后第一次补充条件时，新补充的条件不需要 and
        canAppendAnd = true;
    }

    /**
     * 此方法应谨慎使用，如果有用户输入值，则可能存在SQL注入的风险。
     *
     * @param whereCondition
     * @return
     */
    public SqlBuilder appendCondition(WhereCondition whereCondition) {
        if (whereCondition != null && whereCondition.getExpr().length() > 0) {
            this.appendWhereCondition(whereCondition.getExpr());
            if (whereCondition.getValue() instanceof Collection) {
                getParam().addAll((Collection) whereCondition.getValue());
            }
            else if (ObjectUtils.isArray(whereCondition.getValue())) {
                getParam().addAll(CollectionUtils.arrayToList(whereCondition.getValue()));
            }
            else {
                getParam().add(whereCondition.getValue());
            }
        }
        return this;
    }

    private SqlBuilder appendWhereCondition(String whereCondition) {
        appendAnd();
        preWhere.append(' ').append(whereCondition).append(' ');
        return this;
    }

    private <T> void appendCondition(SSupplier<T> val, String op) {
        preWhere.append(SqlBuilderUtils.getColumn(beanClass, val)).append(op);
        param.add(val.get());
    }

    private <T, R> void appendCondition(SFunction<T, R> column, Object val, String op) {
        preWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(op);
        param.add(val);
    }

    private void appendCondition(String column, Object val, String op) {
        preWhere.append(column).append(op);
        param.add(val);
    }

    /**
     *
     * 当ignoreInvalid为true时，说明需要程序自动判断参数是否有效，否则，需要调用者判断。
     *
     * 判断逻辑：null和空集合为无效值，空字符串和基本类型的默认值是有效值
     *
     * @param val 是无效的吗
     * @return
     */
    protected boolean isInvalid(Object val) {
        // ignoreInvalid false 表示不忽略无效参数  不需要判断参数是否有效
        // ignoreInvalid true 表示忽略无效参数  需要工具判断参数值是否有效
        if (!ignoreInvalid) {
            // 不需要工具判断参数是否有效时 返回false  则填充条件的方法会进行添加查询条件  当用户传入无效参数  程序会抛出异常  而不会导致数据越权
            return false;
        }
        if (val instanceof Collection && CollectionUtils.isEmpty((Collection) val)) {
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
            appendCondition(SqlBuilderUtils.getWhereCondition(columnInfo));
        }
    }

    protected StringBuilder getPreWhere() {
        return this.preWhere;
    }

    @Override
    public String getSql() {
        String sql = this.preWhere.toString();
        // 不能添加where 说明已经有where存在  但是如果参数为空  有可能是因为忽略无效条件导致的
        if (!canAppendWhere && param.size() == 0) {
            sql = sql.substring(0, sql.length() - 8);
        }
        return sql + afterWhere.toString();
    }

    @Override
    public List<Object> getParam() {
        return param;
    }
}
