package com.nway.spring.jdbc.bean.processor.asm;

import com.nway.spring.jdbc.bean.processor.BeanProcessor;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AsmBeanProcessor implements BeanProcessor {

    private static final ConcurrentMap<Class, AsmRowMapper> localCache = new ConcurrentHashMap<>(256);

    @Override
    public <T> List<T> toBeanList(ResultSet rs, Class<T> mappedClass) throws SQLException {

//        long begin = System.currentTimeMillis();

        int rowNum = 0;
        Map<String, Integer> columnIndex = getColumnIndex(rs);

        AsmRowMapper<T> mapper = (AsmRowMapper<T>) Optional.ofNullable(localCache.get(mappedClass))
                .orElseGet(() -> {
                    AsmRowMapper<T> rowMapper = new AsmRowMapper<>(mappedClass);
                    localCache.put(mappedClass, rowMapper);
                    return rowMapper;
                }).setColumnIndexMap(columnIndex);

        final List<T> results = new ArrayList<>();
        while (rs.next()) {
            results.add(mapper.mapRow(rs, rowNum++));
        }

//        System.out.printf("%d\t%s%n", System.currentTimeMillis() - begin, Thread.currentThread().getName());

        return results;
    }

    @Override
    public <T> T toBean(ResultSet rs, Class<T> mappedClass) throws SQLException {

        Map<String, Integer> columnIndex = getColumnIndex(rs);

        AsmRowMapper<T> mapper = (AsmRowMapper<T>) Optional.ofNullable(localCache.get(mappedClass))
                .orElseGet(() -> {
                    AsmRowMapper<T> rowMapper = new AsmRowMapper<>(mappedClass);
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
