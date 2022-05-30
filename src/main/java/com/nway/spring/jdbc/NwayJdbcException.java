package com.nway.spring.jdbc;

public class NwayJdbcException extends RuntimeException {

    public NwayJdbcException() {
        super();
    }

    public NwayJdbcException(Throwable cause) {
        super(cause);
    }

    public NwayJdbcException(String message, Throwable cause) {
        super(message, cause);
    }
}
