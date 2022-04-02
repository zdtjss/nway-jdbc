package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.sql.builder.BatchInsertBuilder;
import com.nway.spring.jdbc.sql.builder.BatchUpdateBuilder;
import com.nway.spring.jdbc.sql.builder.DeleteBuilder;
import com.nway.spring.jdbc.sql.builder.InsertBuilder;
import com.nway.spring.jdbc.sql.builder.QueryBuilder;
import com.nway.spring.jdbc.sql.builder.UpdateBuilder;

public class SQL {

	public static <T> QueryBuilder query(Class<T> beanClass) {
		return new QueryBuilder(beanClass);
	}
	
	public static UpdateBuilder update(Class<?> beanClass) {
		
		return new UpdateBuilder(beanClass);
	}
	
	public static BatchUpdateBuilder batchUpdate(Class<?> beanClass) {
		
		return new BatchUpdateBuilder(beanClass);
	}
	
	public static InsertBuilder insert(Class<?> beanClass) {
		
		return new InsertBuilder(beanClass);
	}
	
	public static BatchInsertBuilder batchInsert(Class<?> beanClass) {
		
		return new BatchInsertBuilder(beanClass);
	}
	
	public static DeleteBuilder delete(Class<?> beanClass) {
		
		return new DeleteBuilder(beanClass);
	}
}
