package com.nway.spring.jdbc.sql;

import org.junit.Test;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.sql.builder.DefaultSqlBuilder;

public class QueryBuilderTest {

	@Test
	public void getSqlTest() {
		
		Computer computer = new Computer();
		computer.setBrand("abc哈哈");
		
		DefaultSqlBuilder builder = SQL.query(Computer.class).like(computer::getBrand).notLike(computer::getModel);
		
		System.out.println(builder.getSql());
		System.out.println(builder.getParam());
	}
}
