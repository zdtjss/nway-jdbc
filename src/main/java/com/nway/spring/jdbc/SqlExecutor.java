/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nway.spring.jdbc;

import com.nway.spring.jdbc.bean.BeanHandler;
import com.nway.spring.jdbc.bean.BeanListHandler;
import com.nway.spring.jdbc.bean.processor.BeanProcessor;
import com.nway.spring.jdbc.bean.processor.ColumnMapRowMapper;
import com.nway.spring.jdbc.bean.processor.DefaultBeanProcessor;
import com.nway.spring.jdbc.pagination.*;
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.SqlType;
import com.nway.spring.jdbc.sql.builder.*;
import com.nway.spring.jdbc.sql.fill.incrementer.IdWorker;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 注意：
 * <p>
 * &nbsp;&nbsp;查询不到数据时：
 * <ul>
 * <li>queryBean返回null；</li>
 * <li>queryBeanList返回值size() == 0；</li>
 * <li>queryBeanPage返回值getTotal() == 0；</li>
 * <li>queryMapPage返回值getTotal() == 0；</li>
 * </ul>
 *
 * @author zdtjss@163.com
 * @since 2014-03-28
 */
public class SqlExecutor implements InitializingBean {

    private final Log logger = LogFactory.getLog(getClass());
    private final boolean isDebugEnabled = logger.isDebugEnabled();
    private PaginationSupport paginationSupport;
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;
    private BeanProcessor beanProcessor;

    private static final ColumnMapRowMapper COLUMN_MAP_ROW_MAPPER = new ColumnMapRowMapper();

    public SqlExecutor() {
    }

    public SqlExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @param sqlBuilder sqlBuilder
     * @return
     */
    public int update(ISqlBuilder sqlBuilder) {
        String sql = sqlBuilder.getSql();
        Object[] params = sqlBuilder.getParam().toArray();
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(params));
        }
        return jdbcTemplate.update(sql, params);
    }

    public int updateById(Object obj) {
        return updateById(obj, new String[0]);
    }

    /**
     * @param obj bean
     * @return
     */
    @SafeVarargs
    public final <T, R> int updateById(Object obj, SFunction<T, R>... columns) {
        Class<?> beanClass = obj.getClass();
        String[] columnArr = Arrays.stream(columns)
                .map(column -> SqlBuilderUtils.getColumn(beanClass, column)).toArray(String[]::new);
        return updateById(obj, columnArr);
    }

    /**
     * @param obj bean
     * @return
     */
    private int updateById(Object obj, String... columns) {
        Class<?> beanClass = obj.getClass();
        UpdateBeanBuilder sqlBuilder = new UpdateBeanBuilder(obj).columns(columns);
        sqlBuilder.eq(SqlBuilderUtils.getIdName(beanClass), SqlBuilderUtils.getIdValue(beanClass, obj));
        int count = update(sqlBuilder);
        saveMultiValue(beanClass, Collections.singletonList(obj), true);
        return count;
    }

    /**
     * 注意：此方法将更新所有字段
     *
     * @param objs
     * @param <T>
     * @return
     */
    public <T> int batchUpdateById(List<?> objs) {
        return batchUpdateById(objs, new String[0]);
    }

    @SafeVarargs
    public final <T, R> int batchUpdateById(List<?> objs, SFunction<T, R>... columns) {
        Class<?> beanClass = objs.get(0).getClass();
        String[] columnArr = Arrays.stream(columns)
                .map(column -> SqlBuilderUtils.getColumn(beanClass, column)).toArray(String[]::new);
        return batchUpdateById(objs, columnArr);
    }

    /**
     * @param objs beans
     * @return
     */
    public int batchUpdateById(List<?> objs, String... columns) {
        if (objs == null || objs.size() == 0) {
            return 0;
        }
        Class<?> beanClass = objs.get(0).getClass();
        ISqlBuilder sqlBuilder = new BatchUpdateByIdBuilder(beanClass).columns(columns).use(objs);
        String sql = sqlBuilder.getSql();
        List params = sqlBuilder.getParam();
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(params.toArray()));
        }
        saveMultiValue(beanClass, objs, true);
        int[] effect = jdbcTemplate.batchUpdate(sql, params, params.size() == 0 ? new int[0] : getSqlType((Object[]) params.get(0)));
        return (int) Arrays.stream(effect).filter(c -> c > 0).count();
    }

    /**
     * 请谨慎使用本方法，因为where条件约束的数据可能是一个范围，此范围可能包含非预期数据。
     *
     * @param sqlBuilder
     * @return
     */
    public int batchUpdate(ISqlBuilder sqlBuilder) {
        String sql = sqlBuilder.getSql();
        List params = sqlBuilder.getParam();
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(params.toArray()));
        }
        if (sqlBuilder instanceof BatchUpdateBuilder) {
            saveMultiValue(sqlBuilder.getBeanClass(), ((BatchUpdateBuilder) sqlBuilder).getData(), true);
        }
        int[] effect = jdbcTemplate.batchUpdate(sql, params, params.size() == 0 ? new int[0] : getSqlType((Object[]) params.get(0)));
        return (int) Arrays.stream(effect).filter(c -> c > 0).count();
    }

    /**
     * @param id
     * @param beanClass
     * @return
     */
    public int deleteById(Serializable id, Class<?> beanClass) {
        DeleteBuilder sqlBuilder = new DeleteBuilder(beanClass);
        sqlBuilder.eq(SqlBuilderUtils.getIdName(beanClass), id);
        return update(sqlBuilder);
    }

    /**
     * @param sqlBuilder sqlBuilder
     * @return
     */
    public int delete(ISqlBuilder sqlBuilder) {
        return update(sqlBuilder);
    }

    public int batchDeleteById(Collection<? extends Serializable> ids, Class<?> beanClass) {
        DeleteBuilder sqlBuilder = new DeleteBuilder(beanClass);
        sqlBuilder.in(SqlBuilderUtils.getIdName(beanClass), ids);
        return update(sqlBuilder);
    }

    public int insert(Object obj) {
        Class<?> beanClass = obj.getClass();
        int count = update(SQL.insert(beanClass).use(obj));
        saveMultiValue(beanClass, Collections.singletonList(obj), false);
        return count;
    }

    /*public <T> T insertAndGetKey(Object obj) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        InsertBuilder sqlBuilder = SQL.insert(obj.getClass()).use(obj);
        String sql = sqlBuilder.getSql();
        Object[] params = sqlBuilder.getParam().toArray(new Object[0]);
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(params));
        }
        PreparedStatementCreator psc = conn -> {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    StatementCreatorUtils.setParameterValue(pstmt, i, StatementCreatorUtils.javaTypeToSqlParameterType(params[i].getClass()), params[i]);
                }
            }
            return pstmt;
        };
        Object key = sqlBuilder.getKeyValue();
        int count = jdbcTemplate.update(psc, keyHolder);
        return count == 0 ? (T) (key != null ? key : keyHolder.getKey()) : null;
    }*/

    public int batchInsert(List<?> objs) {
        if (objs == null || objs.size() == 0) {
            return 0;
        }
        BatchInsertBuilder sqlBuilder = new BatchInsertBuilder(objs.get(0).getClass()).use(objs);
        String sql = sqlBuilder.getSql();
        List params = sqlBuilder.getParam();
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(params));
        }
        int[] count = jdbcTemplate.batchUpdate(sql, params);
        saveMultiValue(sqlBuilder.getBeanClass(), objs, false);
        return count.length;
    }

    public <T> T queryById(Serializable id, Class<T> type) {
        ISqlBuilder queryBuilder = SQL.query(type).eq(SqlBuilderUtils.getIdName(type), id);
        String sql = queryBuilder.getSql();
        Object[] params = queryBuilder.getParam().toArray(new Object[0]);
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(params));
        }
        return jdbcTemplate.query(sql, params, getSqlType(params), new BeanHandler<>(type, beanProcessor));
    }

    public <T> T queryById(Serializable id, Class<T> type, String... mvColNames) {
        ISqlBuilder queryBuilder = SQL.query(type).eq(SqlBuilderUtils.getIdName(type), id);
        ((QueryBuilder) queryBuilder).withMVColumn(mvColNames);
        T bean = queryById(id, type);
        fillMultiValue(queryBuilder, Collections.singletonList(bean));
        return bean;
    }

    @SafeVarargs
    public final <T> T queryById(Serializable id, Class<T> type, SFunction<T, ?>... mvFields) {
        ISqlBuilder queryBuilder = SQL.query(type).eq(SqlBuilderUtils.getIdName(type), id);
        ((QueryBuilder) queryBuilder).withMVColumn(mvFields);
        T bean = queryById(id, type);
        fillMultiValue(queryBuilder, Collections.singletonList(bean));
        return bean;
    }

    /**
     * @param sql
     * @param type
     * @param args
     * @return
     * @throws DataAccessException 数据访问异常
     */
    public <T> T queryBean(String sql, Class<T> type, Object... args) throws DataAccessException {
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(args));
        }
        return jdbcTemplate.query(sql, args, getSqlType(args), new BeanHandler<>(type, beanProcessor));
    }

    /**
     * 基于分页原理减少数据扫描以提高查询性能，适用于从大量数据中通过非索引字段查询预期返回单行数据的情况。
     * <p>
     * 注意：但当满足查询条件的数据不止条时，返回的数据可能不是所预期的。适合用于查询最新的一条记录。
     *
     * @return
     * @throws DataAccessException 数据访问异常
     */
    public <T> T queryFirst(ISqlBuilder sqlBuilder) throws DataAccessException {
        String sql = sqlBuilder.getSql();
        List<Object> params = Optional.ofNullable(sqlBuilder.getParam()).orElse(new ArrayList<>());
        PageDialect pageDialect = paginationSupport.buildPaginationSql(sql, 1, 1);
        params.add(pageDialect.getFirstParam());
        params.add(pageDialect.getSecondParam());
        if (isDebugEnabled) {
            logger.debug("sql = " + pageDialect.getSql());
            logger.debug("params = " + objToStr(params));
        }
        Object[] realParams = params.toArray();
        T bean = jdbcTemplate.query(pageDialect.getSql(), realParams, getSqlType(realParams), new BeanHandler<>(sqlBuilder.getBeanClass(), beanProcessor));
        fillMultiValue(sqlBuilder, Collections.singletonList(bean));
        return bean;
    }

    /**
     * 与{@link #queryFirst(ISqlBuilder)}的区别是，当查询到多条数据此方法将抛出异常
     *
     * @return
     * @throws DataAccessException 数据访问异常
     * @throws IncorrectResultSizeDataAccessException 查询到多条数据时
     */
    public <T> T queryOne(ISqlBuilder sqlBuilder) throws DataAccessException {
        String sql = sqlBuilder.getSql();
        List<Object> params = Optional.ofNullable(sqlBuilder.getParam()).orElse(new ArrayList<>());
        // 最多查询2条  是为了提高性能的同时  当有多条符合条件时 程序可以抛出异常
        PageDialect pageDialect = paginationSupport.buildPaginationSql(sql, 1, 2);
        params.add(pageDialect.getFirstParam());
        params.add(pageDialect.getSecondParam());
        if (isDebugEnabled) {
            logger.debug("sql = " + pageDialect.getSql());
            logger.debug("params = " + objToStr(params));
        }
        Object[] realParams = params.toArray();
        T bean = jdbcTemplate.query(pageDialect.getSql(), realParams, getSqlType(realParams), new BeanHandler<>(sqlBuilder.getBeanClass(), beanProcessor));
        fillMultiValue(sqlBuilder, Collections.singletonList(bean));
        return bean;
    }

    /**
     * @param <T>
     * @param queryBuilder
     * @return
     * @throws DataAccessException 数据访问异常
     */
    public <T> List<T> queryList(ISqlBuilder queryBuilder) throws DataAccessException {
        List<T> beanList = queryList(queryBuilder.getSql(), queryBuilder.getBeanClass(), queryBuilder.getParam().toArray());
        fillMultiValue(queryBuilder, beanList);
        return beanList;
    }

    /**
     * 查询列表数据后根据 key 的取值做map映射
     *
     * @param queryBuilder
     * @param key          返回值的key
     * @param <T>
     * @param <R>
     * @return
     */
    public <T, R> Map<R, T> queryListMap(ISqlBuilder queryBuilder, Function<T, R> key) {
        List<T> dataList = queryList(queryBuilder);
        fillMultiValue(queryBuilder, dataList);
        return dataList.stream().collect(Collectors.toMap(key, Function.identity()));
    }

    public <T> List<T> queryList(List<? extends Serializable> ids, Class<T> type) {
        ISqlBuilder queryBuilder = SQL.query(type).in(SqlBuilderUtils.getIdName(type), ids);
        return queryList(queryBuilder);
    }

    /**
     * 查询列表数据后根据 key 的取值做map映射
     *
     * @param ids
     * @param type
     * @param key  返回值的key
     * @param <T>
     * @param <R>
     * @return
     */
    public <T, R> Map<R, T> queryListMap(List<? extends Serializable> ids, Class<T> type, Function<T, R> key) {
        List<T> dataList = queryList(ids, type);
        return dataList.stream().collect(Collectors.toMap(key, Function.identity()));
    }

    /**
     * @param <T>
     * @param sql
     * @param type
     * @param args
     * @return
     * @throws DataAccessException 数据访问异常
     */
    public <T> List<T> queryList(String sql, Class<T> type, Object... args) throws DataAccessException {
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(args));
        }
        List<T> retVal = jdbcTemplate.query(sql, args, getSqlType(args), new BeanListHandler<>(type, beanProcessor));
        if (isDebugEnabled) {
            logger.debug("total = " + (retVal == null ? 0 : retVal.size()));
        }
        return retVal;
    }

    /**
     * @param <T>
     * @param queryBuilder
     * @param page
     * @param pageSize
     * @return
     * @throws DataAccessException 数据访问异常
     */
    public <T> Page<T> queryPage(ISqlBuilder queryBuilder, int page, int pageSize)
            throws DataAccessException {
        Page<T> pageData = queryPage(queryBuilder.getSql(), queryBuilder.getParam().toArray(), page, pageSize, queryBuilder.getBeanClass());
        fillMultiValue(queryBuilder, pageData.getPageData());
        return pageData;
    }

    /**
     * @param <T>
     * @param sql
     * @param params
     * @param page
     * @param pageSize
     * @param beanClass
     * @return
     * @throws DataAccessException 数据访问异常
     */
    public <T> Page<T> queryPage(String sql, Object[] params, int page, int pageSize, Class<T> beanClass)
            throws DataAccessException {

        List<T> item = new ArrayList<>();

        String countSql = buildPaginationCountSql(sql);
        int totalCount = queryCount(countSql, params);

        if (totalCount != 0) {
            PageDialect pageDialect = paginationSupport.buildPaginationSql(sql, page, pageSize);
            Object[] realParam = new Object[params.length + 2];
            System.arraycopy(params, 0, realParam, 0, params.length);
            realParam[realParam.length - 2] = pageDialect.getFirstParam();
            realParam[realParam.length - 1] = pageDialect.getSecondParam();
            item = queryList(pageDialect.getSql(), beanClass, realParam);
        }

        return new Page<>(item, totalCount, page, pageSize);
    }

    /**
     * @param sql      查询数据的SQL
     * @param params   SQL参数
     * @param page     当前页，<b>负数时将查询所有记录</b>
     * @param pageSize 每页显示的条数，<b>负数时将查询所有记录</b>
     * @return
     * @throws DataAccessException
     */
    public Page<Map<String, Object>> queryPage(String sql, Object[] params, int page, int pageSize)
            throws DataAccessException {

        List<Map<String, Object>> item = new ArrayList<>();

        String countSql = buildPaginationCountSql(sql);

        int totalCount = queryCount(countSql, params);

        if (totalCount != 0) {
            PageDialect pageDialect = paginationSupport.buildPaginationSql(sql, page, pageSize);
            int paramsLength = params == null ? 0 : params.length;
            Object[] realParam = new Object[paramsLength + 2];
            System.arraycopy(params == null ? new Object[0] : params, 0, realParam, 0, paramsLength);
            realParam[realParam.length - 2] = pageDialect.getFirstParam();
            realParam[realParam.length - 1] = pageDialect.getSecondParam();
            if (isDebugEnabled) {
                logger.debug("sql = " + pageDialect.getSql());
                logger.debug("params = " + objToStr(realParam));
            }
            item = jdbcTemplate.query(pageDialect.getSql(), realParam, getSqlType(realParam), COLUMN_MAP_ROW_MAPPER);
        }
        return new Page<>(item, totalCount, page, pageSize);
    }

    /**
     * @param queryBuilder lambda
     * @return queryBuilder描述的beanClass类型的对象
     * @throws DataAccessException 详见 {@link JdbcTemplate#queryForObject(String, Class, Object...)}
     */
    public int count(ISqlBuilder queryBuilder) throws DataAccessException {
        String sql = buildPaginationCountSql(queryBuilder.getSql());
        Object[] params = queryBuilder.getParam().toArray();
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(params));
        }
        return jdbcTemplate.queryForObject(sql, params, getSqlType(params), Integer.class);
    }

    /**
     * 基于分页原理，只查询符合条件的第1条数据，给数据库作为性能优化的提示
     *
     * @param queryBuilder
     * @return
     */
    public boolean exist(ISqlBuilder queryBuilder) {
        if (queryBuilder instanceof QueryBuilder) {
            QueryBuilder queryBuilder1 = (QueryBuilder) queryBuilder;
            // 当没有指定具体字段时，只查询id字段
            if (CollectionUtils.isEmpty(queryBuilder1.getColumns())) {
                queryBuilder1.withColumn(SqlBuilderUtils.getIdName(queryBuilder1.getBeanClass()));
            }
        }
        PageDialect pageDialect = paginationSupport.buildPaginationSql(queryBuilder.getSql(), 1, 1);
        Object[] params = Optional.ofNullable(queryBuilder.getParam()).orElse(new ArrayList<>(0)).toArray();
        Object[] realParam = new Object[params.length + 2];
        System.arraycopy(params, 0, realParam, 0, params.length);
        realParam[realParam.length - 2] = pageDialect.getFirstParam();
        realParam[realParam.length - 1] = pageDialect.getSecondParam();
        String sql = pageDialect.getSql();
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(realParam));
        }
        return Boolean.TRUE.equals(jdbcTemplate.query(sql, realParam, getSqlType(realParam), ResultSet::next));
    }

    /**
     * @param sql 原SQL
     * @return 分页 sql
     */
    private String buildPaginationCountSql(String sql) {

        StringBuilder countSql = new StringBuilder(sql);

        int indexOfOrderBy = indexOfOrderBy(sql);
        if (indexOfOrderBy != -1) {
            // 8 是order by的长度
            countSql.delete(indexOfOrderBy - 8, countSql.length());
        }

        int firstFromIndex = countSql.indexOf(" from ");
        int lastFromIndex = countSql.lastIndexOf(" from ");

        // 两个下标值不相等  说明有多个from
        if (firstFromIndex != lastFromIndex) {
            countSql.insert(0, "select count(*) from (").append(") nway_count");
        } //
        else if (indexOfDistinct(sql, firstFromIndex) == -1) {
            countSql.delete(0, firstFromIndex).insert(0, "select count(*) ");
        } else {
            countSql.insert(0, "select count(*) from (").append(") nway_count");
        }
        return countSql.toString();
    }

    /**
     *
     * @param sql
     * @return
     */
    private int indexOfOrderBy(String sql) {

        int idx = -1;
        char[] orderChar = new char[]{'r', 'e', 'd', 'r', 'o'};

        char[] sqlChars = sql.toCharArray();
        outer:
        for (int i = sqlChars.length - 1; i > -1; i--) {
            // 如果发现 ) 则认为order by不可删除，则不查询order by位置
            if (sqlChars[i] == ')') {
                break;
            }
            // etad_noitcudorp yb redro rotinom_t morf  ==> from t_monitor order by production_date
            if (i + 2 < sqlChars.length && sqlChars[i] == 'y' && sqlChars[i - 1] == 'b' && sqlChars[i + 1] == ' ' && sqlChars[i - 2] == ' ') {
                // 如果匹配到 by  则继续匹配 order
                for (int n = 0; n < orderChar.length; n++) {
                    if (sqlChars[i - n - 3] != orderChar[n] ) {
                        break;
                    }
                    if (n == orderChar.length - 1 && (sqlChars[i - n - 4] == ' ' || sqlChars[i - n - 4] == '\n')) {
                        idx = i;
                        break outer;
                    }
                }
            }
        }
        return idx;
    }

    /**
     * SELECT DISTINCT column_name,column_name FROM table_name;
     * <p>
     * 匹配到 “ distinct ” 即可
     *
     * @param sql
     * @return
     */
    private int indexOfDistinct(String sql, int end) {
        int idx = -1;
        char[] disChar = new char[]{'i', 's', 't', 'i', 'n', 'c', 't', ' '};

        char[] sqlChars = sql.toCharArray();
        outer:
        // select 之后
        for (int i = 6; i < end; i++) {
            // select distinct id from abc
            if (i + 10 < end && sqlChars[i] == ' ' && sqlChars[i + 1] == 'd') {
                // 继续匹配 instinct
                for (int n = 0; n < disChar.length; n++) {
                    if (sqlChars[i + n + 2] != disChar[n]) {
                        idx = i + n;
                        break outer;
                    }
                }
            }
        }
        return idx;
    }

    private void initPaginationSupport() {
        Connection conn = null;
        String databaseProductName;
        try {
            conn = dataSource.getConnection();
            databaseProductName = conn.getMetaData().getDatabaseProductName().toUpperCase();
        } catch (SQLException e) {
            throw new CannotGetJdbcConnectionException("访问数据库失败", e);
        } finally {
            if (conn != null) {
                DataSourceUtils.releaseConnection(conn, dataSource);
            }
        }
        if (databaseProductName.contains("ORACLE")) {
            this.paginationSupport = new OraclePaginationSupport();
        } else if (databaseProductName.contains("MYSQL")
                || databaseProductName.contains("MARIADB")) {
            this.paginationSupport = new MysqlPaginationSupport();
        } else {
            throw new UnsupportedOperationException("暂不支持本数据库的分页操作，请实现com.nway.spring.jdbc.pagination.PaginationSupport接口，通过本类setPaginationSupport方法引入。");
        }
    }

    private Integer queryCount(String countSql, Object[] params) {
        if (isDebugEnabled) {
            logger.debug("sql = " + countSql);
            logger.debug("params = " + objToStr(params));
        }
        return jdbcTemplate.query(countSql, params, getSqlType(params), new IntegerResultSetExtractor(countSql));
    }

    @Override
    public void afterPropertiesSet() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.afterPropertiesSet();
        }
        if (getPaginationSupport() == null) {
            initPaginationSupport();
        }
        if (beanProcessor == null) {
            beanProcessor = new DefaultBeanProcessor();
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PaginationSupport getPaginationSupport() {
        return paginationSupport;
    }

    public void setPaginationSupport(PaginationSupport paginationSupport) {
        this.paginationSupport = paginationSupport;
    }

    public void setBeanProcessor(BeanProcessor beanProcessor) {
        this.beanProcessor = beanProcessor;
    }

    public BeanProcessor getBeanProcessor() {
        return beanProcessor;
    }

    private static class IntegerResultSetExtractor implements ResultSetExtractor<Integer> {
        private final String sql;

        IntegerResultSetExtractor(String sql) {
            this.sql = sql;
        }

        @Override
        public Integer extractData(ResultSet rs) {
            try {
                return rs.next() ? rs.getInt(1) : 0;
            } catch (SQLException e) {
                throw new InvalidResultSetAccessException("获取总数据量失败", sql, e);
            }
        }
    }

    /**
     * from 大小写混合时，如果大写form在小写后会导致问题，但是非常规，不考虑
     *
     * @param sql
     * @return
     */
    private int firstFromIndex(StringBuilder sql) {
        int fromIndex = sql.indexOf(" from ");
		if (fromIndex == -1) {
			fromIndex = sql.indexOf(" FROM ");
		}
        return fromIndex;
    }

    /**
     * from 大小写混合时，如果大写form在小写后会导致问题，但是非常规，不考虑
     *
     * @param sql
     * @return
     */
    private int lastFromIndex(StringBuilder sql) {
        int fromIndex = sql.lastIndexOf(" from ");
		if (fromIndex == -1) {
			fromIndex = sql.lastIndexOf(" FROM ");
		}
        return fromIndex;
    }

    private <T> void fillMultiValue(ISqlBuilder queryBuilder, List<T> beanList) {
        if(beanList == null || beanList.isEmpty() || (beanList.size() == 1 && beanList.get(0) == null)) {
            return;
        }
        List<String> multiValColumn = null;
        if(queryBuilder instanceof MultiValQueryBuilder) {
            multiValColumn = ((MultiValQueryBuilder) queryBuilder).getMultiValColumn();
        }
        if (CollectionUtils.isEmpty(multiValColumn)) {
            return;
        }
        Class<T> type = queryBuilder.getBeanClass();
        List<ColumnInfo> multiValueList = SqlBuilderUtils.getEntityInfo(type).getMultiValue();
        if(CollectionUtils.isEmpty(multiValColumn)) {
            multiValColumn = multiValueList.stream().map(ColumnInfo::getColumnName).collect(Collectors.toList());
        }
        if (!multiValueList.isEmpty()) {
            for (ColumnInfo columnInfo : multiValueList) {
                String columnName = columnInfo.getColumnName();
                if(!multiValColumn.contains(columnName)) {
                    continue;
                }

                Map<Object, T> rows = new HashMap<>(beanList.size());
                for (T bean : beanList) {
                    rows.put(SqlBuilderUtils.getIdValue(type, bean), bean);
                }

                StringBuilder subSql = new StringBuilder(64)
                        .append("select fk,")
                        .append(columnName)
                        .append(" from ")
                        .append(SqlBuilderUtils.getTableNameFromCache(type)).append('_').append(columnName)
                        .append(" where fk in (");
                String placeholder = IntStream.range(0, beanList.size()).mapToObj(a -> "?").collect(Collectors.joining(","));
                subSql.append(placeholder).append(") order by idx");
                Object[] idValueArr = rows.keySet().toArray(new Object[0]);
                if (isDebugEnabled) {
                    logger.debug("sql = " + subSql);
                    logger.debug("params = " + objToStr(idValueArr));
                }
                List<Map<String, Object>> subVal = jdbcTemplate.queryForList(subSql.toString(), idValueArr);
                Map<Object, List<Object>> group = new HashMap<>(idValueArr.length);
                for (Map<String, Object> map : subVal) {
                    Object foreignKey = map.get("fk");
                    List<Object> row = group.computeIfAbsent(foreignKey, k -> new ArrayList<>());
                    row.add(map.get(columnName));
                }
                try {
                    for (Map.Entry<Object, List<Object>> entry : group.entrySet()) {
                        columnInfo.getReadMethod().set(rows.get(entry.getKey()), entry.getValue());
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
    }

    /**
     * 认为每次保存都是全量的
     *
     * @param type
     * @param beanList
     * @param <T>
     */
    protected <T> void saveMultiValue(Class<?> type, List<T> beanList, boolean nedDel) {
        List<ColumnInfo> multiValueList = SqlBuilderUtils.getEntityInfo(type).getMultiValue();
        if (!multiValueList.isEmpty()) {
            Map<Integer, Object> idValMap = new HashMap<>(beanList.size());
            for (ColumnInfo columnInfo : multiValueList) {
                String columnName = columnInfo.getColumnName();
                Map<Object, List<Object>> data = new HashMap<>();
                for (T bean : beanList) {
                    Object value = SqlBuilderUtils.getColumnValue(columnInfo, bean, SqlType.INSERT);
                    if (value == null || ((List) value).isEmpty()) {
                        continue;
                    }
                    Object fk = Optional.ofNullable(idValMap.get(bean.hashCode())).orElseGet(() -> {
                        Object idValue = SqlBuilderUtils.getIdValue(type, bean);
                        idValMap.put(bean.hashCode(), idValue);
                        return idValue;
                    });
                    data.put(fk, (List) value);
                }
                if (data.isEmpty()) {
                    continue;
                }
                String tableName = SqlBuilderUtils.getTableNameFromCache(type) + "_" + columnName;

                if(nedDel) {
                    StringBuilder delSql = new StringBuilder(64)
                            .append("delete from ")
                            .append(tableName)
                            .append(" where fk in (");
                    String placeholder = IntStream.range(0, data.size()).mapToObj(a -> "?").collect(Collectors.joining(","));
                    delSql.append(placeholder).append(")");
                    Object[] idValueArr = idValMap.values().toArray(new Object[0]);
                    if (isDebugEnabled) {
                        logger.debug("sql = " + delSql);
                        logger.debug("params = " + objToStr(idValueArr));
                    }
                    jdbcTemplate.update(delSql.toString(), idValueArr);
                }

                StringBuilder insertSql = new StringBuilder(64)
                        .append("insert into ")
                        .append(tableName)
                        .append("(id, fk,").append(columnName).append(",idx) values (?,?,?,?)");

                for (Map.Entry<Object, List<Object>> entry : data.entrySet()) {
                    Collection<Object> entryValue = entry.getValue();
                    List<Object[]> rows = new ArrayList<>(entryValue.size());
                    int idx = 0;
                    for (Object bizVal : entryValue) {
                        rows.add(new Object[]{IdWorker.getId(), entry.getKey(), bizVal, ++idx});
                    }
                    if (isDebugEnabled) {
                        logger.debug("sql = " + insertSql);
                        logger.debug("params = " + objToStr(rows));
                    }
                    jdbcTemplate.batchUpdate(insertSql.toString(), rows);
                }
            }
        }
    }

    private String objToStr(Object obj) {
        if (ObjectUtils.isArray(obj)) {
            return Arrays.deepToString((Object[]) obj);
        } else if (obj instanceof List) {
            return Arrays.deepToString(((List) obj).toArray());
        }
        return ObjectUtils.nullSafeToString(obj);
    }

    private int[] getSqlType(Object[] objs) {
        if (objs == null) {
            return null;
        }
        if (objs.length == 0) {
            return new int[0];
        }
        return Arrays.stream(objs)
                .map(obj -> {
                    if (obj == null) {
                        return SqlTypeValue.TYPE_UNKNOWN;
                    }
                    return StatementCreatorUtils.javaTypeToSqlParameterType(obj.getClass());
                })
                .mapToInt(x -> x)
                .toArray();
    }
}