package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BatchUpdateBuilder extends SqlBuilder {

    private List<String> columnNameList = new ArrayList<>();
    private final List<String> sets = new ArrayList<>();

    public BatchUpdateBuilder(Class<?> beanClass) {
        super(beanClass);
    }

    public <T, R> BatchUpdateBuilder forColumn(SFunction<T, R>... columns) {
        for (SFunction<T, R> column : columns) {
            columnNameList.add(SqlBuilderUtils.getColumn(beanClass, column));
        }
        return this;
    }

    public BatchUpdateBuilder use(List<? extends Object> params) {
        List<List<Object>> batchParam = new ArrayList<>(params.size());
        for (int i = 0; i < params.size(); i++) {
            batchParam.add(new ArrayList<Object>());
        }
        try {
            EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
            List<String> columnList = columnNameList.size() != 0 ? columnNameList : entityInfo.getColumnList();
            Map<String, ColumnInfo> columnMap = entityInfo.getColumnMap();
            for (String column : columnList) {
                ColumnInfo columnInfo = columnMap.get(column);
                for (int i = 0; i < params.size(); i++) {
                    Object columnValue = SqlBuilderUtils.getColumnValue(columnInfo, params.get(i), SqlType.UPDATE);
                    batchParam.get(i).add(columnValue);
                }
            }
            getParam().addAll(batchParam);
        } catch (Exception e) {
            throw new SqlBuilderException(e);
        }
        return this;
    }

    @Override
    public String getSql() {

        if (!this.where().getSql().trim().endsWith(" where")) {
            throw new SqlBuilderException("请明确where条件");
        }

        List<String> columnList = columnNameList.size() != 0 ? columnNameList : SqlBuilderUtils.getColumnsWithoutId(beanClass);
        String setExp = columnList.stream().map(column -> column + " = ?").collect(Collectors.joining(","));

        List<Object> paramList = getParam();
        int rowCount = ((List) paramList.get(0)).size();
        int whereParamFirstIdx = getParam().size() - 1;
        for (int i = whereParamFirstIdx == -1 ? 0 : whereParamFirstIdx, n = paramList.size(); i < n; i++) {
            Object param = paramList.get(i);
            paramList.add(i, IntStream.of(rowCount).mapToObj(e -> param).collect(Collectors.toList()));
        }

        return "update " + SqlBuilderUtils.getTableNameFromCache(beanClass) + " set " + setExp + ' ' + super.getSql();
    }
}
