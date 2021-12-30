package com.nway.spring.jdbc.pagination;

public class PageDialect {

    private String sql;
    private int firstParam;
    private int secondParam;

    public PageDialect(String sql, int firstParam, int secondParam) {
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

    public int getFirstParam() {
        return firstParam;
    }

    public void setFirstParam(int firstParam) {
        this.firstParam = firstParam;
    }

    public int getSecondParam() {
        return secondParam;
    }

    public void setSecondParam(int secondParam) {
        this.secondParam = secondParam;
    }
}
