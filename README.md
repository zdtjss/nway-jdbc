Nway-JDBC基于Spring JDBC，扩展自Spring的JdbcTemplate，完全兼容Spring JDBC。

如遇问题可联系zdtjss@163.com或QQ:670950251。

如果您通过开源中国访问，发现不登录不能下载，可以到 https://github.com/zdtjss/nway-jdbc 完成您的操作。

使用中SqlExecutor的日志级别需要单独配置，且要高于debug，不然会影响性能，因为JdbcTemplate.handleWarnings()比较耗时。

查询不到数据时：
 <ul>
  <li>queryForBean返回null；</li>
  <li>queryForBeanList返回值size() == 0；</li>
  <li>queryForBeanPagination返回值getTotalCount() == 0；</li>
  <li>queryForMapListPagination返回值getTotalCount() == 0；</li>
  <li>queryForJson返回"{}"</li>
  <li>queryForJsonList返回"[]"</li>
  <li>testJsonPagination返回对象中totalCount == 0</li>
 </ul>

第三方依赖：
	ASM或Javassist

数据库表字段与Java类属性映射规则：

    去除表字段中下划线(_)与Java类属性名称忽略大小写比较，相等则赋值，如：表字段名称为user_name，会通过类中setUserName方法赋值。
	也可以通过com.nway.spring.jdbc.annotation.Column修改默认规则。如：
	
      @Column("user_name")
      public String getName()
	  
Java对象查询支持(JSON字符串单对象及对象集查询与Java对象查询有类似方法)

    单对象：
	
        各种参数的queryForBean方法支持单个Java对象查询
		
        User usr = sqlExecutor.queryForBean("select * from t_user where id = ?", User.class, 10000);
		
    对象集：
	
        各种参数的queryForBeanList方法支持多个Java对象查询
		
        List<User> users = sqlExecutor.queryForBeanList("select * from t_user where id <> ?", User.class, 10000);
		
    分页对象集：
	
        各种参数的queryForBeanPagination方法支持Java对象分页查询
		
        Pagination<User> users = sqlExecutor.queryForBeanPagination("select * from t_user where id <> ? order by id", new Object[]{ 10000 }, 1, 10, User.class);
		
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
		
        Pagination<Map<String, Object>> users = sqlExecutor.queryForMapListPagination("select * from t_user where id <> ? order by id", new Object[]{ 10000 }, 1, 10);
		
        users使用同queryForBeanPagination

	分页默认支持Oracle、Mysql、MariaDB，关于其他数据库的分页可以实现com.nway.spring.jdbc.PaginationSupport接口，通过com.nway.spring.jdbc.SqlExecutor.setPaginationSupport方法引入。
