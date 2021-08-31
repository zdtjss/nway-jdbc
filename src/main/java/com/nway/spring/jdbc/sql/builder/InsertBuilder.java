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

public class InsertBuilder implements ISqlBuilder {

    private final List<String> columns = new ArrayList<>();
    private final StringBuilder sql = new StringBuilder();
    private final List<Object> param = new ArrayList<>();
    private final Class beanClass;

    public InsertBuilder(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public InsertBuilder use(Object obj) {
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
        List<String> columnList = entityInfo.getColumnList();
        Map<String, ColumnInfo> columnMap = entityInfo.getColumnMap().values().stream().collect(Collectors.toMap(ColumnInfo::getColumnName, Function.identity()));
        for (String column : columnList) {
            ColumnInfo columnInfo = columnMap.get(column);
            Object value = SqlBuilderUtils.getColumnValue(columnInfo, obj, SqlType.INSERT);
            if (value != null) {
                columns.add(columnInfo.getColumnName());
                param.add(value);
            }
        }
        return this;
    }

    @Override
    public String getSql() {
        sql.append("insert into ")
                .append(SqlBuilderUtils.getTableNameFromCache(beanClass)).append(" (")
                .append(String.join(",", columns))
                .append(") values (")
                .append(columns.stream().map(e -> "?").collect(Collectors.joining(",")))
                .append(")");
        return sql.toString();
    }

    @Override
    public <T> Class<T> getBeanClass() {
        return this.beanClass;
    }

    @Override
    public List<Object> getParam() {
        return this.param;
    }

    public Object getKeyValue() {
        int keyIdx = -1;
        Object keyVal = null;
        String idName = SqlBuilderUtils.getIdName(getBeanClass());
        for (int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i);
            if (columnName.equals(idName)) {
                keyIdx = i;
                break;
            }
        }
        if (keyIdx != -1) {
            keyVal = getParam().get(keyIdx);
        }
        if (Long.valueOf(0).equals(keyVal) || Integer.valueOf(0).equals(keyVal)) {
            keyVal = null;
        }
        return keyVal;
    }
}
