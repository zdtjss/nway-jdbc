package com.nway.spring.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiColumn {

    /**
     * 子表名
     *
     * @return
     */
    String table() default "";

    /**
     * 子表主键字段名，主键数据类型暂仅支持数值类型
     *
     * @return
     */
    String key() default "id";

    /**
     * 子表与主表的关系字段名，外键名
     *
     * @return
     */
    String fk() default "fk";

    /**
     * 标识顺序的字段名
     *
     * @return
     */
    String idx() default "idx";
}
