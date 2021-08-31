package com.nway.spring.jdbc;

import com.nway.spring.jdbc.pagination.Page;
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.builder.BatchUpdateBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

class SqlExecutorTest2 extends BaseTest {

    @Autowired
    private SqlExecutor sqlExecutor;

    @Test
    void update() {
        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));
        String str = UUID.randomUUID().toString();
        sqlExecutor.update(SQL.update(ExampleEntity.class).set(ExampleEntity::getString, str).where().eq(example::getId));
        ExampleEntity exampleUpdated = sqlExecutor.queryById(example.getId(), ExampleEntity.class);
        Assertions.assertEquals(str, exampleUpdated.getString());
    }

    @Test
    void updateById() {

        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));

        ExampleEntity exampleEntity = new ExampleEntity();
        exampleEntity.setId(example.getId());
        exampleEntity.setString(UUID.randomUUID().toString());

        sqlExecutor.updateById(exampleEntity);

        ExampleEntity exampleUpdated = sqlExecutor.queryById(example.getId(), ExampleEntity.class);

        Assertions.assertEquals(exampleEntity.getString(), exampleUpdated.getString());
    }

    @Test
    void batchUpdateById() {
        Page<ExampleEntity> examplePage = sqlExecutor.queryPage(SQL.query(ExampleEntity.class), 1,2);
        String str = UUID.randomUUID().toString();
        examplePage.getPageData().forEach(e -> e.setString(str));
        sqlExecutor.batchUpdateById(examplePage.getPageData());
        ExampleEntity exampleUpdated = sqlExecutor.queryById(examplePage.getPageData().get(0).getId(), ExampleEntity.class);
        Assertions.assertEquals(str, exampleUpdated.getString());
    }

    @Test
    void deleteById() {
    }

    @Test
    void batchDeleteById() {
    }

    @Test
    void insert() {
    }

    @Test
    void insertAndGetKey() {
    }

    @Test
    void batchInsert() {
    }

    @Test
    void queryById() {
    }

    @Test
    void queryBean() {
    }

    @Test
    void testQueryBean() {
    }

    @Test
    void queryList() {
    }

    @Test
    void testQueryList() {
    }

    @Test
    void testQueryList1() {
    }

    @Test
    void queryPage() {
    }

    @Test
    void testQueryPage() {
    }

    @Test
    void testQueryPage1() {
    }

    @Test
    void testQueryPage2() {
    }

    @Test
    void count() {
    }

    @Test
    void exist() {
    }

    @Test
    void setDataSource() {
    }

    @Test
    void getJdbcTemplate() {
    }

    @Test
    void setJdbcTemplate() {
    }

    @Test
    void getPaginationSupport() {
    }

    @Test
    void setPaginationSupport() {
    }

    @Test
    void setBeanProcessor() {
    }

    @Test
    void getBeanProcessor() {
    }
}