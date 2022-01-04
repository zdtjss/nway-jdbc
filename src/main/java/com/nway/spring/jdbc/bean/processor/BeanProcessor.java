package com.nway.spring.jdbc.bean.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface BeanProcessor {

    /**
     * 查询到多条数据时抛出 IncorrectResultSizeDataAccessException
     *
     * @param rs
     * @param type
     * @param <T>
     * @return
     * @throws SQLException
     */
    <T> T toBean(ResultSet rs, Class<T> type) throws SQLException;

    <T> List<T> toBeanList(ResultSet rs, Class<T> type) throws SQLException;
}
