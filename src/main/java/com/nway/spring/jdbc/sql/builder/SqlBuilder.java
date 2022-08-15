package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.function.SSupplier;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;
import com.nway.spring.jdbc.sql.permission.NonePermissionStrategy;
import com.nway.spring.jdbc.sql.permission.WhereCondition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class SqlBuilder<X extends SqlBuilder<X>> implements ISqlBuilder {

    protected StringBuilder where = new StringBuilder(128);
    protected StringBuilder afterWhere = new StringBuilder(128);
    protected List<Object> param = new ArrayList<>();
    protected Class beanClass;

    /**
     * 查询条件一定是以where()方法开始，where后的第一个条件不要and
     */
    private boolean canAppendAnd = false;
    private boolean canAppendWhere = true;
    /**
     * false 表示不糊了无效值，即要在代码中使用if判断是否作为查询条件
     */
    private boolean ignoreInvalid = false;
    /**
     * true 判断空字符串和全null集合
     */
    private boolean ignoreInvalidDeep = false;

    /**
     * 是否忽略权限
     */
    protected boolean ignorePower = false;

    protected final X thisObj = (X) this;

    protected SqlBuilder(Class beanClass) {
        this.beanClass = beanClass;
    }

    protected abstract SqlType getSqlType();

    @Override
    public <T> Class<T> getBeanClass() {
        return this.beanClass;
    }

    protected X where() {
        if (canAppendWhere) {
            where.append(" where ");
        }
        canAppendWhere = false;
        return thisObj;
    }

    /**
     * 设置为false后，需要自己判断参数是否为空
     */
    public X ignoreInvalid(boolean ignoreInvalid) {
        this.ignoreInvalid = ignoreInvalid;
        return thisObj;
    }

    /**
     * true 忽略空字符串和全null的集合作为查询条件
     */
    public X ignoreInvalidDeep(boolean ignoreInvalidDeep) {
        this.ignoreInvalid = ignoreInvalidDeep;
        this.ignoreInvalidDeep = ignoreInvalidDeep;
        return thisObj;
    }

    public <T> X eq(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(val, " = ?");
        return thisObj;
    }

    public <T, R> X eq(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val, " = ?");
        return thisObj;
    }

    public X eq(String column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val, " = ?");
        return thisObj;
    }

    public <T> X ne(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(val, " <> ?");
        return thisObj;
    }

    public X ne(String column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val, " <> ?");
        return thisObj;
    }

    public <T, R> X ne(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val, " <> ?");
        return thisObj;
    }

    public <T> X gt(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(val, " > ?");
        return thisObj;
    }

    public X gt(String column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val, " > ?");
        return thisObj;
    }

    public <T, R> X gt(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val, " > ?");
        return thisObj;
    }

    public <T> X ge(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(val, " >= ?");
        return thisObj;
    }

    public X ge(String column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val, " >= ?");
        return thisObj;
    }

    public <T, R> X ge(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val, " >= ?");
        return thisObj;
    }

    public <T> X lt(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition( val, " < ?");
        return thisObj;
    }

    public X lt(String column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val, " < ?");
        return thisObj;
    }

    public <T, R> X lt(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val, " < ?");
        return thisObj;
    }

    public <T> X le(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(val, " <= ?");
        return thisObj;
    }

    public X le(String column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val, " <= ?");
        return thisObj;
    }

    public <T, R> X le(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val, " <= ?");
        return thisObj;
    }

    public X like(SSupplier<String> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, val), "%" + val.get() + "%", " like ?");
        return thisObj;
    }

    public X like(String column, String val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, "%" + val + "%", " like ?");
        return thisObj;
    }

    public <T, R> X like(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val + "%", " like ?");
        return thisObj;
    }

    public <T, R> X like(SFunction<T, R> column, Supplier<String> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val.get() + "%", " like ?");
        return thisObj;
    }

    public X notLike(SSupplier<String> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, val), "%" + val.get() + "%", " not like ?");
        return thisObj;
    }

    public X notLike(String column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, "%" + val + "%", " not like ?");
        return thisObj;
    }

    public <T, R> X notLike(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val + "%", " not like ?");
        return thisObj;
    }

    public <T, R> X notLike(SFunction<T, R> column, Supplier<String> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val.get() + "%", " not like ?");
        return thisObj;
    }

    public <T> X likeLeft(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, val), "%" + val.get(), " like ?");
        return thisObj;
    }

    public X likeLeft(String column, String val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, "%" + val, " like ?");
        return thisObj;
    }

    public <T, R> X likeLeft(SFunction<T, R> column, String val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val, " like ?");
        return thisObj;
    }

    public <T, R> X likeLeft(SFunction<T, R> column, SSupplier<String> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), "%" + val.get(), " like ?");
        return thisObj;
    }

    public X likeRight(SSupplier<String> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, val), val.get() + "%", " like ?");
        return thisObj;
    }

    public X likeRight(String column, String val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(column, val + "%", " like ?");
        return thisObj;
    }

    public <T, R> X likeRight(SFunction<T, R> column, String val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), val + "%", " like ?");
        return thisObj;
    }

    public <T, R> X likeRight(SFunction<T, R> column, SSupplier<String> val) {
        if(isInvalid(val.get())) {
            return thisObj;
        }
        appendAnd();
        appendCondition(SqlBuilderUtils.getColumn(beanClass, column), val.get() + "%", " like ?");
        return thisObj;
    }

    public <T, R> X isNull(SFunction<T, R> column) {
        appendAnd();
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" is null");
        return thisObj;
    }

    public <T> X isNull(SSupplier<T> column) {
        appendAnd();
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" is null");
        return thisObj;
    }

    public X isNull(String column) {
        appendAnd();
        where.append(column).append(" is null");
        return thisObj;
    }

    public <T, R> X isNotNull(SFunction<T, R> column) {
        appendAnd();
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" is not null");
        return thisObj;
    }

    public <T> X isNotNull(SSupplier<T> column) {
        appendAnd();
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" is not null");
        return thisObj;
    }

    public X isNotNull(String column) {
        appendAnd();
        where.append(column).append(" is not null");
        return thisObj;
    }

    public <T> X between(SSupplier<T> column, Supplier<T> leftVal, Supplier<T> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return thisObj;
        }
        appendAnd();
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return thisObj;
    }

    public <T, R> X between(SFunction<T, R> column, Object leftVal, Object rightVal) {
        if(isInvalid(leftVal) && isInvalid(rightVal)) {
            return thisObj;
        }
        appendAnd();
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" between").append(" ? and ?");
        param.add(leftVal);
        param.add(rightVal);
        return thisObj;
    }

    public <T, R, O> X between(SFunction<T, R> column, Supplier<O> leftVal, Supplier<O> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return thisObj;
        }
        appendAnd();
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return thisObj;
    }

    public X between(String column, Object leftVal, Object rightVal) {
        if(isInvalid(leftVal) && isInvalid(rightVal)) {
            return thisObj;
        }
        appendAnd();
        where.append(column).append(" between").append(" ? and ?");
        param.add(leftVal);
        param.add(rightVal);
        return thisObj;
    }

    public <T> X between(String column, Supplier<T> leftVal, Supplier<T> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return thisObj;
        }
        appendAnd();
        where.append(column).append(" between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return thisObj;
    }

    public <T> X notBetween(SSupplier<T> column, Supplier<T> leftVal, Supplier<T> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return thisObj;
        }
        appendAnd();
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return thisObj;
    }

    public <T> X notBetween(String column, Supplier<T> leftVal, Supplier<T> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return thisObj;
        }
        appendAnd();
        where.append(column).append(" not between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return thisObj;
    }

    public X notBetween(String column, Object leftVal, Object rightVal) {
        if(isInvalid(leftVal) && isInvalid(rightVal)) {
            return thisObj;
        }
        appendAnd();
        where.append(column).append(" not between").append(" ? and ?");
        param.add(leftVal);
        param.add(rightVal);
        return thisObj;
    }

    public <T, R, O> X notBetween(SFunction<T, R> column, Supplier<O> leftVal, Supplier<O> rightVal) {
        if(isInvalid(leftVal.get()) && isInvalid(rightVal.get())) {
            return thisObj;
        }
        appendAnd();
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not between").append(" ? and ?");
        param.add(leftVal.get());
        param.add(rightVal.get());
        return thisObj;
    }

    public <T, R> X notBetween(SFunction<T, R> column, Object leftVal, Object rightVal) {
        if(isInvalid(leftVal) && isInvalid(rightVal)) {
            return thisObj;
        }
        appendAnd();
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not between").append(" ? and ?");
        param.add(leftVal);
        param.add(rightVal);
        return thisObj;
    }

    public X and(Consumer<X> whereBuilder) {
        X lq = createObj();
        whereBuilder.accept(lq);
        StringBuilder sql = lq.getWhere();
        if (sql.length() > 7) {
            appendAnd();
            where.append("(").append(sql.substring(6)).append(")");
            param.addAll(lq.getParam());
        }
        return thisObj;
    }

    public X or(Consumer<X> whereBuilder) {
        X lq = createObj();
        whereBuilder.accept(lq);
        StringBuilder sql = lq.getWhere();
        // where 的长度
        if (sql.length() > 7) {
            where();
            canAppendAnd = true;
            // where() 方法添加的 " where " 长度
            if(where.length() == 7) {
                where.append("(").append(sql.substring(6)).append(")");
            }
            else {
                where.append(" or (").append(sql.substring(6)).append(")");
            }
            param.addAll(lq.getParam());
        }
        return thisObj;
    }

    public <T> X or() {
        where.append(" or ");
        canAppendAnd = false;
        return thisObj;
    }

    private X createObj() {
        X lq;
        try {
            Class<X> aClass = (Class<X>) thisObj.getClass();
            lq = aClass.getDeclaredConstructor(Class.class).newInstance(beanClass);
        } catch (Exception e) {
            throw new SqlBuilderException(e);
        }
        return lq;
    }

    public X in(String column, Collection<?> val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        where.append(column).append(" in (");
        param.addAll(val);
        for (int i = 0; i < val.size(); i++) {
            where.append("?,");
        }
        if (!val.isEmpty()) {
            where.setCharAt(where.length() - 1, ')');
        }
        return thisObj;
    }

    public <T, R> X in(SFunction<T, R> column, Collection<?> val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" in (");
        param.addAll(val);
        for (int i = 0; i < val.size(); i++) {
            where.append("?,");
        }
        if (!val.isEmpty()) {
            where.setCharAt(where.length() - 1, ')');
        }
        return thisObj;
    }

    public X notIn(String column, Collection<?> val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        where.append(column).append(" not in (");
        param.addAll(val);
        for (int i = 0; i < val.size(); i++) {
            where.append("?,");
        }
        if (!val.isEmpty()) {
            where.setCharAt(where.length() - 1, ')');
        }
        return thisObj;
    }

    public <T, R> X notIn(SFunction<T, R> column, Collection<?> val) {
        if(isInvalid(val)) {
            return thisObj;
        }
        appendAnd();
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" not in (");
        param.addAll(val);
        for (int i = 0; i < val.size(); i++) {
            where.append("?,");
        }
        if (!val.isEmpty()) {
            where.setCharAt(where.length() - 1, ')');
        }
        return thisObj;
    }

    private void appendAnd() {
        if (canAppendAnd) {
            where.append(" and ");
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
    public X appendCondition(WhereCondition whereCondition) {
        if (whereCondition != null && whereCondition.getExpr().length() > 0) {
            this.appendWhereCondition(whereCondition.getExpr());
            if (whereCondition.getValue() instanceof Collection) {
                this.param.addAll((Collection) whereCondition.getValue());
            }
            else if (ObjectUtils.isArray(whereCondition.getValue())) {
                this.param.addAll(CollectionUtils.arrayToList(whereCondition.getValue()));
            }
            else {
                this.param.add(whereCondition.getValue());
            }
        }
        return thisObj;
    }

    private X appendWhereCondition(String whereCondition) {
        appendAnd();
        where.append(' ').append(whereCondition).append(' ');
        return thisObj;
    }

    private <T> void appendCondition(SSupplier<T> val, String op) {
        where.append(SqlBuilderUtils.getColumn(beanClass, val)).append(op);
        param.add(val.get());
    }

    private <T, R> void appendCondition(SFunction<T, R> column, Object val, String op) {
        where.append(SqlBuilderUtils.getColumn(beanClass, column)).append(op);
        param.add(val);
    }

    private void appendCondition(String column, Object val, String op) {
        where.append(column).append(op);
        param.add(val);
    }

    /**
     *
     * 当ignoreInvalid为true时，说明需要程序自动判断参数是否有效，否则，需要调用者判断。
     *
     * 判断逻辑：null和空集合为无效值，空字符串和基本类型的默认值是有效值
     *
     * @param val 是无效的吗
     * @return true 无效
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
        if (ignoreInvalidDeep) {
            if (val instanceof String) {
                return val.toString().length() == 0;
            }
            if (val instanceof Collection) {
                return ((Collection) val).stream().noneMatch(Objects::nonNull);
            }
        }
        return val == null;
    }

    public void ignorePermission() {
        this.ignorePower = true;
    }

    protected void initPermission() {
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
        for (ColumnInfo columnInfo : entityInfo.getColumnMap().values()) {
            if (columnInfo.getPermissionStrategy().getClass() == NonePermissionStrategy.class) {
                continue;
            }
            appendCondition(SqlBuilderUtils.getWhereCondition(getSqlType(), columnInfo));
        }
    }

    protected StringBuilder getWhere() {
        return this.where;
    }

    @Override
    public String getSql() {
        if (!this.ignorePower) {
            initPermission();
        }
        String sql = this.where.toString();
        // 以where结尾  有可能是因为忽略无效条件导致的
        if (sql.endsWith(" where ")) {
            sql = sql.substring(0, sql.length() - 8);
        }
        return sql + afterWhere.toString();
    }

    @Override
    public List<Object> getParam() {
        return param;
    }
}
