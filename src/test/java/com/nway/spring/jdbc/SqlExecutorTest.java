package com.nway.spring.jdbc;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.builder.DefaultSqlBuilder;
import com.nway.spring.jdbc.sql.builder.SqlBuilder;
import org.springframework.util.ObjectUtils;

/**
 * 初始化测试表 可以执行initTable()方法
 * 
 * @author zdtjss@163.com
 */
public class SqlExecutorTest extends BaseTest {
	
	@Autowired
	private SqlExecutor sqlExecutor;

	@Autowired
	private SessionFactory sessionFactory;
	
	@Test
	public void testQueryForBean() {
		
		String sql = "select * from t_nway where rownum = 1";

		ExampleEntity usr = sqlExecutor.queryBean(sql, ExampleEntity.class);

		System.out.println(usr.toString());
	}

	@Test
	public void testQueryForBeanLambda() {

		Computer computer = new Computer();
		computer.setBrand("p");

		DefaultSqlBuilder builder = SQL.query(Computer.class).where().like(computer::getBrand).ge(Computer::getPrice, 1).or().ne(Computer::getPrice, 1000);

		System.out.println(builder.getSql());
		System.out.println(builder.getParam());

		Computer c = sqlExecutor.queryBean(builder);

		System.out.println(c);
	}

	@Test
	public void testQueryForBeanList() {

		String sql = "select a.*,'from' from( select * from t_nway limit 2) a";

		List<ExampleEntity> users = sqlExecutor.queryBeanList(sql, ExampleEntity.class);

		System.out.println(users);
	}

	@Test
	public void testQueryBeanPagination() {
		String sql = "select * from t_nway order by c_p_int";

		Pagination<ExampleEntity> users = sqlExecutor.queryBeanPage(sql, null, 1, 5, ExampleEntity.class);

		System.out.println(users);
	}

	@Test
	public void testQueryMapListPagination() {
		String sql = "select * from t_nway order by c_int";

		Pagination<Map<String, Object>> users = sqlExecutor.queryMapPage(sql, null, 1, 3);

		System.out.println(users);
	}

	@Test
	public void regex() {

		String input = "a order by abc1,a.ab2  ";

		Pattern ORDER_BY_PATTERN = Pattern
				.compile(".+\\p{Blank}+(ORDER|order)\\p{Blank}+(BY|by)[\\,\\p{Blank}\\w\\.]+");

		System.out.println(ORDER_BY_PATTERN.matcher(input.toLowerCase()).matches());
	}

	@Test
	public void countSql() {

		StringBuilder countSql = new StringBuilder();

		// countSql.append("select DISTINCT aaa,bbb from t_nway".toUpperCase());
		countSql.append("select top 50 PERCENT * from t_nway".toUpperCase());

		int firstFromIndex = countSql.indexOf(" FROM ");

		String selectedColumns = countSql.substring(0, firstFromIndex + 1);

		if (selectedColumns.indexOf(" DISTINCT ") == -1
				&& !selectedColumns.matches(".+TOP\\p{Blank}+\\d+\\p{Blank}+.+")) {

			countSql = countSql.delete(0, firstFromIndex).insert(0, "SELECT COUNT(1)");
		} else {

			countSql.insert(0, "SELECT COUNT(1) FROM (").append(')');
		}

		System.out.println(countSql);
	}


	@Test
	public void testUpdate() {

		String sql = "insert into t_monitor (ID,BRAND,MAX_RESOLUTION,MODEL,PHOTO,PRICE,PRODUCTION_DATE,TYPE) values (?,?,?,?,?,?,?,?)";

		sqlExecutor.getJdbcTemplate().update(sql, new Object[] { 100, "abc", "aa", "bb", null, 1.0, new Date(), 0 });

		long begin = System.currentTimeMillis();

		for (int i = 0; i < 10; i++) {

			Object[] param = new Object[] { i, "abc", "aa", "bb", null, 1.0, new Date(), 0 };

			sqlExecutor.getJdbcTemplate().update(sql, param);
		}

		System.out.println(System.currentTimeMillis() - begin);

		begin = System.currentTimeMillis();

		List<Object[]> batchArgs = new ArrayList<Object[]>();

		for (int i = 10; i < 20; i++) {

			batchArgs.add(new Object[] { i, "abc", "aa", "bb", null, 1.0, new Date(), 0 });
		}

		sqlExecutor.getJdbcTemplate().batchUpdate(sql, batchArgs);

		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void lambdaInsertTest() throws SerialException, SQLException {

		List<ExampleEntity> exampleEntityList = new ArrayList<ExampleEntity>();
		for (int i = 0; i < 10; i++) {

			ExampleEntity example = new ExampleEntity();

			DecimalFormat numberFormat = new DecimalFormat("0000000000.00000000000000");

			example.setId((int) (Math.random() * 1000));

			example.setpBoolean(1 == ((Math.random() * 10) % 2));

			example.setpByte((byte) (Math.random() * 100));

			example.setpShort((short) (Math.random() * 100));

			example.setpInt((int) (Math.random() * 1000000));

			example.setpLong((long) (Math.random() * 10000000));

			example.setpFloat(Float.parseFloat(numberFormat.format((Math.random() * 1000000000))));

			example.setpDouble(Double.parseDouble(numberFormat.format((Math.random() * 1000000000))));

			example.setpByteArr("nway-jdbc".getBytes());

			example.setwBoolean(Boolean.valueOf(1 == ((Math.random() * 10) % 2)));

			example.setwByte(Byte.valueOf((byte) (Math.random() * 100)));

			example.setwShort(Short.valueOf(((short) (Math.random() * 100))));

			example.setwInt(Integer.valueOf(((int) (Math.random() * 100000))));

			example.setwLong(Long.valueOf((long) (Math.random() * 10000000)));

			example.setwFloat(Float.valueOf(numberFormat.format((Math.random() * 1000000000))));

			example.setwDouble(Double.valueOf(numberFormat.format((Math.random() * 1000000000))));

			example.setString(UUID.randomUUID().toString());

			example.setUtilDate(new Date());

			example.setSqlDate(new java.sql.Date(System.currentTimeMillis()));

			example.setTimestamp(new Timestamp(System.currentTimeMillis()));

			example.setClob(new SerialClob("nway".toCharArray()));

			example.setBlob(new SerialBlob("nway".getBytes()));

			exampleEntityList.add(example);
		}

		SqlBuilder sqlBuilder = SQL.batchInsert(ExampleEntity.class).use(exampleEntityList);

		System.out.println(sqlExecutor.batchUpdate(sqlBuilder));
		
		listAll();
	}
	
	@Test
	public void lambdaQueryOneTest() {
		
		System.out.println(sqlExecutor.queryBean(406, ExampleEntity.class));
	}

	@Test
	public void lambdaUpdateTest() {

		ExampleEntity example = new ExampleEntity();

		example.setId(10);
		example.setUtilDate(new Date());
		
		ExampleEntity example2 = new ExampleEntity();
		
		example2.setId(11);
		example2.setUtilDate(new Date());

		SqlBuilder sqlBuilder = SQL.update(ExampleEntity.class).set(example::getUtilDate).where().eq(example::getId);

		sqlExecutor.updateById(example);
		
		sqlExecutor.batchUpdateById(Arrays.asList(example, example2));
		
		System.out.println(sqlExecutor.update(sqlBuilder));
	}

	@Test
	public void lambdaDeleteTest() {

		ExampleEntity example = new ExampleEntity();

		example.setId(0);

		SqlBuilder sqlBuilder = SQL.delete(ExampleEntity.class).where().eq(example::getId);

		sqlExecutor.deleteById(0, ExampleEntity.class);
		
		sqlExecutor.batchDeleteById(Arrays.asList(0), ExampleEntity.class);
		
		System.out.println(sqlExecutor.update(sqlBuilder));
	}

	@Test
	public void lambdaQueryTest() {

		ExampleEntity example = new ExampleEntity();
		example.setId(1);
		example.setwDouble(10.d);

		SqlBuilder sqlBuilder = SQL.query(ExampleEntity.class).where().eq(example::getId).ne(example::getpInt)
				.ge(example::getpFloat).le(example::getpDouble).lt(example::getpLong).gt(example::getpDouble)
				.between(example::getId, example::getId, example::getpLong).notBetween(example::getId, example::getId, example::getId)
				.like(example::getString).notLike(example::getString)
				.or(e -> e.eq(example::getId).eq(example::getpByte))
				.and(e -> e.eq(example::getpLong).or().eq(example::getwDouble));
		ExampleEntity ee = sqlExecutor.queryBean(sqlBuilder);

		System.out.println(ee);
		
		ee = sqlExecutor.queryBean(SQL.query(ExampleEntity.class)
				.withColumn(ExampleEntity::getwInt)
				.withColumn(ExampleEntity::getId)
				.where().eq(ExampleEntity::getId, 136));
		System.out.println(ee);
		
		ee = sqlExecutor.queryBean("select * from t_nway where pk_id = ?", ExampleEntity.class, 136);
		System.out.println(ee);
	}

	@Test
	public void lambdaQueryListTest() {

		ExampleEntity example = new ExampleEntity();
		example.setId(1);

		DefaultSqlBuilder sqlBuilder = SQL.query(ExampleEntity.class).where().eq(example::getId);
		List<ExampleEntity> ee = sqlExecutor.queryBeanList(sqlBuilder);
		
		sqlExecutor.queryBeanList(Arrays.asList(100), ExampleEntity.class);

		System.out.println(ee);
	}

	@Test
	public void lambdaQueryPageTest() {

		ExampleEntity example = new ExampleEntity();
		example.setId(10);

		DefaultSqlBuilder sqlBuilder = SQL.query(ExampleEntity.class).where().ne(example::getId);
		Pagination<ExampleEntity> ee = sqlExecutor.queryBeanPage(sqlBuilder, 1, 1);
		System.out.println(ee);
		
		sqlBuilder = SQL.query(ExampleEntity.class).where().ne(ExampleEntity::getId, 10);
		ee = sqlExecutor.queryBeanPage(sqlBuilder, 1, 1);
		System.out.println(ee);
	}
	
	@Test
	public void countTest() {
		DefaultSqlBuilder sqlBuilder = SQL.query(ExampleEntity.class).where().ne(ExampleEntity::getId, 10);
		System.out.println(sqlExecutor.count(sqlBuilder));
	}
	
	@Test
	public void listAll() {
		List<ExampleEntity> list = sqlExecutor.queryBeanList(SQL.query(ExampleEntity.class));
		System.out.println(JSONObject.toJSONString(list));
	}

	@Test
	public void toStrTest() {
		String[] obj = new String[]{"11"};
		if (ObjectUtils.isArray(obj)) {
			System.out.println(Arrays.deepToString((Object[]) obj));
		}
		System.out.println(ObjectUtils.nullSafeToString(obj));
	}

	@Test
	public void initTable() throws SQLException {

		Session session = sessionFactory.openSession();

		Transaction transaction = session.beginTransaction();

		for (int i = 0; i < 10; i++) {

			ExampleEntity example = new ExampleEntity();

			DecimalFormat numberFormat = new DecimalFormat("0000000000.00000000000000");

			example.setpBoolean(1 == ((Math.random() * 10) % 2));

			example.setpByte((byte) (Math.random() * 100));

			example.setpShort((short) (Math.random() * 100));

			example.setpInt((int) (Math.random() * 1000000));

			example.setpLong((long) (Math.random() * 10000000));

			example.setpFloat(Float.parseFloat(numberFormat.format((Math.random() * 1000000000))));

			example.setpDouble(Double.parseDouble(numberFormat.format((Math.random() * 1000000000))));

			example.setpByteArr("nway-jdbc".getBytes());

			example.setwBoolean(Boolean.valueOf(1 == ((Math.random() * 10) % 2)));

			example.setwByte(Byte.valueOf((byte) (Math.random() * 100)));

			example.setwShort(Short.valueOf(((short) (Math.random() * 100))));

			example.setwInt(Integer.valueOf(((int) (Math.random() * 100000))));

			example.setwLong(Long.valueOf((long) (Math.random() * 10000000)));

			example.setwFloat(Float.valueOf(numberFormat.format((Math.random() * 1000000000))));

			example.setwDouble(Double.valueOf(numberFormat.format((Math.random() * 1000000000))));

			example.setString(UUID.randomUUID().toString());

			example.setUtilDate(new Date());

			example.setSqlDate(new java.sql.Date(System.currentTimeMillis()));

			example.setTimestamp(new Timestamp(System.currentTimeMillis()));

			example.setClob(new SerialClob("nway".toCharArray()));

			example.setBlob(new SerialBlob("nway".getBytes()));

//			example.setInputStream(Hibernate.getLobCreator(session).createBlob("nway".getBytes()).getBinaryStream());

			session.save(example);
		}

		transaction.commit();

        listAll();
	}
}
