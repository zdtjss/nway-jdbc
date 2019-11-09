package com.nway.spring.jdbc;

public class HsqldbPaginationSupport implements PaginationSupport {

	@Override
	public String buildPaginationSql(String sql, int start, int pageSize) {

		return sql + " OFFSET " + start + " FETCH " + pageSize + " ROWS ONLY ";
	}

}
