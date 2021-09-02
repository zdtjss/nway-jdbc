package com.nway.spring.jdbc;

import com.nway.spring.jdbc.bean.processor.BeanAccess;
import com.nway.spring.jdbc.performance.entity.Monitor;

public class MonitorAccess implements BeanAccess {

    private Monitor bean;

    public <T> T newInstance() {
        this.bean = new Monitor();
        return (T) this.bean;
    }

    public void setVal(String fieldName, Object val) {
        if ("id".equals(fieldName)) {
            this.bean.setId((Integer) val);
        } else if ("type".equals(fieldName)) {
            this.bean.setType((int) val);
        } else if ("photo".equals(fieldName)) {
            this.bean.setPhoto((byte[]) val);
        }

    }

}
