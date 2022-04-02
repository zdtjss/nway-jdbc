package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.fill.NoneFillStrategy;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UpdateBeanBuilder extends SqlBuilder<UpdateBeanBuilder> {

    private final List<String> sets = new ArrayList<>();
    private final List<Object> setsParam = new ArrayList<>();
    protected final List<String> columnNameList = new ArrayList<>();
    private final Object obj;

    public UpdateBeanBuilder(Object obj) {
        super(obj.getClass());
        this.obj = obj;
    }

    @SafeVarargs
    public final <T, R> UpdateBeanBuilder columns(SFunction<T, R>... columns) {
        for (SFunction<T, R> column : columns) {
            columnNameList.add(SqlBuilderUtils.getColumn(beanClass, column));
        }
        return this;
    }

    public UpdateBeanBuilder columns(String... columns) {
        columnNameList.addAll(Arrays.asList(columns));
        return this;
    }

    private void init() {
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
        List<String> columnList = getColumnNameList(entityInfo);
        Map<String, ColumnInfo> columnMap = entityInfo.getColumnMap().values().stream().collect(Collectors.toMap(ColumnInfo::getColumnName, Function.identity()));
        for (String column : columnList) {
            ColumnInfo columnInfo = columnMap.get(column);
            Object value = SqlBuilderUtils.getColumnValue(columnInfo, obj, SqlType.UPDATE);
            if (value != null) {
                sets.add(columnInfo.getColumnName() + " = ?");
                setsParam.add(value);
            }
        }
    }

    private List<String> getColumnNameList(EntityInfo entityInfo) {
        List<String> columnList = columnNameList.size() != 0 ? columnNameList : SqlBuilderUtils.getColumnsWithoutId(beanClass);
        for (ColumnInfo columnInfo : entityInfo.getColumnMap().values()) {
            if (!NoneFillStrategy.class.equals(columnInfo.getFillStrategy().getClass())
                    && columnInfo.getFillStrategy().isSupport(SqlType.UPDATE)) {
                if (!columnList.contains(columnInfo.getColumnName())) {
                    columnList.add(columnInfo.getColumnName());
                }
            }
        }
        return columnList;
    }

    @Override
    public String getSql() {
        init();
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(SqlBuilderUtils.getTableNameFromCache(beanClass)).append(" set ")
                .append(String.join(",", sets)).append(super.getSql());
        return sql.toString();
    }

    @Override
    public List<Object> getParam() {
        setsParam.addAll(super.getParam());
        return setsParam;
    }
}
