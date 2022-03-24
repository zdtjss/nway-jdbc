package com.nway.spring.jdbc.sql.builder;

public enum SqlOperator {

    EQ("="),
    GE(">="),
    LE("<="),
    GT(">"),
    LT("<");

    private String operator;

    private SqlOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
