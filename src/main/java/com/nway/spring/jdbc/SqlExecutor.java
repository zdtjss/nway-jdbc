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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.nway.spring.jdbc.pagination.*;
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
import com.nway.spring.jdbc.sql.builder.BatchInsertBuilder;
import com.nway.spring.jdbc.sql.builder.BatchUpdateByIdBuilder;
import com.nway.spring.jdbc.sql.builder.UpdateBeanBuilder;
import com.nway.spring.jdbc.sql.builder.DeleteBuilder;
import com.nway.spring.jdbc.sql.builder.InsertBuilder;
import com.nway.spring.jdbc.sql.builder.QueryBuilder;
import com.nway.spring.jdbc.sql.builder.ISqlBuilder;
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
 *
 * @since 2014-03-28
 */
public class SqlExecutor implements InitializingBean {

	protected final Log logger = LogFactory.getLog(SqlExecutor.class);

	private PaginationSupport paginationSupport;
	
	private JdbcTemplate jdbcTemplate;
	
	private DataSource dataSource;
	
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
	 * 
	 * @param sqlBuilder sqlBuilder
	 * @return
	 */
	public int update(ISqlBuilder sqlBuilder) {
		String sql = sqlBuilder.getSql();
		Object[] params = sqlBuilder.getParam().toArray();
		if(logger.isDebugEnabled()) {
			logger.debug("sql = " + sql);
			logger.debug("params = " + objToStr(params));
		}
		return jdbcTemplate.update(sql, params);
	}
	
	/**
	 * 
	 * @param sqlBuilder sqlBuilder
	 * @return
	 */
	public int[] batchUpdate(ISqlBuilder sqlBuilder) {
		List<Object[]> params = sqlBuilder.getParam().stream().map(e -> ((Collection) e).toArray()).collect(Collectors.toList());
		String sql = sqlBuilder.getSql();
		if(logger.isDebugEnabled()) {
			logger.debug("sql = " + sql);
			logger.debug("params = " + objToStr(params));
		}
		return jdbcTemplate.batchUpdate(sql, params);
	}
	
	/**
	 * 
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
	 * 
	 * @param objs beans
	 * @return
	 */
	public int[] batchUpdateById(List<?> objs) {
		if (objs == null || objs.size() == 0) {
			return new int[] {};
		}
		Class<?> beanClass = objs.get(0).getClass();
		return batchUpdate(new BatchUpdateByIdBuilder(beanClass).use(objs));
	}
	
	/**
	 * 
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
		InsertBuilder sqlBuilder = SQL.insert(obj.getClass());
		sqlBuilder.use(obj);
		return update(sqlBuilder);
	}
	
	public <T> T insertAndGetKey(Object obj) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		InsertBuilder sqlBuilder = SQL.insert(obj.getClass());
		sqlBuilder.use(obj);
		PreparedStatementCreator psc = conn -> {
			PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.getSql());
			List<Object> params = sqlBuilder.getParam();
			if (params != null && params.size() > 0) {
				for (int i = 0; i < params.size(); i++) {
					StatementCreatorUtils.setParameterValue(pstmt, i, StatementCreatorUtils.javaTypeToSqlParameterType(params.get(i).getClass()), params.get(i));
				}
			}
			return pstmt;
		};
		Object key = sqlBuilder.getKeyValue();
		if (logger.isDebugEnabled()) {
			logger.debug(sqlBuilder.getSql());
			logger.debug(sqlBuilder.getParam().toArray());
		}
		int count = jdbcTemplate.update(psc, keyHolder);
		return count == 0 ? (T) (key != null ? key : keyHolder.getKey()) : null;
	}
	
	public int[] batchInsert(List<?> objs) {
		if (objs == null || objs.size() == 0) {
			return new int[] {};
		}
		BatchInsertBuilder batchInsertBuilder = new BatchInsertBuilder(objs.get(0).getClass());
		batchInsertBuilder.use(objs);
		return batchUpdate(batchInsertBuilder);
	}
	
	public <T> T queryById(Serializable id, Class<T> type) {
		ISqlBuilder queryBuilder = SQL.query(type).where().eq(SqlBuilderUtils.getIdName(type), id);
		return queryBean(queryBuilder);
	}
	
	/**
	 * 
	 * 
	 * @param queryBuilder lambda
	 * @return queryBuilder描述的beanClass类型的对象
	 * @throws DataAccessException 数据访问异常
	 */
	public <T> T queryBean(ISqlBuilder queryBuilder) throws DataAccessException {
		return queryBean(queryBuilder.getSql(), queryBuilder.getBeanClass(), queryBuilder.getParam().toArray());
	}
	
	/**
	 * 
	 * 
	 * @param sql
	 * @param type
	 * @param args
	 * @return
	 * @throws DataAccessException 数据访问异常
	 */
	public <T> T queryBean(String sql, Class<T> type, Object... args) throws DataAccessException {
		if(logger.isDebugEnabled()) {
			logger.debug("sql = " + sql);
			logger.debug("params = " + objToStr(args));
		}
		return jdbcTemplate.query(sql, args, getSqlType(args), new BeanHandler<T>(type));
	}

	/**
	 * 
	 * 
	 * @param <T>
	 * @param queryBuilder
	 * @return
	 * @throws DataAccessException 数据访问异常
	 */
	public <T> List<T> queryList(ISqlBuilder queryBuilder) throws DataAccessException {
		return queryList(queryBuilder.getSql(), queryBuilder.getBeanClass(), queryBuilder.getParam().toArray());
	}
	
	public <T> List<T> queryList(List<? extends Serializable> ids, Class<T> type) {
		ISqlBuilder queryBuilder = SQL.query(type).where().in(SqlBuilderUtils.getIdName(type), ids);
		return queryList(queryBuilder);
	}
	
	/**
	 * 
	 * 
	 * @param <T>
	 * @param sql
	 * @param type
	 * @param args
	 * @return
	 * @throws DataAccessException 数据访问异常
	 */
	public <T> List<T> queryList(String sql, Class<T> type, Object... args) throws DataAccessException {
		if(logger.isDebugEnabled()) {
			logger.debug("sql = " + sql);
			logger.debug("params = " + objToStr(args));
		}
		List<T> retVal = jdbcTemplate.query(sql, args, getSqlType(args), new BeanListHandler<>(type));
		if(logger.isDebugEnabled()) {
			logger.debug("total = " + retVal.size());
		}
		return retVal;
	}

	/**
	 * 
	 * 
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
	 * 
	 * 
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
	 * 
	 * 
	 * @param queryBuilder
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws DataAccessException
	 */
	public Page<Map<String, Object>> queryPage(QueryBuilder<?> queryBuilder, int page,
											   int pageSize) throws DataAccessException {
		return queryPage(queryBuilder.getSql(), queryBuilder.getParam().toArray(), page, pageSize);
	}
	
	/**
	 * 
	 * 
	 * @param sql
	 *            查询数据的SQL
	 * @param params
	 *            SQL参数
	 * @param page
	 *            当前页，<b>负数时将查询所有记录</b>
	 * @param pageSize
	 *            每页显示的条数，<b>负数时将查询所有记录</b>
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
			Object[] realParam = new Object[params.length + 2];
			System.arraycopy(params, 0, realParam, 0, params.length);
			realParam[realParam.length - 2] = pageDialect.getFirstParam();
			realParam[realParam.length - 1] = pageDialect.getSecondParam();
			if(logger.isDebugEnabled()) {
				logger.debug("sql = " + pageDialect.getSql());
				logger.debug("params = " + objToStr(realParam));
			}
			item = jdbcTemplate.queryForList(pageDialect.getSql(), realParam, getSqlType(params));
		}
		return new Page<>(item, totalCount, page, pageSize);
	}

	/**
	 * 
	 * 
	 * @param queryBuilder lambda
	 * @return queryBuilder描述的beanClass类型的对象
	 * 
	 * @throws DataAccessException 详见 {@link JdbcTemplate#queryForObject(String, Class, Object...)}
	 */
	public int count(ISqlBuilder queryBuilder) throws DataAccessException {
		String sql = buildPaginationCountSql(queryBuilder.getSql());
		Object[] params = queryBuilder.getParam().toArray();
		if(logger.isDebugEnabled()) {
			logger.debug("sql = " + sql);
			logger.debug("params = " + objToStr(params));
		}
		return jdbcTemplate.queryForObject(sql, params, getSqlType(params), Integer.class);
	}
	
	/**
	 *
	 * @param sql
	 *            原SQL
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
		} 
		else {
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
		} 
		catch (SQLException e) {
			throw new CannotGetJdbcConnectionException("访问数据库失败", e);
		}
		finally {
			if(conn != null) {
				DataSourceUtils.releaseConnection(conn, dataSource);
			}
		}
		if (databaseProductName.contains("ORACLE")) {
			this.paginationSupport = new OraclePaginationSupport();
		} 
		else if (databaseProductName.contains("MYSQL")
				|| databaseProductName.contains("MARIADB")) {
			this.paginationSupport = new MysqlPaginationSupport();
		} 
		else {
			throw new UnsupportedOperationException("暂不支持本数据库的分页操作，请实现com.nway.spring.jdbc.pagination.PaginationSupport接口，通过本类setPaginationSupport方法引入。");
		}
	}
	
	private Integer queryCount(String countSql, Object[] params) {
		if(logger.isDebugEnabled()) {
			logger.debug("sql = " + countSql);
			logger.debug("params = " + objToStr(params));
		}
		return jdbcTemplate.query(countSql, params, getSqlType(params), new IntegerResultSetExtractor(countSql));
	}

	@Override
	public void afterPropertiesSet() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.afterPropertiesSet();
		if (getPaginationSupport() == null) {
			initPaginationSupport();
		}
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public PaginationSupport getPaginationSupport() {
		return paginationSupport;
	}

	public void setPaginationSupport(PaginationSupport paginationSupport) {
		this.paginationSupport = paginationSupport;
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
			} 
			catch (SQLException e) {
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
		}
		else if (obj instanceof List) {
			return Arrays.deepToString(((List) obj).toArray());
		}
		return ObjectUtils.nullSafeToString(obj);
	}

	private int[] getSqlType(Object[] objs) {
		if (objs != null) {
			return Arrays.stream(objs)
					.map(obj -> StatementCreatorUtils.javaTypeToSqlParameterType(obj.getClass()))
					.mapToInt(x -> x)
					.toArray();
		}
		return null;
	}
}