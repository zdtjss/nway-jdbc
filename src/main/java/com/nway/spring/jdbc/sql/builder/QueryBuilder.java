package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder extends SqlBuilder<QueryBuilder> implements MultiValQueryBuilder {

    private boolean ignorePower = false;
    private String distinct = "";
    private final List<String> columns = new ArrayList<>();
    private final List<String> excludeColumns = new ArrayList<>();
    private final List<String> multiValColumn = new ArrayList<>();

    public QueryBuilder(Class<?> beanClass) {
        super(beanClass);
    }

    public QueryBuilder distinct() {
        this.distinct = " distinct ";
        return this;
    }

    @SafeVarargs
    public final <T> QueryBuilder withColumn(SFunction<T, ?>... fields) {
        for (SFunction<T, ?> field : fields) {
            columns.add(SqlBuilderUtils.getColumn(beanClass, field));
        }
        return this;
    }

    public QueryBuilder withColumn(String... columnNames) {
        columns.addAll(Arrays.asList(columnNames));
        return this;
    }

    public QueryBuilder excludeColumn(String... columnNames) {
        excludeColumns.addAll(Arrays.asList(columnNames));
        return this;
    }

    @SafeVarargs
    public final <T> QueryBuilder excludeColumn(SFunction<T, ?>... fields) {
        for (SFunction<T, ?> field : fields) {
            excludeColumns.add(SqlBuilderUtils.getColumn(beanClass, field));
        }
        return this;
    }

    @SafeVarargs
    public final <T> QueryBuilder withMVColumn(SFunction<T, ?>... fields) {
        for (SFunction<T, ?> field : fields) {
            multiValColumn.add(SqlBuilderUtils.getColumn(beanClass, field));
        }
        return this;
    }

    public <T> QueryBuilder withMVColumn(String... columnNames) {
        multiValColumn.addAll(Arrays.asList(columnNames));
        return this;
    }

    public void ignorePermission() {
        this.ignorePower = true;
    }

    public List<String> getColumns() {
        return columns;
    }

    @Override
    public List<String> getMultiValColumn() {
        return multiValColumn;
    }

    @Override
    public String getSql() {
        if (!this.ignorePower) {
            initPermission();
        }
        return getSelectStmt() + super.getSql();
    }

    private String getSelectStmt() {
        StringBuilder sql = new StringBuilder(64);
        if (getColumns().size() > 0) {
            sql.append("select ").append(this.distinct).append(String.join(",", getColumns())).append(" from ").append(SqlBuilderUtils.getTableNameFromCache(beanClass));
        } else {
            EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
            String columnList = entityInfo.getColumnList().stream().filter(column -> !excludeColumns.contains(column)).collect(Collectors.joining(","));
            sql.append("select ").append(this.distinct).append(columnList).append(" from ").append(SqlBuilderUtils.getTableNameFromCache(beanClass));
        }
        return sql.toString();
    }

}
