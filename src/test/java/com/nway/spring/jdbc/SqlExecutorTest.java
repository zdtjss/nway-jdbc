package com.nway.spring.jdbc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nway.spring.jdbc.pagination.Page;
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.builder.SqlBuilder;
import com.nway.spring.jdbc.sql.builder.ISqlBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

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
	public void sqlTest() {

		ExampleEntity exampleEntity = new ExampleEntity();

		exampleEntity.setString("strcolumn");

		SqlBuilder builder = SQL.query(ExampleEntity.class).where()
				.eq(ExampleEntity::getId, 1)
				.eq(exampleEntity::getId)
				.eq("pk_id", exampleEntity.getId())
				.ne(ExampleEntity::getString, "ne")
				.ne(exampleEntity::getString)
				.ne("string", "str")
				.ge(ExampleEntity::getId, 11)
				.ge(exampleEntity::getId)
				.ge("pk_id", exampleEntity.getId())
				.gt(ExampleEntity::getId, 11)
				.gt(exampleEntity::getId)
				.gt("pk_id", exampleEntity.getId())
				.lt(ExampleEntity::getId, 11)
				.lt(exampleEntity::getId)
				.lt("pk_id", exampleEntity.getId())
				.le(ExampleEntity::getId, 11)
				.le(exampleEntity::getId)
				.le("pk_id", exampleEntity.getId())
				.between(ExampleEntity::getString, exampleEntity::getpLong, exampleEntity::getpDouble)
				.between(ExampleEntity::getpDouble, exampleEntity.getpDouble(), exampleEntity.getpDouble())
				.between("p_double", 1, 2)
				.between("p_double", exampleEntity::getpDouble, exampleEntity::getpDouble)
				.notBetween(ExampleEntity::getpDouble, exampleEntity::getpLong, exampleEntity::getpLong)
				.notBetween(ExampleEntity::getpDouble, exampleEntity.getpDouble(), exampleEntity.getpDouble())
				.notBetween("p_double", 1, 2)
				.notBetween("p_double", exampleEntity::getpDouble, exampleEntity::getpDouble)
				.like(exampleEntity::getString)
				.like("string", exampleEntity.getString())
				.like(ExampleEntity::getString, exampleEntity::getString)
				.like(ExampleEntity::getString, exampleEntity.getString())
				.notLike(exampleEntity::getString)
				.notLike("string", exampleEntity.getString())
				.notLike(ExampleEntity::getString, exampleEntity::getString)
				.notLike(ExampleEntity::getString, exampleEntity.getString())
				.likeLeft(exampleEntity::getString)
				.likeLeft("string", exampleEntity.getString())
				.likeLeft(ExampleEntity::getString, exampleEntity::getString)
				.likeLeft(ExampleEntity::getString, exampleEntity.getString())
				.likeRight(exampleEntity::getString)
				.likeRight("string", exampleEntity.getString())
				.likeRight(ExampleEntity::getString, exampleEntity::getString)
				.likeRight(ExampleEntity::getString, exampleEntity.getString())
				.in(ExampleEntity::getString, Collections.singletonList("aa"))
				.in("string", Collections.singletonList("aa"))
				.notIn(ExampleEntity::getString, Collections.singletonList("aa"))
				.notIn("string", Collections.singletonList("aa"))
				.or()
				.eq(exampleEntity::getId)
				.or(e -> e.eq(exampleEntity::getId).ne(exampleEntity::getId))
//				.groupBy(ExampleEntity::getString, ExampleEntity::getpDouble)
//				.groupBy("string", "p_double")
//				.having(e -> e.eq(exampleEntity::getId).ne(exampleEntity::getId))
//				.orderBy(ExampleEntity::getString, ExampleEntity::getpDouble)
//				.appendOrderBy("string", "p_double")
				.orderByDesc(ExampleEntity::getString, ExampleEntity::getpDouble)
				.appendOrderByDesc("string", "p_double")
				.appendOrderBy(ExampleEntity::getString, ExampleEntity::getpDouble)
				;

		sqlExecutor.queryBean(builder);
	}

	@Test
	public void testQueryForBeanLambda() {

		ExampleEntity computer = new ExampleEntity();
		computer.setId(1);
		computer.setString("a87ba");

		SqlBuilder builder = SQL.query(ExampleEntity.class)
				.where().like(computer::getString).ge(ExampleEntity::getpInt, 1)
				.or().ne(ExampleEntity::getwInt, 1000);

		ExampleEntity c = sqlExecutor.queryBean(builder);

		System.out.println(c);
	}

	@Test
	public void likeTest() {
		SqlBuilder builder = SQL.query(ExampleEntity.class).where()
				.likeLeft(ExampleEntity::getString, "2f79");
		System.out.println(sqlExecutor.queryBean(builder).toString());
	}

	@Test
	public void inTest() {
		SqlBuilder builder = SQL.query(ExampleEntity.class).where()
				.notIn(ExampleEntity::getId, Arrays.asList(1, 3));
		sqlExecutor.queryList(builder).forEach(e -> System.out.println(e));
	}

	@Test
	public void testQueryForBeanList() {

		String sql = "select a.*,'from' from( select * from t_nway limit 2) a";

		List<ExampleEntity> users = sqlExecutor.queryList(sql, ExampleEntity.class);

		System.out.println(users);
	}

	@Test
	public void testQueryBeanPagination() {
		String sql = "select * from t_nway order by c_p_int";

		Page<ExampleEntity> users = sqlExecutor.queryPage(sql, null, 1, 5, ExampleEntity.class);

		System.out.println(users);
	}

	@Test
	public void testQueryMapListPagination() {
		String sql = "select * from t_nway order by c_int";

		Page<Map<String, Object>> users = sqlExecutor.queryPage(sql, null, 1, 3);

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

		ISqlBuilder ISqlBuilder = SQL.batchInsert(ExampleEntity.class).use(exampleEntityList);

		System.out.println(sqlExecutor.batchUpdate(ISqlBuilder));
		
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

		ISqlBuilder ISqlBuilder = SQL.update(ExampleEntity.class).set(example::getUtilDate).where().eq(example::getId);

		sqlExecutor.updateById(example);
		
		sqlExecutor.batchUpdateById(Arrays.asList(example, example2));
		
		System.out.println(sqlExecutor.update(ISqlBuilder));
	}

	@Test
	public void lambdaDeleteTest() {

		ExampleEntity example = new ExampleEntity();

		example.setId(0);

		ISqlBuilder ISqlBuilder = SQL.delete(ExampleEntity.class).where().eq(example::getId);

		sqlExecutor.deleteById(0, ExampleEntity.class);
		
		sqlExecutor.batchDeleteById(Arrays.asList(0), ExampleEntity.class);
		
		System.out.println(sqlExecutor.update(ISqlBuilder));
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

		SqlBuilder sqlBuilder = SQL.query(ExampleEntity.class).where().eq(example::getId);
		List<ExampleEntity> ee = sqlExecutor.queryList(sqlBuilder);
		
		sqlExecutor.queryList(Arrays.asList(100), ExampleEntity.class);

		System.out.println(ee);
	}

	@Test
	public void lambdaQueryPageTest() {

		ExampleEntity example = new ExampleEntity();
		example.setId(10);

		SqlBuilder sqlBuilder = SQL.query(ExampleEntity.class).where().ne(example::getId);
		Page<ExampleEntity> ee = sqlExecutor.queryPage(sqlBuilder, 1, 2);
		System.out.println(ee);
		
		sqlBuilder = SQL.query(ExampleEntity.class).where().ne(ExampleEntity::getId, 10);
		ee = sqlExecutor.queryPage(sqlBuilder, 1, 10);
		System.out.println(ee);
	}
	
	@Test
	public void countTest() {
		SqlBuilder sqlBuilder = SQL.query(ExampleEntity.class).where().ne(ExampleEntity::getId, 10);
		System.out.println(sqlExecutor.count(sqlBuilder));
	}
	
	@Test
	public void listAll() {
		List<ExampleEntity> list = sqlExecutor.queryList(SQL.query(ExampleEntity.class));
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

			example.setpBoolean(0 == ((Math.random() * 10000) % 2));

			example.setpByte((byte) (Math.random() * 100));

			example.setpShort((short) (Math.random() * 100));

			example.setpInt((int) (Math.random() * 1000000));

			example.setpLong((long) (Math.random() * 10000000));

			example.setpFloat(Float.parseFloat(numberFormat.format((Math.random() * 1000000000))));

			example.setpDouble(Double.parseDouble(numberFormat.format((Math.random() * 1000000000))));

			example.setpByteArr("nway-jdbc".getBytes());

			example.setwBoolean(Boolean.valueOf(1 == ((Math.random() * 10000) % 2)));

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

			example.setBigDecimal(BigDecimal.valueOf(Math.random() * 10000));
			example.setLocalDate(LocalDate.now());
			example.setLocalDateTime(LocalDateTime.now());

//			example.setInputStream(Hibernate.getLobCreator(session).createBlob("nway".getBytes()).getBinaryStream());

			session.save(example);
		}

		transaction.commit();

        listAll();
	}
}
