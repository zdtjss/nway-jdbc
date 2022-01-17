package com.nway.spring.jdbc.sql.permission;

public class WhereCondition {

    private Object value;
    private String expr;

    public WhereCondition() {
    }

    public WhereCondition(String expr, Object value) {
        this.value = value;
        this.expr = expr;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }
}
