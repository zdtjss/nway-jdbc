#Nway-JDBC扩展自Spring的JdbcTemplate。提供了高性能简单易用的单表操作方法，支持新增、修改时为指定字段自动填充数据，并为数据权限的管控提供了支持。使用时只需要给主类SqlExecutor配置好数据源，不需要继承某个类，也不需要实现哪个接口。

#镜像地址：https://gitee.com/nway/Nway-JDBC。

	<dependency>
		<groupId>com.github.zdtjss</groupId>
		<artifactId>nway-jdbc</artifactId>
		<version>1.3.15</version>
	</dependency>

#本项目专注于单表操作，对于多表的情况可以考虑使用视图，但更建议使用MyBatis等其他擅长多表操作的工具。

#使用方法：

基于xml配置的bean： 

    <bean id="sqlExecutor" class="com.nway.spring.jdbc.SqlExecutor">
        <property name="dataSource" ref="dataSource"/>
    </bean>

Spring Boot：

    @Autowired
    private DataSource dataSource;

	@Bean
	public SqlExecutor createSqlExecutor() {
		return new SqlExecutor(dataSource);
	}

增、改、删：  

	SqlBuilder builder = SQL.insert(Class beanClass);
	SqlBuilder builder = SQL.delete(Class beanClass);
	SqlBuilder builder = SQL.update(Class beanClass);
	
	int effectCount = sqlExecutor.update(builder);
    
单对象查询：
	
    SqlBuilder builder = SQL.query(User.class).where().eq(usrQuery::getStatus).like(usrQuery::getName);
    User user = sqlExecutor.queryBean(builder);
    User user = sqlExecutor.queryById(100, User.class);
    
    or
    
    User usr = sqlExecutor.queryForBean("select * from t_user where user_name like ? and status = ?", User.class, "abc", 1);
        
对象集查询：
	
    SqlBuilder builder = SQL.query(User.class).where().eq(usrQuery::getStatus).like(usrQuery::getName);
    List<User> users = sqlExecutor.queryList(builder);
    List<User> users = sqlExecutor.queryList(Arrays.asList(100), User.class);
    Map<String, User> userMap = queryListMap(builder, User::getId)
    Map<String, User> userMap = queryListMap(Arrays.asList(100), User.class, User::getId)
    
    or
    
    List<User> users = sqlExecutor.queryList("select * from t_user where user_name like ? and status = ?", User.class, 10000);
    
分页对象集查询：

    SqlBuilder builder = SQL.query(User.class).where().eq(usrQuery::getStatus).like(usrQuery::getName).orderBy(usrQuery::getId);
    Page<User> users = sqlExecutor.queryPage(builder, 1, 10);
    
    or
    
    Page<User> users = sqlExecutor.queryPage("select * from t_user where user_name like ? and status = ? order by id", new Object[]{ "abc", 1 }, 1, 10, User.class);
    
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
		
    Page<Map<String, Object>> users = sqlExecutor.queryPage("select * from user_name where id <> ? order by id", new Object[]{ "abc", 1 }, 1, 10);
    分页默认支持Oracle、Mysql、MariaDB，关于其他数据库的分页可以实现com.nway.spring.jdbc.pagination.PaginationSupport接口，通过com.nway.spring.jdbc.SqlExecutor.setPaginationSupport方法引入。

自动填充数据和数据权限支持：

	应用场景： 
		1、数据权限可用于数据查询时自动限制可返回的数据
		2、自动填充功能可应用于统一处理数据的“创建人”、“修改人”、“创建时间”、“修改时间“等
	使用办法：
		查询对象对应属性加入注解 Column(fillStrategy = TestFillStrategy.class, permissionStrategy = TestPermissionStrategy.class) 

关于深度自定义：

    1、如果需要对执行SQL和参数深度干预，可以集成Spring的JdbcTemplate覆盖相关方法，然后通过SqlExecutor.setJdbcTemplate替换默认值。
    2、默认表字段和类属性的映射是通过反射完成，但提供了ASM的实现，如果有更好的实现方案，可以实现接口BeanProcessor，然后通过SqlExecutor.setBeanProcessor替换默认值。
    注：理论上ASM性能应高于反射，但自测结果显示，差距并不明显，具体原因需要进一步定位。

#数据库表与Java类的映射规则：  
   
   表名：
   @Table("t_user")
   
   字段：
   类属性和表列默认的对应规则是：删除下划线后首字符大写。表字段user_name对应属性userName，会通过类中setUserName方法赋值。
   也可以通过com.nway.spring.jdbc.annotation.Column修改默认规则。
    例：
   
      import com.nway.spring.jdbc.annotation.Table;
      import com.nway.spring.jdbc.annotation.Column;
      
      @Table("t_user")
      public class User {
      	@Column("user_name")
	  	private String name;
	  	private int status;
      }

#使用中SqlExecutor的日志级别需要单独配置，且要高于debug，不然会影响性能，因为JdbcTemplate.handleWarnings()比较耗时。

# 查询不到数据时：

 <ul>
  <li>query返回null</li>
  <li>queryList返回值size() == 0</li>
  <li>queryPage返回值getTotal() == 0</li>
 </ul>

# 性能测试：

各种情况下的性能均不输给当前主流的其他工具

    OrderPerformanceTest 单线程顺序执行时的性能测试类
    ConcurrentPerformanceTest   多线程并发模式下的性能测试类