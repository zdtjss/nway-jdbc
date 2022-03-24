package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BatchUpdateBuilder implements ISqlBuilder {

    private Class beanClass;
    private List<Object> param = new ArrayList<>();
    protected List<? extends Object> data;
    protected final List<String> columnNameList = new ArrayList<>();
    protected final List<CondExp> whereCondList = new ArrayList<>();

    public BatchUpdateBuilder(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public <T, R> BatchUpdateBuilder columns(SFunction<T, R>... columns) {
        for (SFunction<T, R> column : columns) {
            columnNameList.add(SqlBuilderUtils.getColumn(beanClass, column));
        }
        return this;
    }

    public BatchUpdateBuilder use(List<? extends Object> params) {
        this.data = params;
        return this;
    }

    public <T, R> BatchUpdateBuilder addCondition(SFunction<T, R> column, String exp) {
        this.whereCondList.add(new CondExp(SqlBuilderUtils.getColumn(beanClass, column), exp));
        return this;
    }

    @Override
    public String getSql() {

        // 组织sql参数
        makeSqlParam();

        StringBuilder sql = new StringBuilder();

        sql.append("update ").append(SqlBuilderUtils.getTableNameFromCache(beanClass)).append(" set ");
        int initLength = sql.length();

        List<String> columnList = columnNameList.size() != 0 ? columnNameList : SqlBuilderUtils.getColumnsWithoutId(beanClass);
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
            sql.append(condExp.column).append(' ').append(condExp.exp).append(' ').append('?');
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

    public List<? extends Object> getData() {
        return data;
    }

    private void makeSqlParam() {
        int dataSize = this.data.size();
        List<List<Object>> batchParam = new ArrayList<>(dataSize);
        for (int i = 0; i < dataSize; i++) {
            batchParam.add(new ArrayList<>());
        }
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
        List<String> columnList = columnNameList.size() != 0 ? columnNameList : entityInfo.getColumnList();
        Map<String, ColumnInfo> columnMap = entityInfo.getColumnMap().values().stream().collect(Collectors.toMap(ColumnInfo::getColumnName, Function.identity()));
        for (String column : columnList) {
            ColumnInfo columnInfo = columnMap.get(column);
            for (int i = 0; i < dataSize; i++) {
                Object columnValue = SqlBuilderUtils.getColumnValue(columnInfo, this.data.get(i), SqlType.UPDATE);
                batchParam.get(i).add(columnValue);
            }
        }
        for (CondExp condExp : whereCondList) {
            ColumnInfo columnInfo = columnMap.get(condExp.column);
            for (int i = 0; i < dataSize; i++) {
                Object columnValue = SqlBuilderUtils.getColumnValue(columnInfo, this.data.get(i), SqlType.UPDATE);
                batchParam.get(i).add(columnValue);
            }
        }
        this.param.addAll(batchParam);
    }


    private static class CondExp {

        private final String column;
        private final String exp;

        public CondExp(String column, String exp) {
            this.column = column;
            this.exp = exp;
        }
    }
}
