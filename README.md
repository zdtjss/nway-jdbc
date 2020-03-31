#Nway-JDBC扩展自Spring的JdbcTemplate，完全兼容Spring JDBC。提供了方便简单的单表操作方法，支持和自动填充数据和数据权限控制，只需要给主类SqlExecutor配置好数据源，就没有其他需要配置的了，不需要继承某个类，也不需要实现哪个接口。

#如遇问题可联系zdtjss@163.com或QQ:670950251。

#镜像地址：https://gitee.com/nway/Nway-JDBC。


	<dependency>
		<groupId>com.github.zdtjss</groupId>
		<artifactId>nway-jdbc</artifactId>
		<version>1.1.4</version>
	</dependency>

#使用

增、改、删：  

	SqlBuilder builder = SQL.insert(Class beanClass);
	SqlBuilder builder = SQL.delete(Class beanClass);
	SqlBuilder builder = SQL.update(Class beanClass);
	
	int effectCount = sqlExecutor.update(builder);
    
单对象查询：
	
        各种参数的queryForBean方法支持单个Java对象查询
		
		SqlBuilder builder = SQL.query(User.class).where().eq(usrQuery::getStatus).like(usrQuery::getName);
        User user = sqlExecutor.queryForBean(builder);
        User user = sqlExecutor.queryForBeanById(100, User.class);
        
        or
        
        User usr = sqlExecutor.queryForBean("select * from t_user where user_name like ? and status = ?", User.class, "abc", 1);
        
对象集查询：
	
        各种参数的queryForBeanList方法支持集合对象查询
        
        SqlBuilder builder = SQL.query(User.class).where().eq(usrQuery::getStatus).like(usrQuery::getName);
        List<User> users = sqlExecutor.queryForBeanList(builder);
        List<User> users = sqlExecutor.queryForBeanListById(Arrays.asList(100), User.class);
        
        or
		
        List<User> users = sqlExecutor.queryForBeanList("select * from t_user where user_name like ? and status = ?", User.class, 10000);
		
分页对象集查询：
	
        各种参数的queryForBeanPagination方法支持Java对象分页查询
		
		SqlBuilder builder = SQL.query(User.class).where().eq(usrQuery::getStatus).like(usrQuery::getName).orderBy(usrQuery::getId);
        Pagination<User> users = sqlExecutor.queryForBeanPage(builder, 1, 10);
        
        or
        
        Pagination<User> users = sqlExecutor.queryForBeanPage("select * from t_user where user_name like ? and status = ? order by id", new Object[]{ "abc", 1 }, 1, 10, User.class);
		
        //页面数据 List<T>
        users.getPageData();
		
        //页面数据条数
        users.getPageSize();
		
        //总数据条数
        users.getTotalCount()
		
        //页面大小
        users.getPageSize();
		
        //页数
        users.getPageCount();
		
        //当前页码
        users.getCurrentPage();
		
Map对象集分页：	
	各种参数的queryForMapListPagination方法支持Map对象分页查询
		
        Pagination<Map<String, Object>> users = sqlExecutor.queryForMapPage("select * from user_name where id <> ? order by id", new Object[]{ "abc", 1 }, 1, 10);
        users使用同queryForBeanPage
		分页默认支持Oracle、Mysql、MariaDB，关于其他数据库的分页可以实现com.nway.spring.jdbc.PaginationSupport接口，通过com.nway.spring.jdbc.SqlExecutor.setPaginationSupport方法引入。

自动填充数据和数据权限支持：

	使用场景： 
		1、数据权限可用于数据查询时自动限制可返回的数据
		2、自动填充功能可应用于统一处理数据的“创建人”、“修改人”、“创建时间”、“修改时间“等
	使用并发：
		查询对象对应属性加入注解 Column(fillStrategy = TestFillStrategy.class, permissionStrategy = TestPermissionStrategy.class) 
		fillStrategy取值需要实现FillStrategy接口，permissionStrategy取值需要实现PermissionStrategy接口，程序在插入或更新时将自动从fillStrategy中取值，在查询时会根据permissionStrategy自动补充where条件

#数据库表与Java类的映射规则：  
   
   表名：
   @Table(name = "t_user")  
   
   字段：
   类属性和表列默认的对应规则是：删除下划线后首字符大写。表字段user_name对应属性userName，会通过类中setUserName方法赋值。
    也可以通过com.nway.spring.jdbc.annotation.Column修改默认规则。
    例：
   
      import com.nway.spring.jdbc.annotation.Table;
      import com.nway.spring.jdbc.annotation.Column;
      
      @Table(name = "t_user")
      public class User {
      	@Column("user_name")
	  	private String name;
	  	private int status;
      }
  
Java对象查询支持(JSON字符串单对象及对象集查询与Java对象查询有类似方法)
  
  SqlBuilder  
   
	SQL.insert(Class beanClass);
	SQL.delete(Class beanClass);
	SQL.update(Class beanClass);
	SQL.query(Class beanClass);
	上述beanClass和数据对象可以不是同一类型，但需要使用的相应字段定义要一致。假设要对beanClass中field1操作（增删改查），则用于取值的对象otherClassObj中也应有field1的定义。   
    

#使用中SqlExecutor的日志级别需要单独配置，且要高于debug，不然会影响性能，因为JdbcTemplate.handleWarnings()比较耗时。

#查询不到数据时：
 <ul>
  <li>queryBean返回null</li>
  <li>queryBeanList返回值size() == 0</li>
  <li>queryBeanPage返回值getTotal() == 0</li>
  <li>queryMapPage返回值getTotal() == 0</li>
 </ul>
