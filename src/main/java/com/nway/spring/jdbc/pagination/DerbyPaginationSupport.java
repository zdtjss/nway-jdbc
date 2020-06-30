package com.nway.spring.jdbc.pagination;

public class DerbyPaginationSupport implements PaginationSupport {

    @Override
    public PageDialect buildPaginationSql(String sql, int page, int pageSize) {
        long offset = (page - 1) * pageSize;
        return new PageDialect(sql + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ", offset, pageSize);
    }

}
