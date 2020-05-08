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

		SqlBuilder builder = SQL.query(ExampleEntity.class);
				builder.where()
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
				.between(ExampleEntity::getString, exampleEntity::getPpLong, exampleEntity::getPpDouble)
				.between(ExampleEntity::getPpDouble, exampleEntity.getPpDouble(), exampleEntity.getPpDouble())
				.between("p_double", 1, 2)
				.between("p_double", exampleEntity::getPpDouble, exampleEntity::getPpDouble)
				.notBetween(ExampleEntity::getPpDouble, exampleEntity::getPpLong, exampleEntity::getPpLong)
				.notBetween(ExampleEntity::getPpDouble, exampleEntity.getPpDouble(), exampleEntity.getPpDouble())
				.notBetween("p_double", 1, 2)
				.notBetween("p_double", exampleEntity::getPpDouble, exampleEntity::getPpDouble)
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
//				.groupBy(ExampleEntity::getString, ExampleEntity::getPDouble)
//				.groupBy("string", "p_double")
//				.having(e -> e.eq(exampleEntity::getId).ne(exampleEntity::getId))
//				.orderBy(ExampleEntity::getString, ExampleEntity::getPDouble)
//				.appendOrderBy("string", "p_double")
				.orderByDesc(ExampleEntity::getString, ExampleEntity::getPpDouble)
				.appendOrderByDesc("string", "p_double")
				.appendOrderBy(ExampleEntity::getString, ExampleEntity::getPpDouble)
				;

		sqlExecutor.queryBean(builder);
	}

	@Test
	public void testQueryForBeanLambda() {

		ExampleEntity computer = new ExampleEntity();
		computer.setId(1);
		computer.setString("a87ba");

		SqlBuilder builder = SQL.query(ExampleEntity.class)
				.where().like(computer::getString).ge(ExampleEntity::getPpInt, 1)
				.or().ne(ExampleEntity::getWwInt, 1000);

		ExampleEntity c = sqlExecutor.queryBean(builder);

		System.out.println(c);
	}

	@Test
	public void likeTest() {
		SqlBuilder builder = SQL.query(ExampleEntity.class).where()
				.likeLeft(ExampleEntity::getString, "%' or '1'='1");
		System.out.println(sqlExecutor.queryList(builder));
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
		for (int i = 0; i < 1; i++) {

			ExampleEntity example = new ExampleEntity();

			DecimalFormat numberFormat = new DecimalFormat("0000000000.00000000000000");

			example.setId((int) (Math.random() * 1000));

			example.setPpBoolean(1 == ((Math.random() * 10) % 2));

			example.setPpByte((byte) (Math.random() * 100));

			example.setPpShort((short) (Math.random() * 100));

			example.setPpInt((int) (Math.random() * 1000000));

			example.setPpLong((long) (Math.random() * 10000000));

			example.setPpFloat(Float.parseFloat(numberFormat.format((Math.random() * 1000000000))));

			example.setPpDouble(Double.parseDouble(numberFormat.format((Math.random() * 1000000000))));

			example.setPpByteArr("nway-jdbc".getBytes());

			example.setWwBoolean(1 == ((Math.random() * 10) % 2));

			example.setWwByte((byte) (Math.random() * 100));

			example.setWwShort(((short) (Math.random() * 100)));

//			example.setWwInt(((int) (Math.random() * 100000)));

			example.setWwLong((long) (Math.random() * 10000000));

			example.setWwFloat(Float.valueOf(numberFormat.format((Math.random() * 1000000000))));

			example.setWwDouble(Double.valueOf(numberFormat.format((Math.random() * 1000000000))));

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
		example.setWwDouble(10.d);

		SqlBuilder sqlBuilder = SQL.query(ExampleEntity.class).where().eq(example::getId).ne(example::getPpInt)
				.ge(example::getPpFloat).le(example::getPpDouble).lt(example::getPpLong).gt(example::getPpDouble)
				.between(example::getId, example::getId, example::getPpLong).notBetween(example::getId, example::getId, example::getId)
				.like(example::getString).notLike(example::getString)
				.or(e -> e.eq(example::getId).eq(example::getPpByte))
				.and(e -> e.eq(example::getPpLong).or().eq(example::getWwDouble));
		ExampleEntity ee = sqlExecutor.queryBean(sqlBuilder);

		System.out.println(ee);
		
		ee = sqlExecutor.queryBean(SQL.query(ExampleEntity.class)
				.withColumn(ExampleEntity::getWwInt)
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

		SqlBuilder sqlBuilder = SQL.query(ExampleEntity.class).where().ne(example::getId).orderByDesc(ExampleEntity::getId);
		Page<ExampleEntity> ee = sqlExecutor.queryPage(sqlBuilder, 1, 3);
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
		System.out.println(list);
	}

	@Test
	public void fillTest() {
		SqlBuilder sqlBuilder = SQL.query(ExampleEntity.class).where().ne(ExampleEntity::getId, 10);


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

			example.setPpBoolean(0 == ((Math.random() * 10000) % 2));

			example.setPpByte((byte) (Math.random() * 100));

			example.setPpShort((short) (Math.random() * 100));

			example.setPpInt((int) (Math.random() * 1000000));

			example.setPpLong((long) (Math.random() * 10000000));

			example.setPpFloat(Float.parseFloat(numberFormat.format((Math.random() * 1000000000))));

			example.setPpDouble(Double.parseDouble(numberFormat.format((Math.random() * 1000000000))));

			example.setPpByteArr("nway-jdbc".getBytes());

			example.setWwBoolean(Boolean.valueOf(1 == ((Math.random() * 10000) % 2)));

			example.setWwByte(Byte.valueOf((byte) (Math.random() * 100)));

			example.setWwShort(Short.valueOf(((short) (Math.random() * 100))));

			example.setWwInt(Integer.valueOf(((int) (Math.random() * 100000))));

			example.setWwLong(Long.valueOf((long) (Math.random() * 10000000)));

			example.setWwFloat(Float.valueOf(numberFormat.format((Math.random() * 1000000000))));

			example.setWwDouble(Double.valueOf(numberFormat.format((Math.random() * 1000000000))));

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
