package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.fill.NoneFillStrategy;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.function.SSupplier;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;
import com.nway.spring.jdbc.sql.permission.NonePermissionStrategy;
import com.nway.spring.jdbc.sql.permission.WhereCondition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BatchUpdateByIdBuilder implements ISqlBuilder {

    protected List<?> data;
    protected List<Object> param = new ArrayList<>();
    private List<String> columnNameList = new ArrayList<>();
    protected Class beanClass;
    private String tableName;

    public BatchUpdateByIdBuilder(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public BatchUpdateByIdBuilder use(List<?> data) {
        this.data = data;
        List<List<Object>> batchParam = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            batchParam.add(new ArrayList<>());
        }
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
        List<String> columnList = getColumnNameList(entityInfo);
        for (ColumnInfo columnInfo : entityInfo.getColumnMap().values()) {
            if (!NoneFillStrategy.class.equals(columnInfo.getFillStrategy().getClass())
                    && columnInfo.getFillStrategy().isSupport(SqlType.UPDATE)) {
                if(!columnList.contains(columnInfo.getColumnName())) {
                    columnList.add(columnInfo.getColumnName());
                }
            }
        }

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

    public BatchUpdateByIdBuilder columns(String... columns) {
        columnNameList.addAll(Arrays.asList(columns));
        return this;
    }

    @SafeVarargs
    public final <T> BatchUpdateByIdBuilder columns(SSupplier<T>... columns) {
        for (SSupplier<T> column : columns) {
            columnNameList.add(SqlBuilderUtils.getColumn(this.beanClass, column));
        }
        return this;
    }

    @SafeVarargs
    public final <T, R> BatchUpdateByIdBuilder columns(SFunction<T, R>... columns) {
        for (SFunction<T, R> column : columns) {
            columnNameList.add(SqlBuilderUtils.getColumn(this.beanClass, column));
        }
        return this;
    }

    @Override
    public String getSql() {

        StringBuilder sql = new StringBuilder();

        sql.append("update ").append(getTableName()).append(" set ");
        int initLength = sql.length();

        List<String> columnList = getColumnNameList(SqlBuilderUtils.getEntityInfo(beanClass));
        for (String col : columnList) {
            sql.append(col).append(" = ?,");
        }
        if (sql.length() > initLength) {
            sql.deleteCharAt(sql.length() - 1);
        }

        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
        sql.append(" where ").append(entityInfo.getId().getColumnName()).append(" = ?");

        for (int i = 0; i < this.param.size(); i++) {
            Object idVal = SqlBuilderUtils.getColumnValue(entityInfo.getId(), this.data.get(i), SqlType.UPDATE);
            if (idVal == null) {
                throw new SqlBuilderException("更新失败，批量更新时存在主键为空的数据。");
            }
            ((List) this.param.get(i)).add(idVal);
        }

        for (ColumnInfo columnInfo : entityInfo.getColumnMap().values()) {
            if (columnInfo.getPermissionStrategy().getClass() == NonePermissionStrategy.class) {
                continue;
            }
            WhereCondition whereCondition = SqlBuilderUtils.getWhereCondition(SqlType.UPDATE, columnInfo);
            if (whereCondition != null && whereCondition.getExpr().length() > 0) {
                sql.append(" and ").append(whereCondition.getExpr()).append(' ');
                for (int i = 0; i < this.param.size(); i++) {
                    if (whereCondition.getValue() instanceof Collection) {
                        ((List) this.param.get(i)).addAll((Collection) whereCondition.getValue());
                    } else if (ObjectUtils.isArray(whereCondition.getValue())) {
                        ((List) this.param.get(i)).addAll(CollectionUtils.arrayToList(whereCondition.getValue()));
                    } else {
                        ((List) this.param.get(i)).add(whereCondition.getValue());
                    }
                }
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

    public String getTableName() {
        return tableName == null ? SqlBuilderUtils.getTableNameFromCache(beanClass) : tableName;
    }

    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
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
}
