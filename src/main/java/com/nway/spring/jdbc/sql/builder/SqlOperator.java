package com.nway.spring.jdbc.sql.builder;

public enum SqlOperator {

    EQ("="),
    GE(">="),
    LE("<="),
    GT(">"),
    LT("<"),
    IS_NULL("is null"),
    IS_NOT_NULL("is not null");

    private String operator;

    SqlOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
