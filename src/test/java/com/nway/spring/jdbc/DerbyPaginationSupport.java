package com.nway.spring.jdbc;

import com.nway.spring.jdbc.pagination.PageDialect;
import com.nway.spring.jdbc.pagination.PaginationSupport;

public class DerbyPaginationSupport implements PaginationSupport {

    @Override
    public PageDialect buildPaginationSql(String sql, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return new PageDialect(sql + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ", offset, pageSize);
    }

}
