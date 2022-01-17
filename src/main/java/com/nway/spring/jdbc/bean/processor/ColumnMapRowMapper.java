package com.nway.spring.jdbc.bean.processor;

import com.nway.spring.jdbc.sql.SqlBuilderUtils;

import java.util.function.Supplier;

public class ColumnMapRowMapper extends org.springframework.jdbc.core.ColumnMapRowMapper {

    private Supplier<String> supplier;

    public ColumnMapRowMapper() {

    }

    public ColumnMapRowMapper(Supplier<String> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected String getColumnKey(String columnName) {
        return supplier == null ? SqlBuilderUtils.columnToFieldName(columnName) : supplier.get();
    }


}
