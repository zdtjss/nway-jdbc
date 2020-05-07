package com.nway.spring.jdbc.sql.fill;

public final class NoneValue {

    private static final NoneValue instance = new NoneValue();

    private NoneValue() {

    }

    public static NoneValue getInstance() {
        return instance;
    }

}
