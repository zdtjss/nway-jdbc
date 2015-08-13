package com.nway.spring.jdbc;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/spring.xml" })
public class BaseTest {

	private static DataSource dataSource;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		Resource resource = new ClassPathResource("spring.xml");

		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

		XmlBeanDefinitionReader xmlBeanReader = new XmlBeanDefinitionReader(beanFactory);

		xmlBeanReader.loadBeanDefinitions(resource);

		dataSource = beanFactory.getBean(DataSource.class);

		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		
		builder.bind("java:comp/env/jdbc/nway", dataSource);
		builder.activate();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {

			((org.apache.tomcat.jdbc.pool.DataSource) dataSource).close();
		}
	}
}
