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

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        Page<ExampleEntity> examplePage = sqlExecutor.queryPage(SQL.query(ExampleEntity.class), 1, 2);
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
        ExampleEntity exampleEntity = new ExampleEntity();
        exampleEntity.setId(Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue());
        exampleEntity.setString(UUID.randomUUID().toString());
        int effectCount = sqlExecutor.insert(exampleEntity);
        Assertions.assertEquals(effectCount, 1);
        ExampleEntity exampleDb = sqlExecutor.queryById(exampleEntity.getId(), ExampleEntity.class);
        Assertions.assertEquals(exampleDb.getString(), exampleEntity.getString());
    }

    @Test
    void insertAndGetKey() {
        ExampleEntity exampleEntity = new ExampleEntity();
        exampleEntity.setString(UUID.randomUUID().toString());
        int id = sqlExecutor.insertAndGetKey(exampleEntity);
        Assertions.assertNotEquals(id, 0);
        ExampleEntity exampleDb = sqlExecutor.queryById(id, ExampleEntity.class);
        Assertions.assertEquals(exampleDb.getString(), exampleEntity.getString());
    }

    @Test
    void batchInsert() {
        ExampleEntity exampleEntity = new ExampleEntity();
        exampleEntity.setId(Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue());
        exampleEntity.setString(UUID.randomUUID().toString());
        int[] effectCount = sqlExecutor.batchInsert(Collections.singletonList(exampleEntity));
        Assertions.assertEquals(effectCount[0], 1);
        ExampleEntity exampleDb = sqlExecutor.queryById(exampleEntity.getId(), ExampleEntity.class);
        Assertions.assertEquals(exampleDb.getString(), exampleEntity.getString());
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

    @Test
    void testQueryList() {
        Page<ExampleEntity> objectPage = sqlExecutor.queryPage(SQL.query(ExampleEntity.class), 1, 2);
        List<ExampleEntity> exampleEntityList = sqlExecutor.queryList(objectPage.getPageData().stream().map(ExampleEntity::getId).collect(Collectors.toList()), ExampleEntity.class);
        Assertions.assertEquals(objectPage.getPageData().size(), exampleEntityList.size());
    }

    @Test
    void testQueryList1() {
        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));
        List<ExampleEntity> exampleEntityList = sqlExecutor.queryList("select * from t_nway where pk_id = ?", ExampleEntity.class, example.getId());
        Assertions.assertEquals(exampleEntityList.size(), 1);
        Assertions.assertEquals(exampleEntityList.get(0).getId(), example.getId());
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