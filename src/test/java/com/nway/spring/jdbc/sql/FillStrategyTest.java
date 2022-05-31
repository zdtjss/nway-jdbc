package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.SqlExecutor;
import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.annotation.enums.ColumnType;
import com.nway.spring.jdbc.sql.fill.FillStrategy;
import com.nway.spring.jdbc.sql.fill.StringIdStrategy;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;


@Rollback
@Transactional
@SpringJUnitConfig(classes = com.nway.spring.jdbc.sql.FillStrategyTest.Config.class)
public class FillStrategyTest {

    @Autowired
    private SqlExecutor sqlExecutor;

    @Configuration
    @EnableTransactionManagement
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
        public PlatformTransactionManager txManager() {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        @DependsOn("dataSource")
        public SqlExecutor sqlExecutor() {
            return new SqlExecutor(dataSource);
        }

    }

    @Test
    public void stringIdStrategyTest() {

        StringIdStrategy stringIdStrategy = new StringIdStrategy();

        Assertions.assertFalse(stringIdStrategy.isSupport(SqlType.DELETE));
        Assertions.assertFalse(stringIdStrategy.isSupport(SqlType.SELECT));
        Assertions.assertFalse(stringIdStrategy.isSupport(SqlType.UPDATE));
        Assertions.assertTrue(stringIdStrategy.isSupport(SqlType.INSERT));

        Object value = stringIdStrategy.getValue(SqlType.INSERT, null);
        Assertions.assertNotNull(value);

        value = stringIdStrategy.getValue(SqlType.INSERT, "");
        Assertions.assertNotNull(value);

        value = stringIdStrategy.getValue(SqlType.INSERT, FillStrategy.DEFAULT_NONE);
        Assertions.assertNotNull(value);

        value = stringIdStrategy.getValue(SqlType.INSERT, "abc");
        Assertions.assertEquals(value, "abc");
    }


    @Test
    public void test() {

        FillStrategyPojo pojo = new FillStrategyPojo();

        int effect = sqlExecutor.insert(pojo);
        Assertions.assertEquals(effect, 1);

        FillStrategyPojo strategyPojo = sqlExecutor.queryById(pojo.getId(), FillStrategyPojo.class);
        Assertions.assertEquals(strategyPojo.getWwInt(), pojo.getWwInt() + 10);

        sqlExecutor.updateById(strategyPojo);
        Assertions.assertEquals(strategyPojo.getWwInt(), pojo.getWwInt() + 10 + 1);

        FillStrategyPojo strategyPojo2 = sqlExecutor.queryById(pojo.getId(), FillStrategyPojo.class);
        Assertions.assertEquals(strategyPojo2.getWwInt(), pojo.getWwInt() + 10);
    }


    @Data
    @Table("t_nway")
    public static class FillStrategyPojo {

        @Column(name = "pk_id", type = ColumnType.ID, fillStrategy = TestFillStrategy.class)
        private Integer id;

        @Column(name = "w_int", fillStrategy = TestFillStrategy.class)
        private Integer wwInt;

        @Column(fillStrategy = LogicFieldStrategy.class, permissionStrategy = LogicFieldStrategy.class)
        private Integer delFlag;
    }
}
