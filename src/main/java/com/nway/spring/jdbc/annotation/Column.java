package com.nway.spring.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nway.spring.jdbc.annotation.enums.ColumnFillType;
import com.nway.spring.jdbc.annotation.enums.ColumnType;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	String value() default "";

	String name() default "";
	
	ColumnType type() default ColumnType.COMMON;
	
	ColumnFillType fillType() default ColumnFillType.NONE;
}
