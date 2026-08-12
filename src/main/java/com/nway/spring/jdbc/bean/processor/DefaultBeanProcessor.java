package com.nway.spring.jdbc.bean.processor;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultBeanProcessor implements BeanProcessor {

    private static final ConcurrentMap<String, DefaultRowMapper<?>> localCache = new ConcurrentHashMap<>(256);

    @Override
    public <T> List<T> toBeanList(ResultSet rs, Class<T> mappedClass) throws SQLException {

        int rowNum = 0;
        Map<String, Integer> columnIndex = getColumnIndex(rs);

        @SuppressWarnings("unchecked")
        DefaultRowMapper<T> mapper = (DefaultRowMapper<T>) localCache.computeIfAbsent(
                buildCacheKey(mappedClass, columnIndex),
                k -> new DefaultRowMapper<>(mappedClass, columnIndex));

        final List<T> results = new ArrayList<>();
        while (rs.next()) {
            results.add(mapper.mapRow(rs, rowNum++));
        }
        return results;
    }

    @Override
    public <T> T toBean(ResultSet rs, Class<T> mappedClass) throws SQLException {

        Map<String, Integer> columnIndex = getColumnIndex(rs);

        @SuppressWarnings("unchecked")
        DefaultRowMapper<T> mapper = (DefaultRowMapper<T>) localCache.computeIfAbsent(
                buildCacheKey(mappedClass, columnIndex),
                k -> new DefaultRowMapper<>(mappedClass, columnIndex));

        T row = mapper.mapRow(rs, 0);
        if (rs.next()) {
            throw new IncorrectResultSizeDataAccessException("查询到多条符合条件的数据", 1);
        }
        return row;
    }

    /**
     * Build a cache key that includes class name and column name + position info,
     * ensuring different column orderings produce different cache keys.
     */
    private String buildCacheKey(Class<?> clazz, Map<String, Integer> columnIndex) {
        StringBuilder sb = new StringBuilder(clazz.getName());
        // Include column names with their positions to distinguish different orderings
        columnIndex.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> sb.append('|').append(e.getKey()).append(':').append(e.getValue()));
        return sb.toString();
    }

    private Map<String, Integer> getColumnIndex(ResultSet rs) throws SQLException {

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Map<String, Integer> columnIndex = new LinkedHashMap<>(columnCount);

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            columnIndex.put(column, index);
        }
        return columnIndex;
    }

}
