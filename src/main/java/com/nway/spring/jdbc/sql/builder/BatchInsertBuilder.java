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

public class BatchInsertBuilder implements ISqlBuilder {

    private List<String> columnList = new ArrayList<>();
    private final StringBuilder sql = new StringBuilder();
    private final List<Object> param = new ArrayList<>();
    private final Class beanClass;

    public BatchInsertBuilder(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public BatchInsertBuilder use(List<?> objList) {
        List<List<Object>> batchParam = new ArrayList<>(objList.size());
        for (int i = 0; i < objList.size(); i++) {
            batchParam.add(new ArrayList<>());
        }
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
        columnList = entityInfo.getColumnList();
        Map<String, ColumnInfo> columnMap = entityInfo.getColumnMap().values().stream().collect(Collectors.toMap(ColumnInfo::getColumnName, Function.identity()));
        for (String column : columnList) {
            ColumnInfo columnInfo = columnMap.get(column);
            for (int i = 0; i < objList.size(); i++) {
                Object columnValue = SqlBuilderUtils.getColumnValue(columnInfo, objList.get(i), SqlType.INSERT);
                batchParam.get(i).add(columnValue);
            }
        }
        param.addAll(batchParam);
        return this;
    }

    @Override
    public String getSql() {
        sql.append("insert into ")
                .append(SqlBuilderUtils.getTableNameFromCache(beanClass)).append(" (")
                .append(String.join(",", columnList))
                .append(") values (")
                .append(columnList.stream().map(e -> "?").collect(Collectors.joining(",")))
                .append(")");
        return sql.toString();
    }

    @Override
    public <T> Class<T> getBeanClass() {
        return this.beanClass;
    }

    @Override
    public List<Object> getParam() {
        return this.param.stream().map(e -> ((List) e).toArray()).collect(Collectors.toList());
    }
}
