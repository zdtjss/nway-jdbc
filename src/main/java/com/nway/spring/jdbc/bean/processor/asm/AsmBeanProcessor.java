package com.nway.spring.jdbc.bean.processor.asm;

import com.nway.spring.jdbc.bean.processor.BeanProcessor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AsmBeanProcessor implements BeanProcessor {

    private static final ConcurrentMap<String, AsmRowMapper> localCache = new ConcurrentHashMap<>(256);

    @Override
    public <T> List<T> toBeanList(ResultSet rs, Class<T> mappedClass) throws SQLException {

        int rowNum = 0;
        LinkedHashMap<String, Integer> columnIndexMap = getColumnIndex(rs);
        String cacheKey = columnIndexMap.keySet().toString() + mappedClass.hashCode();

        AsmRowMapper<T> mapper = (AsmRowMapper<T>) Optional.ofNullable(localCache.get(cacheKey))
                .orElseGet(() -> {
                    AsmRowMapper<T> rowMapper = new AsmRowMapper<>(mappedClass, columnIndexMap);
                    localCache.put(cacheKey, rowMapper);
                    return rowMapper;
                });

        final List<T> results = new ArrayList<>();
        while (rs.next()) {
            results.add(mapper.mapRow(rs, rowNum++));
        }

        return results;
    }

    @Override
    public <T> T toBean(ResultSet rs, Class<T> mappedClass) throws SQLException {

        LinkedHashMap<String, Integer> columnIndex = getColumnIndex(rs);

        String cacheKey = columnIndex.keySet().toString() + mappedClass.hashCode();

        AsmRowMapper<T> mapper = (AsmRowMapper<T>) Optional.ofNullable(localCache.get(cacheKey))
                .orElseGet(() -> {
                    AsmRowMapper<T> rowMapper = new AsmRowMapper<T>(mappedClass, columnIndex);
                    localCache.put(cacheKey, rowMapper);
                    return rowMapper;
                });

        T row = mapper.mapRow(rs, 0);
        if (rs.next()) {
            throw new IncorrectResultSizeDataAccessException("发现了多条符合条件的数据", 1);
        }
        return row;
    }

    private LinkedHashMap<String, Integer> getColumnIndex(ResultSet rs) throws SQLException {

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        LinkedHashMap<String, Integer> columnIndex = new LinkedHashMap<>(columnCount);

        for (int index = 1; index <= columnCount; index++) {
            columnIndex.put(JdbcUtils.lookupColumnName(rsmd, index), index);
        }
        return columnIndex;
    }

}
