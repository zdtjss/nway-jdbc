package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BatchUpdateByIdBuilder implements ISqlBuilder {

    protected List<?> data;
    protected StringBuilder sql = new StringBuilder();
    protected List<Object> param = new ArrayList<>();
    private List<String> columnNameList = new ArrayList<>();
    protected Class beanClass;

    public BatchUpdateByIdBuilder(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public ISqlBuilder use(List<?> data) {
        this.data = data;
        List<List<Object>> batchParam = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            batchParam.add(new ArrayList<>());
        }
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);

        List<String> columnList = columnNameList.size() != 0 ? columnNameList : SqlBuilderUtils.getColumnsWithoutId(beanClass);
        Map<String, ColumnInfo> columnMap = entityInfo.getColumnMap().values().stream().collect(Collectors.toMap(ColumnInfo::getColumnName, Function.identity()));
        for (String column : columnList) {
            ColumnInfo columnInfo = columnMap.get(column);
            for (int i = 0; i < data.size(); i++) {
                Object columnValue = SqlBuilderUtils.getColumnValue(columnInfo, data.get(i), SqlType.UPDATE);
                batchParam.get(i).add(columnValue);
            }
        }
        this.param.addAll(batchParam);
        return this;
    }

    @Override
    public String getSql() {

        StringBuilder sql = new StringBuilder();

        List<String> columnList = columnNameList.size() != 0 ? columnNameList : SqlBuilderUtils.getColumnsWithoutId(beanClass);
        String setExp = columnList.stream().map(column -> column + " = ?").collect(Collectors.joining(","));

        sql.append("update ").append(SqlBuilderUtils.getTableNameFromCache(beanClass)).append(" set ").append(setExp).append(" where ");

        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
        sql.append(entityInfo.getId().getColumnName()).append(" = ?");

        for (int i = 0; i < this.param.size(); i++) {
            Object idVal = SqlBuilderUtils.getColumnValue(entityInfo.getId(), this.data.get(i), SqlType.UPDATE);
            if (idVal == null) {
                throw new SqlBuilderException("更新失败，批量更新时存在主键为空的数据。");
            }
            ((List) this.param.get(i)).add(idVal);
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
}
