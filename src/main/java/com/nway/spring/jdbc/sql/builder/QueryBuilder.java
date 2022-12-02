package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.meta.EntityInfo;
import com.nway.spring.jdbc.sql.meta.MultiValueColumnInfo;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryBuilder extends SqlBuilder<QueryBuilder> implements MultiValQueryBuilder {

    private String distinct = "";
    private final List<String> columns = new ArrayList<>();
    private final List<String> excludeColumns = new ArrayList<>();
    private final List<String> multiValColumn = new ArrayList<>();
    private final Map<String, Collection<?>> selectMultiParamMap = new LinkedHashMap<>();

    public QueryBuilder(Class<?> beanClass) {
        super(beanClass);
    }

    @Override
    protected SqlType getSqlType() {
        return SqlType.SELECT;
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

    public <T> QueryBuilder mvIn(SFunction<T, ?> field, Collection<?> params) {
        String column = SqlBuilderUtils.getColumn(beanClass, field);
        selectMultiParamMap.put(column, params);
        return this;
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
        return getSelectStmt() + super.getSql();
    }

    private String getSelectStmt() {
        StringBuilder sql = new StringBuilder(64);
        if (getColumns().size() > 0) {
            sql.append("select ") .append(this.distinct).append(String.join(",", getColumns()))
                    .append(" from ").append(SqlBuilderUtils.getTableNameFromCache(beanClass));
        } else {
            EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
            List<String> columnList = entityInfo.getColumnList();
            String columnStr = columnList.stream()
                    .filter(column -> !excludeColumns.contains(column))
                    .collect(Collectors.joining(","));
            sql.append("select ").append(this.distinct).append(columnStr)
                    .append(" from ").append(SqlBuilderUtils.getTableNameFromCache(beanClass));
        }

        if (!selectMultiParamMap.isEmpty()) {
            List<MultiValueColumnInfo> multiValueList = SqlBuilderUtils.getEntityInfo(getBeanClass()).getMultiValue();
            Map<String, MultiValueColumnInfo> mvColMap = multiValueList.stream().collect(Collectors.toMap(MultiValueColumnInfo::getColumnName, Function.identity()));

            int current = 0;
            for (Map.Entry<String, Collection<?>> entry : selectMultiParamMap.entrySet()) {
                int next = current + 1;
                String curAlias = "t" + current;
                String nextAlias = "t" + next;
                MultiValueColumnInfo multiValueColumnInfo = mvColMap.get(entry.getKey());
                sql.append(" ").append(curAlias).append(" left join ")
                        .append(multiValueColumnInfo.getTable()).append(" ").append(nextAlias).append(" on ").append(curAlias).append('.').append(SqlBuilderUtils.getIdName(getBeanClass())).append(" = ").append(nextAlias).append('.').append(multiValueColumnInfo.getFk());
                super.in(nextAlias + "." + entry.getKey(), entry.getValue());
            }
        }

        return sql.toString();
    }

}
