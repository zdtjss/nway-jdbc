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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.builder.BatchInsertBuilder;
import com.nway.spring.jdbc.sql.builder.BatchUpdateByIdBuilder;
import com.nway.spring.jdbc.sql.builder.BeanUpdateBuilder;
import com.nway.spring.jdbc.sql.builder.DeleteBuilder;
import com.nway.spring.jdbc.sql.builder.InsertBuilder;
import com.nway.spring.jdbc.sql.builder.QueryBuilder;
import com.nway.spring.jdbc.sql.builder.SqlBuilder;

/**
 * 注意：
 * <p>
 * &nbsp;&nbsp;查询不到数据时：
 * <ul>
 * <li>queryForBean返回null；</li>
 * <li>queryForBeanList返回值size() == 0；</li>
 * <li>queryForBeanPage返回值getTotalCount() == 0；</li>
 * <li>queryForMapPage返回值getTotalCount() == 0；</li>
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

	/**
	 * 
	 * @param sqlBuilder sqlBuilder
	 * @return
	 */
	public int update(SqlBuilder sqlBuilder) {
		return super.update(sqlBuilder.getSql(), sqlBuilder.getParam().toArray());
	}
	
	/**
	 * 
	 * @param sqlBuilder sqlBuilder
	 * @return
	 */
	public int[] batchUpdate(SqlBuilder sqlBuilder) {
		List<Object> params = sqlBuilder.getParam();
		return super.batchUpdate(sqlBuilder.getSql(), params.stream().map(e -> ((Collection) e).toArray()).collect(Collectors.toList()));
	}
	
	/**
	 * 
	 * @param obj bean
	 * @return
	 */
	public int updateById(Object obj) {
		Class<?> beanClass = obj.getClass();
		BeanUpdateBuilder sqlBuilder = new BeanUpdateBuilder(obj);
		sqlBuilder.where().eq(SqlBuilderUtils.getIdName(beanClass), SqlBuilderUtils.getIdValue(beanClass, obj));
		return update(sqlBuilder);
	}
	
	/**
	 * 
	 * @param objs beans
	 * @return
	 */
	public int[] batchUpdateById(List<Object> objs) {
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
	
	public int[] batchInsert(List<Object> objs) {
		if (objs == null || objs.size() == 0) {
			return new int[] {};
		}
		BatchInsertBuilder batchInsertBuilder = new BatchInsertBuilder(objs.get(0).getClass());
		batchInsertBuilder.use(objs);
		return batchUpdate(batchInsertBuilder);
	}
	
	public <T> T queryForBeanById(Serializable id, Class<T> type) {
		SqlBuilder queryBuilder = SQL.query(type).where().eq(SqlBuilderUtils.getIdName(type), id);
		return queryForBean(queryBuilder);
	}
	
	public <T> List<T> queryForBeanListById(List<? extends Serializable> ids, Class<T> type) {
		SqlBuilder queryBuilder = SQL.query(type).in(SqlBuilderUtils.getIdName(type), ids);
		return queryForBeanList(queryBuilder);
	}
	
	/**
	 * 
	 * 
	 * @param queryBuilder lambda
	 * @return queryBuilder描述的beanClass类型的对象
	 * @throws DataAccessException 数据访问异常
	 */
	public <T> T queryForBean(SqlBuilder queryBuilder) throws DataAccessException {
		return queryForBean(queryBuilder.getSql(), queryBuilder.getBeanClass(), queryBuilder.getParam().toArray());
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
	public <T> T queryForBean(String sql, Class<T> type, Object... args) throws DataAccessException {
		return super.query(sql, new BeanHandler<T>(type), args);
	}

	/**
	 * 
	 * 
	 * @param <T>
	 * @param queryBuilder
	 * @return
	 * @throws DataAccessException 数据访问异常
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
	 * @throws DataAccessException 数据访问异常
	 */
	public <T> List<T> queryForBeanList(String sql, Class<T> type, Object... args) throws DataAccessException {
		return super.query(sql, new BeanListHandler<T>(type), args);
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
	public <T> Pagination<T> queryForBeanPage(SqlBuilder queryBuilder, int page, int pageSize)
			throws DataAccessException {
		return queryForBeanPage(queryBuilder.getSql(), queryBuilder.getParam().toArray(), page, pageSize, queryBuilder.getBeanClass());
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
	 * @throws DataAccessException 数据访问异常
	 */
	public <T> Pagination<T> queryForBeanPage(String sql, Object[] params, int page, int pageSize,
			Class<T> beanClass) throws DataAccessException {

		List<T> item = new ArrayList<T>();

		String countSql = buildPaginationCountSql(sql);
		int totalCount = queryCount(countSql, params);
		
		if (totalCount != 0) {
			String paginationSql = paginationSupport.buildPaginationSql(sql, page, pageSize);
			item = queryForBeanList(paginationSql, beanClass, params);
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
	public Pagination<Map<String, Object>> queryForMapPage(QueryBuilder queryBuilder, int page,
			int pageSize) throws DataAccessException {
		return queryForMapPage(queryBuilder.getSql(), queryBuilder.getParam().toArray(), page, pageSize);
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
	public Pagination<Map<String, Object>> queryForMapPage(String sql, Object[] params, int page,
			int pageSize) throws DataAccessException {

		List<Map<String, Object>> item = new ArrayList<>();

		String upperCaseSql = sql.toUpperCase();
		String countSql = buildPaginationCountSql(upperCaseSql);

		int totalCount = queryCount(countSql, params);

		if (totalCount != 0) {
			String paginationSql = paginationSupport.buildPaginationSql(upperCaseSql, page, pageSize);
			item = queryForList(paginationSql, params);
		}
		return new Pagination<Map<String, Object>>(item, totalCount, page, pageSize);
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
		String databaseProductName = "";
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
	
	private int queryCount(String countSql, Object[] params) {
		return query(countSql, params, new IntegerResultSetExtractor(countSql));
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