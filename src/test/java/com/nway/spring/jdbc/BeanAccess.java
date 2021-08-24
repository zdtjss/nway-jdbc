package com.nway.spring.jdbc;

public interface BeanAccess {

    <T> T newInstance();

    void setVal(String fieldName, Object val);

}
