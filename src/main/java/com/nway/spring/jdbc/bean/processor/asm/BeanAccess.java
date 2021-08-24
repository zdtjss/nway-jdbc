package com.nway.spring.jdbc.bean.processor.asm;

public interface BeanAccess {

    <T> T newInstance(String className);

    void setVal(String fieldName, Object val);

}
