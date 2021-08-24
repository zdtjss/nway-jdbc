package com.nway.spring.jdbc;

import com.nway.spring.jdbc.performance.entity.Monitor;

public class AsmExample implements BeanAccess {

    private Monitor bean;

    @Override
    public <T> T newInstance() {
        this.bean = new Monitor();
        return (T) bean;
    }

    public void setVal(String fieldName, Object val) {
        if ("brand".equals(fieldName)) {
            bean.setBrand((String) val);
        } else if ("price".equals(fieldName)) {
            bean.setPrice((float) val);
        } else if ("price".equals(fieldName)) {
            bean.setPrice((float) val);
        } else {
            throw new IllegalArgumentException("属性" + fieldName + "不存在");
        }
    }

}
