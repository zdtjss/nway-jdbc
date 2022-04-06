package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
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

    public QueryBuilder withMVColumn(String... columnNames) {
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

    public QueryBuilder groupBy(String... column) {
        afterWhere.append(" group by ").append(String.join(",", column));
        return this;
    }

    @SafeVarargs
    public final <T, R> QueryBuilder groupBy(SFunction<T, R>... columns) {
        afterWhere.append(" group by ");
        for (SFunction<T, R> column : columns) {
            afterWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(",");
        }
        afterWhere.deleteCharAt(afterWhere.length() - 1);
        return this;
    }

    @SafeVarargs
    public final <T, R> QueryBuilder orderBy(SFunction<T, R>... columns) {
        afterWhere.append(" order by ");
        for (SFunction<T, R> column : columns) {
            afterWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(",");
        }
        afterWhere.deleteCharAt(afterWhere.length() - 1);
        return this;
    }

    public QueryBuilder orderBy(String... columns) {
        afterWhere.append(" order by ");
        for (String column : columns) {
            afterWhere.append(column).append(",");
        }
        afterWhere.deleteCharAt(afterWhere.length() - 1);
        return this;
    }

    public <T, R> QueryBuilder andOrderByAsc(SFunction<T, R> column) {
        afterWhere.append(",").append(SqlBuilderUtils.getColumn(beanClass, column)).append(" asc");
        return this;
    }

    public QueryBuilder andOrderByAsc(String... columns) {
        for (String column : columns) {
            afterWhere.append(",").append(column).append(" asc");
        }
        return this;
    }

    @SafeVarargs
    public final <T, R> QueryBuilder orderByDesc(SFunction<T, R>... columns) {
        afterWhere.append(" order by ");
        for (SFunction<T, R> column : columns) {
            afterWhere.append(SqlBuilderUtils.getColumn(beanClass, column)).append(" desc,");
        }
        afterWhere.deleteCharAt(afterWhere.length() - 1);
        return this;
    }

    public QueryBuilder orderByDesc(String... columns) {
        afterWhere.append(" order by ");
        for (String column : columns) {
            afterWhere.append(column).append(" desc,");
        }
        afterWhere.deleteCharAt(afterWhere.length() - 1);
        return this;
    }

    public <T, R> QueryBuilder andOrderByDesc(SFunction<T, R> column) {
        afterWhere.append(",").append(SqlBuilderUtils.getColumn(beanClass, column)).append(" desc");
        return this;
    }

    public QueryBuilder andOrderByDesc(String... columns) {
        for (String column : columns) {
            afterWhere.append(",").append(column).append(" desc");
        }
        return this;
    }

    public QueryBuilder having(Consumer<QueryBuilder> whereBuilder) {
        QueryBuilder lq = new QueryBuilder(beanClass);
        whereBuilder.accept(lq);
        StringBuilder sql = lq.getWhere();
        // where 的长度
        if (sql.length() > 7) {
            afterWhere.append(" having ").append(lq.getWhere().substring(7));
            param.addAll(lq.getParam());
        }
        return thisObj;
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
