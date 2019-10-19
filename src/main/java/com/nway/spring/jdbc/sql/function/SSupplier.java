package com.nway.spring.jdbc.sql.function;

import java.io.Serializable;
import java.util.function.Supplier;

@FunctionalInterface
public interface SSupplier<T> extends Supplier<T>, Serializable {
}