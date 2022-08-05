package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.LogicFieldStrategy;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.fill.FillStrategy;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.Optional;

public class DeleteBuilder extends SqlBuilder<DeleteBuilder> {

    public DeleteBuilder(Class<?> beanClass) {
        super(beanClass);
    }

    @Override
    protected SqlType getSqlType() {
        return SqlType.DELETE;
    }

    @Override
    public String getSql() {
        Optional<ColumnInfo> logicDelField = getLogicDelField();
        if (logicDelField.isPresent()) {
            ColumnInfo columnInfo = logicDelField.get();
            StringBuilder sql = new StringBuilder(128);
            sql.append("update ")
                    .append(SqlBuilderUtils.getTableNameFromCache(beanClass))
                    .append(" set ").append(columnInfo.getColumnName()).append(" = ").append(columnInfo.getFillStrategy().getValue(SqlType.DELETE, FillStrategy.DEFAULT_NONE))
                    .append(super.getSql());
            return sql.toString();
        }
        return "delete from " + SqlBuilderUtils.getTableNameFromCache(beanClass) + " " + super.getSql();
    }

    private Optional<ColumnInfo> getLogicDelField() {
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
        return entityInfo.getColumnMap().values().stream().filter(e -> e.getFillStrategy() instanceof LogicFieldStrategy).findFirst();
    }

}
