package com.nway.spring.jdbc.sql;

import java.io.Serializable;
import java.util.function.Supplier;

@FunctionalInterface
public interface SSupplier<T> extends Supplier<T>, Serializable {
}