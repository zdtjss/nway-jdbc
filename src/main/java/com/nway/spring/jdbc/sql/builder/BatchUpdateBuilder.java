package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.fill.NoneFillStrategy;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 请谨慎使用此类，因为where条件约束的数据可能是一个范围，此范围可能包含非预期数据。
 */
public class BatchUpdateBuilder implements ISqlBuilder {

    private Class beanClass;
    protected List<?> data;
    private final List<Object> param = new ArrayList<>();
    protected final List<String> columnNameList = new ArrayList<>();
    protected final List<CondExp> whereCondList = new ArrayList<>();

    public BatchUpdateBuilder(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @SafeVarargs
    public final <T, R> BatchUpdateBuilder columns(SFunction<T, R>... columns) {
        for (SFunction<T, R> column : columns) {
            columnNameList.add(SqlBuilderUtils.getColumn(beanClass, column));
        }
        return this;
    }

    public BatchUpdateBuilder use(List<?> params) {
        this.data = params;
        return this;
    }

    /**
     * 只支持and连接，对于SqlOperator建议慎重使用范围类，最好只使用SqlOperator.EQ类型
     *
     * @return
     */
    public <T, R> BatchUpdateBuilder addCondition(SFunction<T, R> column, SqlOperator operator) {
        this.whereCondList.add(new CondExp(SqlBuilderUtils.getColumn(beanClass, column), operator));
        return this;
    }

    @Override
    public String getSql() {

        // 组织sql参数
        makeSqlParam();

        StringBuilder sql = new StringBuilder();

        sql.append("update ").append(SqlBuilderUtils.getTableNameFromCache(beanClass)).append(" set ");
        int initLength = sql.length();

        List<String> columnList = getColumnNameList(SqlBuilderUtils.getEntityInfo(beanClass));
        for (String col : columnList) {
            sql.append(col).append(" = ?,");
        }
        if (sql.length() > initLength) {
            sql.deleteCharAt(sql.length() - 1);
        }
        sql.append(" where ");
        for (int i = 0; i < whereCondList.size(); i++) {
            if (i != 0) {
                sql.append(" and ");
            }
            CondExp condExp = whereCondList.get(i);
            sql.append(condExp.column).append(' ').append(condExp.operator);
            if (!(SqlOperator.IS_NULL.getOperator().equals(condExp.operator)
                    || SqlOperator.IS_NOT_NULL.getOperator().equals(condExp.operator))) {
                sql.append(' ').append('?');
            }
        }
        return sql.toString();
    }

    @Override
    public <T> Class<T> getBeanClass() {
        return beanClass;
    }

    @Override
    public List<Object> getParam() {
        return param.stream().map(e -> ((List<Object>) e).toArray()).collect(Collectors.toList());
    }

    public List<?> getData() {
        return data;
    }

    private void makeSqlParam() {
        int dataSize = this.data.size();
        List<List<Object>> batchParam = new ArrayList<>(dataSize);
        for (int i = 0; i < dataSize; i++) {
            batchParam.add(new ArrayList<>());
        }
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
        List<String> columnList = getColumnNameList(entityInfo);
        Map<String, ColumnInfo> columnMap = entityInfo.getColumnMap().values().stream().collect(Collectors.toMap(ColumnInfo::getColumnName, Function.identity()));
        for (String column : columnList) {
            ColumnInfo columnInfo = columnMap.get(column);
            for (int i = 0; i < dataSize; i++) {
                Object columnValue = SqlBuilderUtils.getColumnValue(columnInfo, this.data.get(i), SqlType.UPDATE);
                batchParam.get(i).add(columnValue);
            }
        }
        for (CondExp condExp : whereCondList) {
            if (SqlOperator.IS_NULL.getOperator().equals(condExp.operator)
                    || SqlOperator.IS_NOT_NULL.getOperator().equals(condExp.operator)) {
                continue;
            }
            ColumnInfo columnInfo = columnMap.get(condExp.column);
            for (int i = 0; i < dataSize; i++) {
                Object columnValue = SqlBuilderUtils.getColumnValue(columnInfo, this.data.get(i), SqlType.UPDATE);
                batchParam.get(i).add(columnValue);
            }
        }
        this.param.addAll(batchParam);
    }

    private List<String> getColumnNameList(EntityInfo entityInfo) {
        List<String> columnList = columnNameList.size() != 0 ? columnNameList : SqlBuilderUtils.getColumnsWithoutId(beanClass);
        for (ColumnInfo columnInfo : entityInfo.getColumnMap().values()) {
            if (!NoneFillStrategy.class.equals(columnInfo.getFillStrategy().getClass())
                    && columnInfo.getFillStrategy().isSupport(SqlType.UPDATE)) {
                if(!columnList.contains(columnInfo.getColumnName())) {
                    columnList.add(columnInfo.getColumnName());
                }
            }
        }
        return columnList;
    }

    private static class CondExp {

        private final String column;
        private final String operator;

        public CondExp(String column, SqlOperator operator) {
            this.column = column;
            this.operator = operator.getOperator();
        }
    }
}
