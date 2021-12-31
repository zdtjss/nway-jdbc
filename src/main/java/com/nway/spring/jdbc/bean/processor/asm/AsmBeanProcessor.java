package com.nway.spring.jdbc.bean.processor.asm;

import com.nway.spring.jdbc.bean.processor.BeanProcessor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class AsmBeanProcessor implements BeanProcessor {

    private static final ConcurrentMap<String, AsmRowMapper> localCache = new ConcurrentHashMap<>(256);

    private Function<ResultSet, String> sqlExtractor;

    @Override
    public <T> List<T> toBeanList(ResultSet rs, Class<T> mappedClass) throws SQLException {

        int rowNum = 0;

        RowMapper<T> mapper = getMapper(rs, mappedClass);

        final List<T> results = new ArrayList<>();
        while (rs.next()) {
            results.add(mapper.mapRow(rs, rowNum++));
        }

        return results;
    }

    @Override
    public <T> T toBean(ResultSet rs, Class<T> mappedClass) throws SQLException {

        RowMapper<T> mapper = getMapper(rs, mappedClass);
        T row = mapper.mapRow(rs, 0);
        if (rs.next()) {
            throw new IncorrectResultSizeDataAccessException("发现了多条符合条件的数据", 1);
        }
        return row;
    }

    public void setSqlExtractor(Function<ResultSet, String> extractor) {
        this.sqlExtractor = extractor;
    }

    private <T> RowMapper<T> getMapper(ResultSet rs, Class<T> mappedClass) throws SQLException {

        String cacheKey = null;
        if(sqlExtractor != null) {
            cacheKey = sqlExtractor.apply(rs);
        }

        LinkedHashMap<String, Integer> columnIndexMap = null;
        if (cacheKey == null) {
            columnIndexMap = getColumnIndex(rs);
            cacheKey = columnIndexMap.keySet().toString() + mappedClass.hashCode();
        }

        AsmRowMapper<T> mapper = localCache.get(cacheKey);
        if(mapper == null) {
            columnIndexMap = columnIndexMap == null ? getColumnIndex(rs) : columnIndexMap;
            mapper = new AsmRowMapper<>(mappedClass, columnIndexMap);
            localCache.put(cacheKey, mapper);
        }
        return mapper;
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
