package com.nway.spring.jdbc.bean.processor;

import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultBeanProcessor implements BeanProcessor {

    private static final ConcurrentMap<Class, DefaultRowMapper> localCache = new ConcurrentHashMap<>(256);

    @Override
    public <T> List<T> toBeanList(ResultSet rs, Class<T> mappedClass) throws SQLException {

        int rowNum = 0;
        Map<String, Integer> columnIndex = getColumnIndex(rs);

        DefaultRowMapper<T> mapper = Optional.ofNullable(localCache.get(mappedClass))
                .orElseGet(() -> {
                    DefaultRowMapper<T> rowMapper = new DefaultRowMapper<>(mappedClass);
                    localCache.put(mappedClass, rowMapper);
                    return rowMapper;
                }).setColumnIndexMap(columnIndex);

        final List<T> results = new ArrayList<>();
        while (rs.next()) {
            results.add(mapper.mapRow(rs, rowNum++));
        }
        return results;
    }

    @Override
    public <T> T toBean(ResultSet rs, Class<T> mappedClass) throws SQLException {

        Map<String, Integer> columnIndex = getColumnIndex(rs);

        DefaultRowMapper<T> mapper = Optional.ofNullable(localCache.get(mappedClass))
                .orElseGet(() -> {
                    DefaultRowMapper<T> rowMapper = new DefaultRowMapper<>(mappedClass);
                    localCache.put(mappedClass, rowMapper);
                    return rowMapper;
                }).setColumnIndexMap(columnIndex);

        return mapper.mapRow(rs, 0);
    }

    private Map<String, Integer> getColumnIndex(ResultSet rs) throws SQLException {

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Map<String, Integer> columnIndex = new HashMap<>(columnCount);

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            columnIndex.put(column, index);
        }
        return columnIndex;
    }


}
