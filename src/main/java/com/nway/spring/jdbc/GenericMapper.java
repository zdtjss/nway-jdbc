package com.nway.spring.jdbc;

import java.util.Map;

/**
 * 一个通用的ORM工具，根据默认约定或注解定义OM关系。 可以自定义获取对象中部分信息
 * 
 * @author zdtjss@163.com
 * @since 2018-12-01
 *
 */
public class GenericMapper {

	public <T> T get(Class<T> beanClass) {

		return null;
	}

	public <T> T get(Class<T> beanClass, Map<String, Object> param) {

		return null;
	}

	public <T> T add(Class<T> beanClass, Map<String, Object> param) {

		return null;
	}
}
