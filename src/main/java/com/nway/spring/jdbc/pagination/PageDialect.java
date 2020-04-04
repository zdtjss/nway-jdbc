package com.nway.spring.jdbc.pagination;

public class PageDialect {

    private String sql;
    private long firstParam;
    private long secondParam;

    public PageDialect(String sql, long firstParam, long secondParam) {
        this.sql = sql;
        this.firstParam = firstParam;
        this.secondParam = secondParam;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public long getFirstParam() {
        return firstParam;
    }

    public void setFirstParam(long firstParam) {
        this.firstParam = firstParam;
    }

    public long getSecondParam() {
        return secondParam;
    }

    public void setSecondParam(long secondParam) {
        this.secondParam = secondParam;
    }
}
