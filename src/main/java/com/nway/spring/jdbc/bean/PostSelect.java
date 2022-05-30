package com.nway.spring.jdbc.bean;

import com.nway.spring.jdbc.NwayException;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.List;
import java.util.stream.Collectors;

class PostSelect {

    public static <T> List<T> post(List<T> objs, Class<T> type) {
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(type);
        List<ColumnInfo> columns = entityInfo.getColumnMap().values()
                .stream().filter(col -> col.getFillStrategy().isSupport(SqlType.SELECT)).collect(Collectors.toList());
        for (T obj : objs) {
            post(obj, columns);
        }
        return objs;
    }

    public static <T> T post(T obj, Class<T> type) {
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(type);
        List<ColumnInfo> columns = entityInfo.getColumnMap().values()
                .stream().filter(col -> col.getFillStrategy().isSupport(SqlType.SELECT)).collect(Collectors.toList());
        return post(obj, columns);
    }

    private static <T> T post(T obj, List<ColumnInfo> columns) {
        if (!columns.isEmpty()) {
            try {
                for (ColumnInfo column : columns) {
                    Object newVal = SqlBuilderUtils.getColumnValue(column, obj, SqlType.SELECT);
                    column.getReadMethod().set(obj, newVal);
                }
            } catch (IllegalAccessException e) {
                throw new NwayException(e);
            }
        }
        return obj;
    }
}
