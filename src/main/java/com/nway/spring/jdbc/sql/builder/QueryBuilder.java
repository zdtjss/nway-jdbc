package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder<T> extends SqlBuilder {

    private boolean ignorePower = false;

    private final List<String> columns = new ArrayList<>();
    private final List<String> excludeColumns = new ArrayList<>();
    private final List<String> multiValColumn = new ArrayList<>();

    public QueryBuilder(Class<T> beanClass) {
        super(beanClass);
    }

    public SqlBuilder distinct() {
        getPreWhere().append(" distinct ");
        return this;
    }

    @SafeVarargs
    public final QueryBuilder<T> withColumn(SFunction<T, ?>... fields) {
        for (SFunction<T, ?> field : fields) {
            columns.add(SqlBuilderUtils.getColumn(beanClass, field));
        }
        return this;
    }

    public QueryBuilder<T> withColumn(String... columnNames) {
        columns.addAll(Arrays.asList(columnNames));
        return this;
    }

    public QueryBuilder<T> excludeColumn(String... columnNames) {
        excludeColumns.addAll(Arrays.asList(columnNames));
        return this;
    }

    @SafeVarargs
    public final QueryBuilder<T> excludeColumn(SFunction<T, ?>... fields) {
        for (SFunction<T, ?> field : fields) {
            excludeColumns.add(SqlBuilderUtils.getColumn(beanClass, field));
        }
        return this;
    }

    @SafeVarargs
    public final QueryBuilder<T> withMVColumn(SFunction<T, ?>... fields) {
        for (SFunction<T, ?> field : fields) {
            multiValColumn.add(SqlBuilderUtils.getColumn(beanClass, field));
        }
        return this;
    }

    public QueryBuilder<T> withMVColumn(String... columnNames) {
        multiValColumn.addAll(Arrays.asList(columnNames));
        return this;
    }

    public void ignorePower() {
        this.ignorePower = true;
    }

    public List<String> getColumns() {
        return columns;
    }

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
            sql.append("select ").append(String.join(",", getColumns())).append(" from ").append(SqlBuilderUtils.getTableNameFromCache(beanClass));
        } else {
            EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
            entityInfo.getColumnList().removeAll(excludeColumns);
            sql.append("select ").append(String.join(",", entityInfo.getColumnList())).append(" from ").append(SqlBuilderUtils.getTableNameFromCache(beanClass));
        }
        return sql.toString();
    }

}
