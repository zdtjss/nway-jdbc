package com.nway.spring.jdbc.bean.processor.asm;

public interface BeanAccess<T> {

    T newInstance();

    void setVal(String fieldName, Object val);

}
