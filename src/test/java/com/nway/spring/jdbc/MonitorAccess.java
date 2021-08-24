package com.nway.spring.jdbc;

import com.nway.spring.jdbc.bean.processor.asm.BeanAccess;
import com.nway.spring.jdbc.performance.entity.Monitor;

import java.util.Date;

public class MonitorAccess implements BeanAccess<Monitor> {

    private Monitor bean;

    public MonitorAccess() {
    }

    public Monitor newInstance() {
        this.bean = new Monitor();
        return this.bean;
    }

    public void setVal(String fieldName, Object val) {
        if ("id".equals(fieldName)) {
            this.bean.setId((Integer) val);
        } else if ("brand".equals(fieldName)) {
            this.bean.setBrand((String) val);
        } else if ("model".equals(fieldName)) {
            this.bean.setModel((String) val);
        } else if ("price".equals(fieldName)) {
            this.bean.setPrice((Float) val);
        } else if ("type".equals(fieldName)) {
            this.bean.setType((Integer) val);
        } else if ("maxResolution".equals(fieldName)) {
            this.bean.setMaxResolution((String) val);
        }
    }

}
