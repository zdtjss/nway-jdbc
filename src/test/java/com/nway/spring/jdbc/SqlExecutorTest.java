package com.nway.spring.jdbc;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;

import com.nway.spring.jdbc.bean.BeanListHandler;
import com.nway.spring.jdbc.json.JsonListHandler;
import com.nway.spring.jdbc.performance.entity.Monitor;

//import oracle.jdbc.OracleCallableStatement;
//import oracle.jdbc.OracleTypes;

/**
 * 初始化测试表 可以执行initTable()方法
 * 
 * @author zdtjss@163.com
 *
 */
public class SqlExecutorTest extends BaseTest
{
	@Autowired
	private SqlExecutor sqlExecutor;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@Test
	public void testQueryForBean()
	{
		String sql = "select * from t_nway where rownum = 1";
		
		ExampleEntity usr = sqlExecutor.queryForBean(sql, ExampleEntity.class);

		System.out.println(usr.toString());
	}

	@Test
	public void testQueryForBeanList() {
		
		String sql = "select a.*,'from' from( select * from t_nway limit 2) a";

		List<ExampleEntity> users = sqlExecutor.queryForBeanList(sql, ExampleEntity.class);

		System.out.println(users);
	}

	@Test
	public void testQueryBeanPagination()
	{
		String sql = "select * from t_nway order by c_p_int";

		Pagination<ExampleEntity> users = sqlExecutor.queryForBeanPagination(sql, null, 1, 5, ExampleEntity.class);

		System.out.println(users);
	}

	@Test
	public void testQueryMapListPagination()
	{
		String sql = "select * from t_nway order by c_int";

		Pagination<Map<String, Object>> users = sqlExecutor.queryForMapListPagination(sql,null, 1, 3);

		System.out.println(users);
	}

	@Test
	public void regex() {

		String input = "a order by abc1,a.ab2  ";

		Pattern ORDER_BY_PATTERN = Pattern
				.compile(".+\\p{Blank}+ORDER\\p{Blank}+BY[\\,\\p{Blank}\\w\\.]+");

		System.out.println(ORDER_BY_PATTERN.matcher(input.toUpperCase()).matches());
	}
	
	@Test
	public void countSql() {
		
		StringBuilder countSql = new StringBuilder();
		
		//countSql.append("select DISTINCT aaa,bbb from t_nway".toUpperCase());
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
	public void testJson()
	{
		String sql = "select * from t_nway where rownum = 1";
		
		String json = sqlExecutor.queryForJson(sql, ExampleEntity.class);
		
		System.out.println(json);
	}
	
	@Test
	public void testJsonList() {
		
		String sql = "select * from t_nway";

		String json = sqlExecutor.queryForJsonList(sql, ExampleEntity.class);

		System.out.println(json);
	}
	
	@Test
	public void testJsonListNRC() {
	    
	    String sql = "select * from t_nway";
	    
	    String json = sqlExecutor.queryForJsonList(sql);
	    
	    System.out.println(sqlExecutor.queryForJsonList(sql));
	    
	    System.out.println(json);
	}
	
	@Test
	public void testJsonPaginationNRC()
	{
		String sql = "select * from t_nway order by p_int";

		String json = sqlExecutor.queryForJsonPagination(sql, null, 1, 5);

		System.out.println(json);
	}
	
	@Test
	public void testJsonPagination()
	{
	    String sql = "select * from t_nway order by p_int";
	    
	    String json = sqlExecutor.queryForJsonPagination(sql, null, 1, 5, ExampleEntity.class);
	    
	    System.out.println(json);
	}
	
	/*@Test
	public void testCallForBeanList() {
	    
	    List<Monitor> monitors = sqlExecutor.execute("{? = call list_monitor2(?)}", new CallableStatementCallback<List<Monitor>>()
        {
            @Override
            public List<Monitor> doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException
            {

                OracleCallableStatement ocs = (OracleCallableStatement) cs;
                
                ocs.setInt(2, 102);
                
                ocs.registerOutParameter(1, OracleTypes.CURSOR);
                
                ocs.execute();
                
                BeanListHandler<Monitor> blh = new BeanListHandler<Monitor>(Monitor.class, "list_monitor2");
                
                return blh.extractData(ocs.getCursor(1));
            }
        });
	    
	    System.out.println(Arrays.toString(monitors.toArray()));
	}
	
	@Test
	public void testCallForJsonList() {
	    
	    String monitors = sqlExecutor.execute("{? = call list_monitor2(?)}", new CallableStatementCallback<String>()
	    {
	        @Override
	        public String doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException
	        {
	            
	            OracleCallableStatement ocs = (OracleCallableStatement) cs;
	            
	            ocs.setInt(2, 102);
	            
	            ocs.registerOutParameter(1, OracleTypes.CURSOR);
	            
	            ocs.execute();
	            
	            // 返回无对应Java类的json 使用JsonListHandlerNRC
	            JsonListHandler blh = new JsonListHandler(Monitor.class, "list_monitor2");
	            
	            return blh.extractData(ocs.getCursor(1));
	        }
	    });
	    
	    System.out.println(monitors);
	}*/
	
	@Test
	public void testUpdate() {
		
		String sql = "insert into t_monitor (ID,BRAND,MAX_RESOLUTION,MODEL,PHOTO,PRICE,PRODUCTION_DATE,TYPE) values (?,?,?,?,?,?,?,?)";
		
		sqlExecutor.update(sql, new Object[] { 100, "abc", "aa", "bb",null, 1.0, new Date(), 0 });
		
		long begin = System.currentTimeMillis();
		
		for (int i = 0; i < 10; i++) {

			Object[] param = new Object[] { i, "abc", "aa", "bb",null, 1.0, new Date(), 0 };

			sqlExecutor.update(sql, param);
		}
		
		System.out.println(System.currentTimeMillis() - begin);
		
		begin = System.currentTimeMillis();
		
		List<Object[]> batchArgs = new ArrayList<>();
		
		for (int i = 10; i < 20; i++) {

			batchArgs.add(new Object[] { i, "abc", "aa","bb", null, 1.0, new Date(), 0 });
		}
		
		sqlExecutor.batchUpdate(sql, batchArgs);
		
		System.out.println(System.currentTimeMillis() - begin);
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
	}
}
