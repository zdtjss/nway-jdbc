package com.nway.spring.jdbc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"/spring.xml"})
public class BaseTest {

    private static DataSource dataSource;

    @BeforeAll
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

    @AfterAll
    public static void tearDownAfterClass() throws Exception {

        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {

            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).close();
        }
    }
}
