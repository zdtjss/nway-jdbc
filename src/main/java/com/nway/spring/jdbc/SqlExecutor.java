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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.nway.spring.jdbc.bean.BeanHandler;
import com.nway.spring.jdbc.bean.BeanListHandler;
import com.nway.spring.jdbc.json.JsonHandler;
import com.nway.spring.jdbc.json.JsonListHandler;
import com.nway.spring.jdbc.sql.builder.QueryBuilder;
import com.nway.spring.jdbc.sql.builder.SqlBuilder;

/**
 * 注意：
 * <p>
 * &nbsp;&nbsp;查询不到数据时：
 * <ul>
 * <li>queryForBean返回null；</li>
 * <li>queryForBeanList返回值size() == 0；</li>
 * <li>queryForBeanPagination返回值getTotalCount() == 0；</li>
 * <li>queryForMapListPagination返回值getTotalCount() == 0；</li>
 * <li>queryForJson返回"{}"</li>
 * <li>queryForJsonList返回"[]"</li>
 * <li>testJsonPagination返回对象中totalCount == 0</li>
 * </ul>
 * 
 * @author zdtjss@163.com
 *
 * @since 2014-03-28
 */
public class SqlExecutor extends JdbcTemplate {

	private PaginationSupport paginationSupport;

	/**
	 * 最后一个不以 ) 结尾的 order by 匹配正则 <br>
	 */
	private static final Pattern SQL_ORDER_BY_PATTERN = Pattern
			.compile(".+\\p{Blank}+(ORDER|order)\\p{Blank}+(BY|by)[\\,\\p{Blank}\\w\\.]+");
	/**
	 * SQL 语句中top匹配
	 */
	private static final Pattern SQL_TOP_PATTERN = Pattern.compile(".+(TOP|top)\\p{Blank}+\\d+\\p{Blank}+.+");

	public int update(SqlBuilder sqlBuilder) {
		
		return super.update(sqlBuilder.getSql(), sqlBuilder.getParam().toArray());
	}
	
	public int[] batchUpdate(SqlBuilder sqlBuilder) {
		
		List<Object> params = sqlBuilder.getParam();
		
		return super.batchUpdate(sqlBuilder.getSql(), params.stream().map(e -> (Object[]) e).collect(Collectors.toList()));
	}
	
	public int insert(SqlBuilder sqlBuilder) {
		return update(sqlBuilder);
	}
	
	public int[] batchInsert(SqlBuilder sqlBuilder) {
		return batchUpdate(sqlBuilder);
	}
	
	/**
	 * 
	 * 
	 * @param queryBuilder lambda
	 * @return queryBuilder描述的beanClass类型的对象
	 * @throws DataAccessException
	 */
	public <T> T queryForBean(SqlBuilder queryBuilder) throws DataAccessException {

		return queryForBean(queryBuilder.getSql(), queryBuilder.getBeanClass(), queryBuilder.getParam().toArray());
	}
	
	/**
	 * 
	 * 
	 * @param sql
	 * @param type
	 * @return
	 * @throws DataAccessException
	 */
	public <T> T queryForBean(String sql, Class<T> type) throws DataAccessException {

		return super.query(sql, new BeanHandler<T>(type));
	}
	
	/**
	 * 
	 * 
	 * @param sql
	 * @param type
	 * @param args
	 * @return
	 * @throws DataAccessException
	 */
	public <T> T queryForBean(String sql, Class<T> type, Object... args) throws DataAccessException {
		
		return super.query(sql, new BeanHandler<T>(type), args);
	}

	/**
	 * 
	 * 
	 * @param sql
	 * @param args
	 * @param argTypes
	 * @param type
	 * @return
	 * @throws DataAccessException
	 */
	public <T> T queryForBean(String sql, Object[] args, int[] argTypes, Class<T> type)
			throws DataAccessException {

		return super.query(sql, args, argTypes, new BeanHandler<T>(type));
	}

	/**
	 * 
	 * 
	 * @param sql
	 * @param type
	 * @return
	 * @throws DataAccessException
	 */
	public <T> List<T> queryForBeanList(String sql, Class<T> type) throws DataAccessException {

		return super.query(sql, new BeanListHandler<T>(type));
	}

	/**
	 * 
	 * 
	 * @param <T>
	 * @param queryBuilder
	 * @return
	 * @throws DataAccessException
	 */
	public <T> List<T> queryForBeanList(SqlBuilder queryBuilder) throws DataAccessException {
		
		return queryForBeanList(queryBuilder.getSql(), queryBuilder.getBeanClass(), queryBuilder.getParam().toArray());
	}
	
	/**
	 * 
	 * 
	 * @param <T>
	 * @param sql
	 * @param type
	 * @param args
	 * @return
	 * @throws DataAccessException
	 */
	public <T> List<T> queryForBeanList(String sql, Class<T> type, Object... args) throws DataAccessException {
		
		return super.query(sql, new BeanListHandler<T>(type), args);
	}

	/**
	 * 
	 * 
	 * @param <T>
	 * @param sql
	 * @param args
	 * @param argTypes
	 * @param type
	 * @return
	 * @throws DataAccessException
	 */
	public <T> List<T> queryForBeanList(String sql, Object[] args, int[] argTypes, Class<T> type)
			throws DataAccessException {

		return super.query(sql, args, argTypes, new BeanListHandler<T>(type));
	}
	
	/**
	 * 
	 * 
	 * @param sql
	 * @param type
	 * @return
	 * @throws DataAccessException
	 */
	public String queryForJson(String sql, Class<?> type) throws DataAccessException {

		return super.query(sql, new JsonHandler(type, sql));
	}
	
	/**
	 * 
	 * 
	 * @param sql
	 * @param type
	 * @param args
	 * @return
	 * @throws DataAccessException
	 */
	public String queryForJson(String sql, Class<?> type,Object... args) throws DataAccessException {
		
		return super.query(sql, new JsonHandler(type, sql), args);
	}
	
	/**
	 * 
	 * 
	 * @param sql
	 * @param args
	 * @param argTypes
	 * @param type
	 * @return
	 * @throws DataAccessException
	 */
	public String queryForJson(String sql, Object[] args, int[] argTypes, Class<?> type) throws DataAccessException {

		return super.query(sql, args, argTypes, new JsonHandler(type, sql));
	}
	
	/**
	 * 
	 * 
	 * @param queryBuilder
	 * @return
	 * @throws DataAccessException
	 */
	public String queryForJsonList(QueryBuilder queryBuilder) throws DataAccessException {
	    
	    return queryForJsonList(queryBuilder.getSql(), queryBuilder.getBeanClass(), queryBuilder.getParam().toArray());
	}
	
	/**
	 * 
	 * 
	 * @param sql
	 * @param type
	 * @return
	 * @throws DataAccessException
	 */
	public String queryForJsonList(String sql, Class<?> type) throws DataAccessException {
		
		return super.query(sql, new JsonListHandler(type));
	}
	
	/**
	 * 
	 * 
	 * @param sql
	 * @param type
	 * @param args
	 * @return
	 * @throws DataAccessException
	 */
	public String queryForJsonList(String sql, Class<?> type, Object... args) throws DataAccessException {
	    
	    return super.query(sql, new JsonListHandler(type), args);
	}
	
	/**
	 * 
	 * 
	 * @param sql
	 * @param args
	 * @param argTypes
	 * @param type
	 * @return
	 * @throws DataAccessException
	 */
	public String queryForJsonList(String sql, Object[] args, int[] argTypes, Class<?> type) throws DataAccessException {
	    
	    return super.query(sql, args, argTypes, new JsonListHandler(type));
	}
	
	/**
	 * 
	 * 
	 * @param <T>
	 * @param queryBuilder
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws DataAccessException
	 */
	public <T> Pagination<T> queryForBeanPagination(SqlBuilder queryBuilder, int page, int pageSize)
			throws DataAccessException {

		return queryForBeanPagination(queryBuilder.getSql(), queryBuilder.getParam().toArray(), null, page, pageSize, queryBuilder.getBeanClass());
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
	 * @throws DataAccessException
	 */
	public <T> Pagination<T> queryForBeanPagination(String sql, Object[] params, int page,
			int pageSize, Class<T> beanClass) throws DataAccessException {
		
		return queryForBeanPagination(sql, params, null, page, pageSize, beanClass);
	}

	/**
	 * 
	 * 
	 * @param <T>
	 * @param sql
	 * @param params
	 * @param argTypes
	 * @param page
	 * @param pageSize
	 * @param beanClass
	 * @return
	 * @throws DataAccessException
	 */
	public <T> Pagination<T> queryForBeanPagination(String sql, Object[] params, int[] argTypes, int page,
			int pageSize, Class<T> beanClass) throws DataAccessException {

		List<T> item = Collections.emptyList();

		String countSql = buildPaginationCountSql(sql);
		int totalCount = queryCount(countSql, params, argTypes);
		
		if (totalCount != 0) {
			String paginationSql = paginationSupport.buildPaginationSql(sql, page, pageSize);
			if (argTypes == null) {
				item = queryForBeanList(paginationSql, beanClass, params);
			} 
			else {
				item = queryForBeanList(paginationSql, params, argTypes, beanClass);
			}
		}

		return new Pagination<T>(item, totalCount, page, pageSize);
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
	public Pagination<Map<String, Object>> queryForMapListPagination(QueryBuilder queryBuilder, int page,
			int pageSize) throws DataAccessException {

		return queryForMapListPagination(queryBuilder.getSql(), queryBuilder.getParam().toArray(), null, page, pageSize);
	}
	
	/**
	 * 
	 * 
	 * @param sql
	 * @param params
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws DataAccessException
	 */
	public Pagination<Map<String, Object>> queryForMapListPagination(String sql, Object[] params, int page,
			int pageSize) throws DataAccessException {
		
		return queryForMapListPagination(sql, params, null, page, pageSize);
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
	public Pagination<Map<String, Object>> queryForMapListPagination(String sql, Object[] params, int[] argTypes,
			int page, int pageSize) throws DataAccessException {

		List<Map<String, Object>> item = Collections.emptyList();

		String upperCaseSql = sql.toUpperCase();

		String countSql = buildPaginationCountSql(upperCaseSql);

		int totalCount = queryCount(countSql, params, argTypes);

		if (totalCount != 0) {

			String paginationSql = paginationSupport.buildPaginationSql(upperCaseSql, page, pageSize);

			if (argTypes == null) {
				
				item = queryForList(paginationSql, params);
			} 
			else {
				item = queryForList(paginationSql, params, argTypes);
			}
		}

		return new Pagination<Map<String, Object>>(item, totalCount, page, pageSize);
	}

	/**
	 * 
	 * 
	 * @param sql
	 * @param params
	 * @param page
	 * @param pageSize
	 * @return
	 * @throws DataAccessException
	 */
	public String queryForJsonPagination(String sql, Object[] params, int page, int pageSize)
			throws DataAccessException {
		
		return queryForJsonPagination(sql, params, null, page, pageSize, null);
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
	public String queryForJsonPagination(QueryBuilder queryBuilder, int page, int pageSize)
	        throws DataAccessException {
	    
	    return queryForJsonPagination(queryBuilder.getSql(), queryBuilder.getParam().toArray(), null, page, pageSize, queryBuilder.getBeanClass());
	}
	
	/**
	 * 
	 * 
	 * @param sql
	 * @param params
	 * @param page
	 * @param pageSize
	 * @param beanClass
	 * @return
	 * @throws DataAccessException
	 */
	public String queryForJsonPagination(String sql, Object[] params, int page, int pageSize, Class<?> beanClass)
			throws DataAccessException {
		
		return queryForJsonPagination(sql, params, null, page, pageSize, beanClass);
	}
    
	/**
	 * 
	 * 
	 * @param sql
	 * @param params
	 * @param argTypes
	 * @param page
	 * @param pageSize
	 * @param beanClass 
	 * @return
	 */
	public String queryForJsonPagination(String sql, Object[] params, int[] argTypes, int page, int pageSize,
			Class<?> beanClass) {
		
		StringBuilder json = new StringBuilder("{");
		
		String countSql = buildPaginationCountSql(sql);

		int pageCount = 0;
		int totalCount = queryCount(countSql, params, argTypes);

		if (totalCount != 0) {

			String paginationSql = paginationSupport.buildPaginationSql(sql, page, pageSize);

			if (argTypes == null) {
		        json.append("\"pageData\":").append(queryForJsonList(paginationSql, beanClass, params)).append(',');
			}
			else {
		        json.append("\"pageData\":").append(queryForJsonList(paginationSql, params, argTypes, beanClass)).append(',');
			}
			
			pageCount = totalCount / pageSize;
			
			if (totalCount % pageSize > 0) {
				pageCount++;
			}
		}
		
		json.append("\"totalCount\":").append(totalCount).append(",\"pageCount\":").append(pageCount).append(",\"page\":").append(page).append(",\"pageSize\":").append(pageSize).append('}');
		
		return json.toString();
	}
    
	/**
	 * 
	 * 
	 * @param requiredType 预期类型
	 * @param sql sql
	 * @param defaultValue 有异常时的默认值
	 * @return
	 * @throws DataAccessException 详见 {@link JdbcTemplate#queryForObject(String, Class, Object...)}
	 */
	public <T> T queryForObject(Class<T> requiredType, String sql, T defaultValue) throws DataAccessException {
    	T obj = null;
		try {
			obj = super.queryForObject(sql, requiredType);
		}
		catch (EmptyResultDataAccessException e) {
			obj = defaultValue;
		}
		return obj;
	}
	
	/**
	 * 
	 * 
	 * @param requiredType 预期类型
	 * @param sql sql
	 * @param args argTypes
	 * @param defaultValue 有异常时的默认值
	 * @return
	 * @throws DataAccessException 详见 {@link JdbcTemplate#queryForObject(String, Class, Object...)}
	 */
	public <T> T queryForObject(Class<T> requiredType, String sql, Object[] args, T defaultValue)
			throws DataAccessException {
		T obj = null;
		try {
			obj = super.queryForObject(sql, args, requiredType);
		}
		catch (EmptyResultDataAccessException e) {
			obj = defaultValue;
		}
		return obj;
	}
	
	/**
	 * 
	 * 
	 * @param requiredType 预期类型
	 * @param sql sql
	 * @param args param
	 * @param argTypes argTypes
	 * @param defaultValue 有异常时的默认值
	 * @return
	 * @throws DataAccessException 详见 {@link JdbcTemplate#queryForObject(String, Class, Object...)}
	 */
	public <T> T queryForObject(Class<T> requiredType, String sql, Object[] args, int[] argTypes, T defaultValue)
			throws DataAccessException {
		T obj = null;
		try {
			obj = super.queryForObject(sql, args, argTypes, requiredType);
		}
		catch (EmptyResultDataAccessException e) {
			obj = defaultValue;
		}
		return obj;
	}
	
	/**
	 * 
	 * 
	 * @param queryBuilder lambda
	 * @return queryBuilder描述的beanClass类型的对象
	 * 
	 * @throws DataAccessException 详见 {@link JdbcTemplate#queryForObject(String, Class, Object...)}
	 */
	public int count(QueryBuilder queryBuilder) throws DataAccessException {
		return queryForObject(queryBuilder.getSql(), Integer.class, queryBuilder.getParam().toArray());
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
			countSql.delete(countSql.lastIndexOf(" ORDER "), countSql.length());
		}
		
		int firstFromIndex = firstFromIndex(countSql.toString(), 0);
		String selectedColumns = countSql.substring(0, firstFromIndex);
		
		if ((selectedColumns.indexOf(" DISTINCT ") == -1 || selectedColumns.indexOf(" distinct ") == -1)
				&& !SQL_TOP_PATTERN.matcher(selectedColumns).matches()) {
			countSql.delete(0, firstFromIndex).insert(0, "SELECT COUNT(1) ");
		} 
		else {
			countSql.insert(0, "SELECT COUNT(1) FROM (").append(')');
		}
		
		return countSql.toString();
	}

	private void initPaginationSupport() {

		Connection conn = null;
		
		String databaseProductName = null;

		try {
			conn = getDataSource().getConnection();
			databaseProductName = conn.getMetaData().getDatabaseProductName().toUpperCase();
		} 
		catch (SQLException e) {
			throw new CannotGetJdbcConnectionException("访问数据库失败", e);
		}
		finally {
			if(conn != null) {
				DataSourceUtils.releaseConnection(conn, getDataSource());
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
			throw new UnsupportedOperationException("暂不支持本数据库的分页操作，请实现com.nway.spring.jdbc.PaginationSupport接口，通过本类setPaginationSupport方法引入。");
		}
	}
	
	private int queryCount(String countSql, Object[] params, int[] argTypes) {
		if (argTypes == null) {
			return query(countSql, params, new IntegerResultSetExtractor(countSql));
		} 
		else {
			return query(countSql, params, argTypes, new IntegerResultSetExtractor(countSql));
		}
	}


	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		if (getPaginationSupport() == null) {
			initPaginationSupport();
		}
	}
	
	public PaginationSupport getPaginationSupport() {
		return paginationSupport;
	}

	public void setPaginationSupport(PaginationSupport paginationSupport) {
		this.paginationSupport = paginationSupport;
	}

	private class IntegerResultSetExtractor implements ResultSetExtractor<Integer> {
		private String sql;
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
	
}