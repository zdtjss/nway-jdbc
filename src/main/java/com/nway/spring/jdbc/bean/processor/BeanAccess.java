package com.nway.spring.jdbc.bean.processor;

public interface BeanAccess {

    <T> T newInstance();

    void setVal(String fieldName, Object val);

}
