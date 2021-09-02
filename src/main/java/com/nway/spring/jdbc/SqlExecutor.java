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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.nway.spring.jdbc.bean.processor.BeanProcessor;
import com.nway.spring.jdbc.bean.processor.DefaultBeanProcessor;
import com.nway.spring.jdbc.pagination.*;
import com.nway.spring.jdbc.sql.builder.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.nway.spring.jdbc.bean.BeanHandler;
import com.nway.spring.jdbc.bean.BeanListHandler;
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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
    /**
     * 最后一个不以 ) 结尾的 order by 匹配正则 <br>
     */
    private static final Pattern SQL_ORDER_BY_PATTERN = Pattern.compile(".+\\p{Blank}+(ORDER|order)\\p{Blank}+(BY|by)[\\,\\p{Blank}\\w\\.]+");
    /**
     * SQL 语句中top匹配
     */
    private static final Pattern SQL_TOP_PATTERN = Pattern.compile(".+(TOP|top)\\p{Blank}+\\d+\\p{Blank}+.+");

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

    /**
     * @param obj bean
     * @return
     */
    public int updateById(Object obj) {
        Class<?> beanClass = obj.getClass();
        UpdateBeanBuilder sqlBuilder = new UpdateBeanBuilder(obj);
        sqlBuilder.where().eq(SqlBuilderUtils.getIdName(beanClass), SqlBuilderUtils.getIdValue(beanClass, obj));
        return update(sqlBuilder);
    }

    /**
     * @param objs beans
     * @return
     */
    public int[] batchUpdateById(List<?> objs) {
        if (objs == null || objs.size() == 0) {
            return new int[]{};
        }
        Class<?> beanClass = objs.get(0).getClass();
        ISqlBuilder sqlBuilder = new BatchUpdateByIdBuilder(beanClass).use(objs);
        String sql = sqlBuilder.getSql();
        List params = sqlBuilder.getParam();
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(params.toArray()));
        }
        return jdbcTemplate.batchUpdate(sql, params, params.size() == 0 ? new int[0] : getSqlType((Object[]) params.get(0)));
    }

    /**
     * @param id
     * @param beanClass
     * @return
     */
    public int deleteById(Serializable id, Class<?> beanClass) {
        DeleteBuilder sqlBuilder = new DeleteBuilder(beanClass);
        sqlBuilder.where().eq(SqlBuilderUtils.getIdName(beanClass), id);
        return update(sqlBuilder);
    }

    public int batchDeleteById(Collection<? extends Serializable> ids, Class<?> beanClass) {
        DeleteBuilder sqlBuilder = new DeleteBuilder(beanClass);
        sqlBuilder.where().in(SqlBuilderUtils.getIdName(beanClass), ids);
        return update(sqlBuilder);
    }

    public int insert(Object obj) {
        return update(SQL.insert(obj.getClass()).use(obj));
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

    public int[] batchInsert(List<?> objs) {
        if (objs == null || objs.size() == 0) {
            return new int[]{};
        }
        BatchInsertBuilder sqlBuilder = new BatchInsertBuilder(objs.get(0).getClass()).use(objs);
        String sql = sqlBuilder.getSql();
        List params = sqlBuilder.getParam();
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(params));
        }
        return jdbcTemplate.batchUpdate(sql, params);
    }

    public <T> T queryById(Serializable id, Class<T> type) {
        ISqlBuilder queryBuilder = SQL.query(type).where().eq(SqlBuilderUtils.getIdName(type), id);
        String sql = queryBuilder.getSql();
        Object[] params = queryBuilder.getParam().toArray(new Object[0]);
        if (isDebugEnabled) {
            logger.debug("sql = " + sql);
            logger.debug("params = " + objToStr(params));
        }
        return jdbcTemplate.query(sql, params, getSqlType(params), new BeanHandler<>(type, beanProcessor));
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
     * 基于分页原理减少数据扫描以提高查询性能，适用于从大量数据中通过非索引字段查询预期返回单行数据的情况
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
        return jdbcTemplate.query(pageDialect.getSql(), realParams, getSqlType(realParams), new BeanHandler<>(sqlBuilder.getBeanClass(), beanProcessor));
    }

    /**
     * @param <T>
     * @param queryBuilder
     * @return
     * @throws DataAccessException 数据访问异常
     */
    public <T> List<T> queryList(ISqlBuilder queryBuilder) throws DataAccessException {
        return queryList(queryBuilder.getSql(), queryBuilder.getBeanClass(), queryBuilder.getParam().toArray());
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
        return dataList.stream().collect(Collectors.toMap(key, Function.identity()));
    }

    public <T> List<T> queryList(List<? extends Serializable> ids, Class<T> type) {
        ISqlBuilder queryBuilder = SQL.query(type).where().in(SqlBuilderUtils.getIdName(type), ids);
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
        return queryPage(queryBuilder.getSql(), queryBuilder.getParam().toArray(), page, pageSize, queryBuilder.getBeanClass());
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

        List<T> item = new ArrayList<T>();

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

        String upperCaseSql = sql.toUpperCase();
        String countSql = buildPaginationCountSql(upperCaseSql);

        int totalCount = queryCount(countSql, params);

        if (totalCount != 0) {
            PageDialect pageDialect = paginationSupport.buildPaginationSql(upperCaseSql, page, pageSize);
            int paramsLength = params == null ? 0 : params.length;
            Object[] realParam = new Object[paramsLength + 2];
            System.arraycopy(params == null ? new Object[0] : params, 0, realParam, 0, paramsLength);
            realParam[realParam.length - 2] = pageDialect.getFirstParam();
            realParam[realParam.length - 1] = pageDialect.getSecondParam();
            if (isDebugEnabled) {
                logger.debug("sql = " + pageDialect.getSql());
                logger.debug("params = " + objToStr(realParam));
            }
            item = jdbcTemplate.queryForList(pageDialect.getSql(), realParam, getSqlType(realParam));
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
            QueryBuilder<?> queryBuilder1 = (QueryBuilder<?>) queryBuilder;
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

        if (SQL_ORDER_BY_PATTERN.matcher(sql).matches()) {
            int orderIdx = countSql.lastIndexOf(" order ");
            countSql.delete(orderIdx == -1 ? countSql.lastIndexOf(" ORDER ") : orderIdx, countSql.length());
        }

        int firstFromIndex = firstFromIndex(countSql.toString(), 0);
        String selectedColumns = countSql.substring(0, firstFromIndex);

        if ((!selectedColumns.contains(" DISTINCT ") || !selectedColumns.contains(" distinct "))
                && !SQL_TOP_PATTERN.matcher(selectedColumns).matches()) {
            countSql.delete(0, firstFromIndex).insert(0, "SELECT COUNT(*) ");
        } else {
            countSql.insert(0, "SELECT COUNT(*) FROM (").append(')');
        }

        return countSql.toString();
    }

    private void initPaginationSupport() {
        Connection conn = null;
        String databaseProductName = "";
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

    private int firstFromIndex(String sql, int startIndex) {
        int fromIndex = sql.toUpperCase().indexOf("FROM", startIndex);
        char previousChar = sql.charAt(fromIndex - 1);
        char nextChar = sql.charAt(fromIndex + 4);
        if (!(previousChar == ' ' || previousChar == '*' || previousChar == '\t' || previousChar == '\n')
                && !(nextChar == ' ' || nextChar == '(' || nextChar == '\t' || nextChar == '\n')) {
            fromIndex = firstFromIndex(sql, fromIndex + 4);
        }
        return fromIndex;
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