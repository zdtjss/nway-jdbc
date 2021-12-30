package com.nway.spring.jdbc.pagination;

public class HsqldbPaginationSupport implements PaginationSupport {

    @Override
    public PageDialect buildPaginationSql(String sql, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return new PageDialect(sql + " OFFSET ? FETCH ? ROWS ONLY ", offset, pageSize);
    }

}
