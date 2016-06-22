package com.nway.spring.jdbc;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
		
		String sql = "select * from t_nway where rownum < 10";

		String json = sqlExecutor.queryForJsonList(sql, ExampleEntity.class);

		System.out.println(json);
	}
	
	@Test
	public void testJsonPagination()
	{
		String sql = "select * from t_nway order by c_p_int";

		String json = sqlExecutor.queryForJsonPagination(sql, null, 1, 5, ExampleEntity.class);

		System.out.println(json);
	}
	
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

		SessionFactory sessionFactory = null;
		
		try {

			Configuration cfg = new Configuration().configure();

			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = cfg.buildSessionFactory(
					new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build());
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		
		Session session = sessionFactory.openSession();
		
		Transaction transaction = session.beginTransaction();
		
		for (int i = 0; i < 100; i++) {
			
			ExampleEntity example = new ExampleEntity();

			DecimalFormat numberFormat = new DecimalFormat("0000000000.00");

			example.setId((int) (Math.random() * 10) % 2);

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
			
			example.setClob(Hibernate.getLobCreator(session).createClob("nway"));
			
			example.setBlob(Hibernate.getLobCreator(session).createBlob("nway".getBytes()));
			
//			example.setInputStream(Hibernate.getLobCreator(session).createBlob("nway".getBytes()).getBinaryStream());
		
			session.save(example);
		}
		
		transaction.commit();
	}
	
	@Test
	public void init() {
	    
	    String t_xsgz_ndjh_0525 = "insert into T_XSGZ_NDJH_0525_PART (fid,jhmc,jhnr,jhnd,isxf,xfsj,xfr,createdtm,createuser,updatedtm,updateuser,fsysid) values(?,?,?,?,?,?,?,?,?,?,?,?)";
	    
	    String t_xsgz_rwjh_0525 = "insert into T_XSGZ_RWJH_0525_PART (fid,NDJHFID,RWJHBH,RWJHMC,XSZID,XSZZID,BXSDWDM,QKLY,XSKSRQ,XSJSRQ,JHZW,JHZT,JHSBR,JHSBSJ,JHXFR,JHXFSJ,CREATEDTM,CREATEUSER,UPDATEDTM,UPDATEUSER,FSYSID) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	    
	    List<Object[]> ndjhPs = new ArrayList<>();
	    
        for (int i = 0; i < 50001; i++)
        {
            Object[] ndjh = new Object[12];
            //FID
            ndjh[0] = UUID.randomUUID().toString(); 
            // JHMC
            ndjh[1] = UUID.randomUUID().toString();
            // JHNR  
            ndjh[2] = UUID.randomUUID().toString();
            //JHND
            ndjh[3] = UUID.randomUUID().toString();
            //ISXF
            ndjh[4] = Math.round(Math.random() * 10) % 2;
            // XFSJ
            ndjh[5] = new Date();
            // XFR
            ndjh[6] = UUID.randomUUID().toString();
             //CREATEDTM
            ndjh[7] = new Date();
            // CREATEUSER
            ndjh[8] = UUID.randomUUID().toString();
            // CREATEUSER
            ndjh[9] = new Date();
            // UPDATEDTM
            ndjh[10] =  UUID.randomUUID().toString();
            
            ndjh[11] = System.currentTimeMillis() - Math.round(Math.random() * 100);
            
            ndjhPs.add(ndjh);
        }
        
        List<Object[]> rwjhPs = new ArrayList<>();
        
        for (int i = 0; i < 50001; i++)
        {
            Object[] rwjh = new Object[21];
            //FID
            rwjh[0] = UUID.randomUUID().toString(); 
            // NDJHFID
            rwjh[1] = UUID.randomUUID().toString();
            // RWJHBH  
            rwjh[2] = UUID.randomUUID().toString();
            //RWJHMC
            rwjh[3] = UUID.randomUUID().toString();
            //XSZID
            rwjh[4] = UUID.randomUUID().toString();
            // XSZZID
            rwjh[5] = UUID.randomUUID().toString();
            // BXSDWDM
            rwjh[6] = UUID.randomUUID().toString();
            //QKLY
            rwjh[7] = UUID.randomUUID().toString();
            //XSKSRQ
            rwjh[8] = new Date();
            // XSJSRQ
            rwjh[9] = new Date();
            // JHZW
            rwjh[10] =  UUID.randomUUID().toString();
            //JHZT
            rwjh[11] = Math.round(Math.random()) % 2 + 1;
          //JHSBR
            rwjh[12] = UUID.randomUUID().toString(); 
            // JHSBSJ
            rwjh[13] = new Date();
            // JHXFR  
            rwjh[14] = UUID.randomUUID().toString();
            //JHXFSJ
            rwjh[15] = new Date();
            //CREATEDTM     
            rwjh[16] = new Date();
            // CREATEUSER
            rwjh[17] = UUID.randomUUID().toString();
            // UPDATEDTM
            rwjh[18] = new Date();
            //UPDATEUSER
            rwjh[19] = UUID.randomUUID().toString();
            //FSYSID
            rwjh[20] = System.currentTimeMillis() - Math.round(Math.random() * 100);
            
            rwjhPs.add(rwjh);
        }
        
        sqlExecutor.batchUpdate(t_xsgz_ndjh_0525, ndjhPs);
        sqlExecutor.batchUpdate(t_xsgz_rwjh_0525, rwjhPs);
	}
	
	@Test
	public void count() {
	    
	    String sql = "select sum(pre_count)  ss\n" +
                "  from (select /*+parallel(t_xsgz_ndjh_0525, 5)*/ count(fid) pre_count\n" + 
                "          from t_xsgz_ndjh_0525\n" + 
                "         where isxf = 0 AND FSYSID LIKE '%1464340814%'\n" + 
                "        union all\n" + 
                "        select /*+parallel(t_xsgz_ndjh_0525, 5)*/ count(fid) pre_count\n" + 
                "          from t_xsgz_rwjh_0525\n" + 
                "         where jhzt = 2 AND FSYSID LIKE '%1464340814%')";
	    
	    String sql2 = "select sum(pre_count) ss\n" +
	                    "  from (select /*+parallel(t_xsgz_ndjh_0525_bak, 5)*/ count(fid) pre_count\n" + 
	                    "          from t_xsgz_ndjh_0525_bak\n" + 
	                    "         where isxf = 0 AND FSYSID LIKE '%1464340814%'\n" + 
	                    "        union all\n" + 
	                    "        select /*+parallel(t_xsgz_rwjh_0525_bak, 5)*/ count(fid) pre_count\n" + 
	                    "          from t_xsgz_rwjh_0525_bak\n" + 
	                    "         where jhzt = 2 AND FSYSID LIKE '%1464340814%')";
	    
	    String sql3 = "select sum(pre_count)\n" +
	            "  from (select count(fid) pre_count\n" + 
	            "          from T_XSGZ_NDJH_0525_PART\n" + 
	            "         where isxf = '0'\n" + 
	            "        union all\n" + 
	            "        select count(fid) pre_count\n" + 
	            "          from T_XSGZ_RWJH_0525_PART\n" + 
	            "         where jhzt = '2')";

	    //sqlExecutor.queryForList(sql);
	    
	    sqlExecutor.queryForList(sql2);
	    
	    //sqlExecutor.queryForList(sql3);
	    
	    long begin = System.currentTimeMillis();
	    System.out.println(sqlExecutor.queryForList(sql).get(0).get("ss"));
	    System.out.println(System.currentTimeMillis() - begin);
	    
	    begin = System.currentTimeMillis();
	    System.out.println(sqlExecutor.queryForList(sql).get(0).get("ss"));
	    System.out.println(System.currentTimeMillis() - begin);
	    
	    begin = System.currentTimeMillis();
	    System.out.println(sqlExecutor.queryForList(sql).get(0).get("ss"));
	    System.out.println(System.currentTimeMillis() - begin);
	    
	    System.out.println();
	    
	    begin = System.currentTimeMillis();
	    System.out.println(sqlExecutor.queryForList(sql2).get(0).get("ss"));
	    System.out.println(System.currentTimeMillis() - begin);
	    
	    begin = System.currentTimeMillis();
	    System.out.println(sqlExecutor.queryForList(sql2).get(0).get("ss"));
	    System.out.println(System.currentTimeMillis() - begin);
	    
	    begin = System.currentTimeMillis();
	    System.out.println(sqlExecutor.queryForList(sql2).get(0).get("ss"));
	    System.out.println(System.currentTimeMillis() - begin);
	    
	    System.out.println();
        
        /*begin = System.currentTimeMillis();
        sqlExecutor.queryForList(sql3);
        System.out.println(System.currentTimeMillis() - begin);
        
        begin = System.currentTimeMillis();
        sqlExecutor.queryForList(sql3);
        System.out.println(System.currentTimeMillis() - begin);
        
        begin = System.currentTimeMillis();
        sqlExecutor.queryForList(sql3);
        System.out.println(System.currentTimeMillis() - begin);*/
	}
	
	@Test
	public void initzxgl() {
	    
	    String dwdmSql = "select dwdm from t_sys_org where dwdm like '4100003%'";
	    String useridSql = "select fuserid from fbs_users";
	    String jcdSql = "select fid from T_XZGL_JCD";
	    
	    String ycjcsjSql = "insert into T_XZGL_YCJCSJ (FID,ASJBH,BADWDM,CBMJID,JCDID,BJNR,JLBH,JCBH,YCSJFSSJ,DJRYID,JCSJ,CLDWDM,ISWT,YQFKRQ,JCNR,BMLDYJ,DWLDYJ,BZ,DBZT,STATE,ISZB,YYCSJID,CREATEBYUSER,CREATEDTM,UPDATEBYUSER,UPDATEDTM,FSYSID,JCDCLDWDM,ISQS,GBYY,GBR,GBSJ,FSJYJ,FZRYJ,NQYJ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	    
	    String ycfkSql = "insert into T_XZGL_YCFK(FSYSID,ISFK,FKRYXM,FID,YCJCSJID,FKDWDM,ISWT,FKNR,SJMJID,CLJG,BMLDYJ,DWLDYJ,BZ,FKSJ,CREATEBYUSER,CREATEDTM,UPDATEBYUSER,UPDATEDTM) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	    
	    List<String> dwdmList = sqlExecutor.queryForList(dwdmSql, String.class);
	    
	    List<String> useridList = sqlExecutor.queryForList(useridSql, String.class);
	    
	    List<String> jcdList = sqlExecutor.queryForList(jcdSql, String.class);
	    
	    List<Object[]> ycjcsParams = new ArrayList<>();
	    List<Object[]> ycfkParams = new ArrayList<>();
	    
        for (int i = 0; i < 50000; i++)
        {
            Object[] ycjcsjParam = new Object[35];
            
            ycjcsjParam[0] = UUID.randomUUID().toString();
            // ASJBH 案事件编号
            ycjcsjParam[1] = randomInteger(1000000000);
            // BADWDM 办案单位代码
            ycjcsjParam[2] = randomDwdm(dwdmList);
            // CBMJID 承办民警ID
            ycjcsjParam[3] = randomUserid(useridList);
            // JCDID 监测点ID
            ycjcsjParam[4] = randomJcd(jcdList);
            // BJNR  报警内容
            ycjcsjParam[5] = UUID.randomUUID().toString();
            // JLBH 记录编号
            ycjcsjParam[6] = UUID.randomUUID().toString();
            // JCBH 检查编号
            ycjcsjParam[7] = UUID.randomUUID().toString();
            // YCSJFSSJ 异常数据发生时间
            ycjcsjParam[8] = new Date();
            // DJRYID 登记人员ID
            ycjcsjParam[9] = randomUserid(useridList);
            // JCSJ 检查时间
            ycjcsjParam[10] = new Date();
            // CLDWDM 处理单位代码
            ycjcsjParam[11] = randomDwdm(dwdmList);
            // ISWT 是否存在问题(0 否 1 是)
            ycjcsjParam[12] = randomInteger(10) % 2;
            // YQFKRQ 要求反馈日期
            ycjcsjParam[13] = new Date();
            //JCNR 检查内容
            ycjcsjParam[14] = UUID.randomUUID().toString();
            // BMLDYJ  主任意见
            ycjcsjParam[15] = UUID.randomUUID().toString();
            // DWLDYJ 书记意见
            ycjcsjParam[16] = UUID.randomUUID().toString();
            // BZ 备注
            ycjcsjParam[17] = UUID.randomUUID().toString();
            // DBZT 督办状态（0未督办 1已督办  默认0）
            ycjcsjParam[18] = randomInteger(10) % 2;
            // STATE 数据状态（0:未处理 1：已下发未反馈 2：已反馈未审核 3：已处理  4：无需处理）
            ycjcsjParam[19] = ruandomLess(4);
            // ISZB 是否转办(0否 1是 默认0)
            ycjcsjParam[20] = randomInteger(10) % 2;
            // YYCSJID 转办所属检测ID(转办生成新的异常数据，此ID为源异常数据ID)
            ycjcsjParam[21] = UUID.randomUUID().toString();
            // CREATEBYUSER 
            ycjcsjParam[22] = randomUserid(useridList);
            // CREATEDTM
            ycjcsjParam[23] = new Date();
            // UPDATEBYUSER 
            ycjcsjParam[24] = randomUserid(useridList);
            // UPDATEDTM
            ycjcsjParam[25] = new Date();
            // FSYSID
            ycjcsjParam[26] = "4100";
            // JCDCLDWDM 监测点处理单位代码
            ycjcsjParam[27] = randomDwdm(dwdmList);
            // ISQS 是否签收 是否签收 0：默认 1：签收， 2：不签收
            ycjcsjParam[28] = ruandomLess(2);
            // GBYY  关闭原因
            ycjcsjParam[29] = UUID.randomUUID().toString();
            // GBR 关闭人
            ycjcsjParam[30] = randomUserid(useridList);
            // GBSJ 关闭时间
            ycjcsjParam[31] = new Date();
            // FSJYJ 副书记意见
            ycjcsjParam[32] = UUID.randomUUID().toString();
            // FZRYJ 副主任意见
            ycjcsjParam[33] = UUID.randomUUID().toString();
            // NQYJ 内勤意见
            ycjcsjParam[34] = UUID.randomUUID().toString();
            
            ycjcsParams.add(ycjcsjParam);
            
            Object[] ycfkParam = new Object[18];
            // FSYSID
            ycfkParam[0] = "4100";
            // ISFK 是否反馈
            ycfkParam[1] = randomInteger(10) % 2;
            // FKRYXM
            ycfkParam[2] = UUID.randomUUID().toString();
            // FID
            ycfkParam[3] = UUID.randomUUID().toString();
            // YCJCSJID 异常数据ID
            ycfkParam[4] = ycjcsjParam[0];
            // FKDWDM 反馈单位代码
            ycfkParam[5] = randomDwdm(dwdmList);
            // ISWT 是否问题
            ycfkParam[6] = randomInteger(10) % 2;
            // FKNR 反馈内容
            ycfkParam[7] = UUID.randomUUID().toString();
            // SJMJID 涉及民警ID
            ycfkParam[8] = randomUserid(useridList);
            // CLJG 处理结果
            ycfkParam[9] = UUID.randomUUID().toString();
            // BMLDYJ 部门领导意见
            ycfkParam[10] = UUID.randomUUID().toString();
            // DWLDYJ 单位领导意见
            ycfkParam[11] = UUID.randomUUID().toString();
            // BZ 备注
            ycfkParam[12] = UUID.randomUUID().toString();
            // FKSJ 反馈时间
            ycfkParam[13] = new Date();
            // CREATEBYUSER 
            ycfkParam[14] = randomUserid(useridList);
            // CREATEDTM
            ycfkParam[15] = new Date();
            // UPDATEBYUSER 
            ycfkParam[16] = randomUserid(useridList);
            // UPDATEDTM
            ycfkParam[17] = new Date();
            
            ycfkParams.add(ycfkParam);
        }
        
        sqlExecutor.batchUpdate(ycjcsjSql, ycjcsParams);
        
        sqlExecutor.batchUpdate(ycfkSql, ycfkParams);
	}
	
	private long randomInteger(int precision) {
	    
	    return Math.round(Math.random() * precision);
	}
	
	private int ruandomLess(int lessThan) {
	    
	   long rd = randomInteger(10);
	   
	   if(rd > lessThan) {
	       ruandomLess(lessThan);
	   }
	   
       return (int) rd;
	}
	
    private String randomDwdm(List<String> dwdmList)
    {
        try
        {
            return dwdmList.get((int) randomInteger(10));
        }
        catch (IndexOutOfBoundsException e)
        {
            return randomDwdm(dwdmList);
        }
    }
    
    private String randomUserid(List<String> useridList)
    {
        try
        {
            return useridList.get((int) randomInteger(10));
        }
        catch (IndexOutOfBoundsException e)
        {
            return randomDwdm(useridList);
        }
    }
    
    private String randomJcd(List<String> jcdList)
    {
        try
        {
            return jcdList.get((int) randomInteger(10));
        }
        catch (IndexOutOfBoundsException e)
        {
            return randomDwdm(jcdList);
        }
    }
}
