package com.nway.spring.jdbc;

import com.nway.spring.jdbc.bean.processor.asm.AsmBeanProcessor;
import com.nway.spring.jdbc.pagination.Page;
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.builder.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringJUnitConfig(classes = SqlExecutorAsmTest.Config.class)
class SqlExecutorAsmTest  {

    @Autowired
    private SqlExecutor sqlExecutor;

    @Configuration
    static class Config {

        @Autowired
        private DataSource dataSource;

        @Bean("dataSource")
        public DataSource dataSource() {

            Resource resource = new ClassPathResource("datasource.xml");

            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

            XmlBeanDefinitionReader xmlBeanReader = new XmlBeanDefinitionReader(beanFactory);

            xmlBeanReader.loadBeanDefinitions(resource);

            return beanFactory.getBean(DataSource.class);
        }

        @Bean
        @DependsOn("dataSource")
        public SqlExecutor sqlExecutor() {
            SqlExecutor sqlExecutor = new SqlExecutor(dataSource);
            sqlExecutor.setBeanProcessor(new AsmBeanProcessor());
            return sqlExecutor;
        }

    }

    @Test
    void queryById() {

        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));

        ExampleEntity example1 = sqlExecutor.queryById(example.getId(), ExampleEntity.class);

        Assertions.assertEquals(example.getString(), example1.getString());
    }

    @Test
    void queryList() {
        List<ExampleEntity> objectList = sqlExecutor.queryList(SQL.query(ExampleEntity.class));
        Assertions.assertTrue(objectList.size() > 0);
    }
}