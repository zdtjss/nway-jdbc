package com.nway.spring.jdbc.sql.permission;

public class WhereCondition {

    private String column;
    private Object value;
    private String expr;

    public WhereCondition() {
    }

    public WhereCondition(String column, Object value, String expr) {
        this.column = column;
        this.value = value;
        this.expr = expr;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
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
