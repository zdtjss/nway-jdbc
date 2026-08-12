package com.nway.spring.jdbc.bean.processor.asm;

import com.nway.spring.jdbc.bean.processor.BeanProcessor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class AsmBeanProcessor implements BeanProcessor {

    private static final ConcurrentMap<String, AsmRowMapper<?>> localCache = new ConcurrentHashMap<>(256);

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
            throw new IncorrectResultSizeDataAccessException("查询到多条符合条件的数据", 1);
        }
        return row;
    }

    public void setSqlExtractor(Function<ResultSet, String> extractor) {
        this.sqlExtractor = extractor;
    }

    @SuppressWarnings("unchecked")
    private <T> RowMapper<T> getMapper(ResultSet rs, Class<T> mappedClass) throws SQLException {

        String cacheKey = null;
        if (sqlExtractor != null) {
            cacheKey = sqlExtractor.apply(rs);
        }

        LinkedHashMap<String, Integer> columnIndexMap = null;
        if (cacheKey == null) {
            columnIndexMap = getColumnIndex(rs);
            cacheKey = buildCacheKey(mappedClass, columnIndexMap);
        }

        // Fast path: cache hit (no locking, no allocation)
        AsmRowMapper<T> mapper = (AsmRowMapper<T>) localCache.get(cacheKey);
        if (mapper != null) {
            return mapper;
        }

        // Slow path: generate ASM mapper. Use putIfAbsent to avoid blocking other buckets
        // during bytecode generation. Accept rare duplicate generation at startup over
        // holding a bucket lock for ~5ms during ASM class creation.
        if (columnIndexMap == null) {
            columnIndexMap = getColumnIndex(rs);
        }
        AsmRowMapper<T> newMapper = new AsmRowMapper<>(mappedClass, columnIndexMap);
        AsmRowMapper<T> existing = (AsmRowMapper<T>) localCache.putIfAbsent(cacheKey, newMapper);
        return existing != null ? existing : newMapper;
    }

    /**
     * Build cache key using class name + sorted column names.
     * Uses Arrays.sort + StringBuilder to avoid Stream object allocation.
     */
    private String buildCacheKey(Class<?> clazz, LinkedHashMap<String, Integer> columnIndexMap) {
        String[] keys = columnIndexMap.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuilder sb = new StringBuilder(clazz.getName().length() + keys.length * 16);
        sb.append(clazz.getName());
        for (String key : keys) {
            sb.append('|').append(key);
        }
        return sb.toString();
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
