package com.nway.spring.jdbc.bean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AsmBeanProcessor implements BeanProcessor {

    @Override
    public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
        return null;
    }

    @Override
    public <T> List<T> toBeanList(ResultSet rs, Class<T> type) throws SQLException {
        return null;
    }
}
