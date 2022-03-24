package com.nway.spring.jdbc.sql.builder;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.fill.NoneFillStrategy;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.function.SSupplier;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;

import java.util.ArrayList;
import java.util.List;

public class UpdateBuilder extends SqlBuilder {

    private final List<String> sets = new ArrayList<>();

    public UpdateBuilder(Class<?> beanClass) {
        super(beanClass);
        initFilled();
    }

    public <T> UpdateBuilder set(SSupplier<T> val) {
        if(isInvalid(val.get())) {
            return this;
        }
        sets.add(SqlBuilderUtils.getColumn(beanClass, val) + " = ?");
        param.add(val.get());
        return this;
    }

    public <T, R> UpdateBuilder set(SFunction<T, R> column, Object val) {
        if(isInvalid(val)) {
            return this;
        }
        sets.add(SqlBuilderUtils.getColumn(beanClass, column) + " = ?");
        param.add(val);
        return this;
    }

    @Override
    public String getSql() {
        return "update " + SqlBuilderUtils.getTableNameFromCache(beanClass) + " set " + String.join(",", sets) + super.getSql();
    }

    private void initFilled() {
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(beanClass);
        for (ColumnInfo columnInfo : entityInfo.getColumnMap().values()) {
            if (!NoneFillStrategy.class.equals(columnInfo.getFillStrategy().getClass())
                    && columnInfo.getFillStrategy().isSupport(SqlType.UPDATE)) {
                Object value = columnInfo.getFillStrategy().getValue(SqlType.UPDATE);
                sets.add(columnInfo.getColumnName() + " = ?");
                param.add(value);
            }
        }
    }

}
