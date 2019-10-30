package com.nway.spring.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nway.spring.jdbc.annotation.enums.ColumnType;
import com.nway.spring.jdbc.sql.fill.FillStrategy;
import com.nway.spring.jdbc.sql.fill.NoneFillStrategy;
import com.nway.spring.jdbc.sql.permission.NonePermissionStrategy;
import com.nway.spring.jdbc.sql.permission.PermissionStrategy;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	String value() default "";

	String name() default "";
	
	ColumnType type() default ColumnType.COMMON;
	
	Class<? extends FillStrategy> fillStrategy() default NoneFillStrategy.class;
	
	Class<? extends PermissionStrategy> permissionStrategy() default NonePermissionStrategy.class;
}
