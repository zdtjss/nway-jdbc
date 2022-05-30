package com.nway.spring.jdbc;

import com.alibaba.fastjson.JSON;
import com.nway.spring.jdbc.pagination.Page;
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.builder.*;
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
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

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

@Rollback
@Transactional
@SpringJUnitConfig(classes = SqlExecutorTest.Config.class)
class SqlExecutorTest {

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
    void update() {
        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));
        String str = UUID.randomUUID().toString();
        UpdateBuilder updateBuilder = SQL.update(ExampleEntity.class).set(ExampleEntity::getString, str).eq(example::getId);
        sqlExecutor.update(updateBuilder);
        ExampleEntity exampleUpdated = sqlExecutor.queryById(example.getId(), ExampleEntity.class);
        Assertions.assertEquals(str, exampleUpdated.getString());
    }

    @Test
    void updateById() {

        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));

        ExampleEntity exampleEntity = new ExampleEntity();
        exampleEntity.setId(example.getId());
        exampleEntity.setString(UUID.randomUUID().toString());

        int effect = sqlExecutor.updateById(exampleEntity);

        Assertions.assertEquals(effect, 1);

        ExampleEntity exampleUpdated = sqlExecutor.queryById(example.getId(), ExampleEntity.class);

        Assertions.assertEquals(exampleEntity.getString(), exampleUpdated.getString());
    }

    @Test
    void updateById2() {

        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));

        ExampleEntity exampleEntity = new ExampleEntity();
        exampleEntity.setId(example.getId());
        exampleEntity.setString(UUID.randomUUID().toString());

        int effect = sqlExecutor.updateById(exampleEntity, ExampleEntity::getString);

        Assertions.assertEquals(effect, 1);

        ExampleEntity exampleUpdated = sqlExecutor.queryById(example.getId(), ExampleEntity.class);

        Assertions.assertEquals(exampleEntity.getString(), exampleUpdated.getString());
    }

    @Test
    void batchUpdateById() {
        Page<ExampleEntity> examplePage = sqlExecutor.queryPage(SQL.query(ExampleEntity.class), 1, 2);
        String str = UUID.randomUUID().toString();
        examplePage.getPageData().forEach(e -> e.setString(str));
        int effect = sqlExecutor.batchUpdateById(examplePage.getPageData());
        Assertions.assertEquals(examplePage.getPageData().size(), effect);
        ExampleEntity exampleUpdated = sqlExecutor.queryById(examplePage.getPageData().get(0).getId(), ExampleEntity.class);
        Assertions.assertEquals(str, exampleUpdated.getString());
    }

    @Test
    void batchUpdateById2() {
        Page<ExampleEntity> examplePage = sqlExecutor.queryPage(SQL.query(ExampleEntity.class), 1, 2);
        List<ExampleEntity> exampleList = examplePage.getPageData().stream().map(entity -> {
            ExampleEntity example = new ExampleEntity();
            example.setId(entity.getId());
            example.setString(UUID.randomUUID().toString());
            example.setPpInt((int) (Math.random() * 1000));
            return example;
        }).collect(Collectors.toList());
        int effect = sqlExecutor.batchUpdateById(exampleList, ExampleEntity::getPpInt, ExampleEntity::getString);
        Assertions.assertEquals(effect, exampleList.size());
        ExampleEntity exampleUpdated = sqlExecutor.queryById(examplePage.getPageData().get(0).getId(), ExampleEntity.class);
        Assertions.assertEquals(exampleList.get(0).getString(), exampleUpdated.getString());
    }

    @Test
    void batchUpdate() {
        Page<ExampleEntity> examplePage = sqlExecutor.queryPage(SQL.query(ExampleEntity.class).isNull(ExampleEntity::getSqlDate), 1, 2);
        List<ExampleEntity> exampleList = examplePage.getPageData().stream().map(entity -> {
            ExampleEntity example = new ExampleEntity();
            example.setId(entity.getId());
            example.setSqlDate(entity.getSqlDate());
            example.setString(UUID.randomUUID().toString());
            example.setPpInt((int) (Math.random() * 1000));
            return example;
        }).collect(Collectors.toList());
        BatchUpdateBuilder updateBuilder = SQL.batchUpdate(ExampleEntity.class)
                .columns(ExampleEntity::getPpInt, ExampleEntity::getString)
                .addCondition(ExampleEntity::getId, SqlOperator.EQ)
                .addCondition(ExampleEntity::getSqlDate, SqlOperator.IS_NULL)
                .use(exampleList);
        int effect = sqlExecutor.batchUpdate(updateBuilder);
        Assertions.assertEquals(effect, exampleList.size());
        ExampleEntity exampleUpdated = sqlExecutor.queryById(exampleList.get(0).getId(), ExampleEntity.class);
        Assertions.assertEquals(exampleList.get(0).getString(), exampleUpdated.getString());
    }

    @Test
    void deleteById() {
        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));
        int effectCount = sqlExecutor.deleteById(example.getId(), ExampleEntity.class);
        Assertions.assertEquals(effectCount, 1);
        boolean exist = sqlExecutor.exist(SQL.query(ExampleEntity.class).eq(example::getId));
        Assertions.assertFalse(exist);
    }

    @Test
    void deleteTest() {
        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));
        DeleteBuilder deleteBuilder = SQL.delete(ExampleEntity.class).eq(ExampleEntity::getId, example.getId());
        int effectCount = sqlExecutor.delete(deleteBuilder);
        Assertions.assertEquals(effectCount, 1);
        boolean exist = sqlExecutor.exist(SQL.query(ExampleEntity.class).eq(example::getId));
        Assertions.assertFalse(exist);
    }

    @Test
    void batchDeleteById() {
        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));
        int effectCount = sqlExecutor.batchDeleteById(Collections.singletonList(example.getId()), ExampleEntity.class);
        Assertions.assertEquals(effectCount, 1);
        boolean exist = sqlExecutor.exist(SQL.query(ExampleEntity.class).eq(example::getId));
        Assertions.assertFalse(exist);
    }

    @Test
    void insert() {
        ExampleEntity exampleEntity = new ExampleEntity();
        int effectCount = sqlExecutor.insert(exampleEntity);
        Assertions.assertEquals(effectCount, 1);
        ExampleEntity exampleDb = sqlExecutor.queryById(exampleEntity.getId(), ExampleEntity.class);
        Assertions.assertEquals(exampleDb.getString(), exampleEntity.getString());
    }

    //    @Test
    void insertAndGetKey() {
        ExampleEntity exampleEntity = new ExampleEntity();
        exampleEntity.setString(UUID.randomUUID().toString());
        int id = 0;//sqlExecutor.insertAndGetKey(exampleEntity);
        Assertions.assertNotEquals(id, 0);
        ExampleEntity exampleDb = sqlExecutor.queryById(id, ExampleEntity.class);
        Assertions.assertEquals(exampleDb.getString(), exampleEntity.getString());
    }

    @Test
    void batchInsert() {
        ExampleEntity exampleEntity = new ExampleEntity();
        exampleEntity.setId(Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue());
        exampleEntity.setString(UUID.randomUUID().toString());
        int effectCount = sqlExecutor.batchInsert(Collections.singletonList(exampleEntity));
        Assertions.assertEquals(effectCount, 1);
        ExampleEntity exampleDb = sqlExecutor.queryById(exampleEntity.getId(), ExampleEntity.class);
        Assertions.assertEquals(exampleDb.getString(), exampleEntity.getString());
    }

    @Test
    void queryById() {

        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));

        ExampleEntity example1 = sqlExecutor.queryById(example.getId(), ExampleEntity.class);

//        Assertions.assertEquals(example.getString(), example1.getString());
    }

    @Test
    void queryList() {
        List<ExampleEntity> objectList = sqlExecutor.queryList(SQL.query(ExampleEntity.class));
        Assertions.assertTrue(objectList.size() > 0);
    }

    @Test
    public void withColumnTest() {
        ExampleEntity first = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class).withColumn(ExampleEntity::getId));
        Assertions.assertNotNull(first.getId());
        Assertions.assertNull(first.getString());
        Assertions.assertNull(first.getMv());
    }

    @Test
    void queryListMap() {
        Map<Integer, ExampleEntity> objectMap = sqlExecutor.queryListMap(SQL.query(ExampleEntity.class), ExampleEntity::getId);
        Assertions.assertTrue(objectMap.size() > 0);
    }

    @Test
    void testQueryList() {
        Page<ExampleEntity> objectPage = sqlExecutor.queryPage(SQL.query(ExampleEntity.class), 1, 2);
        List<ExampleEntity> exampleEntityList = sqlExecutor.queryList(objectPage.getPageData().stream().map(ExampleEntity::getId).collect(Collectors.toList()), ExampleEntity.class);
        Assertions.assertEquals(objectPage.getPageData().size(), exampleEntityList.size());
    }

    @Test
    void testQueryListMap() {
        Page<ExampleEntity> objectPage = sqlExecutor.queryPage(SQL.query(ExampleEntity.class), 1, 2);
        Map<Integer, ExampleEntity> exampleEntityMap = sqlExecutor.queryListMap(objectPage.getPageData().stream().map(ExampleEntity::getId).collect(Collectors.toList()), ExampleEntity.class, ExampleEntity::getId);
        Assertions.assertEquals(objectPage.getPageData().size(), exampleEntityMap.size());
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
        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));
        Page<Map<String, Object>> mapPage = sqlExecutor.queryPage("select * from t_nway where pk_id = ?", new Object[]{example.getId()}, 1, 1);
        Assertions.assertEquals(mapPage.getPageData().size(), 1);
        Assertions.assertEquals(mapPage.getPageData().get(0).get("pkId"), example.getId());
    }

    @Test
    void testQueryPage() {
        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));
        Page<ExampleEntity> mapPage = sqlExecutor.queryPage("select * from t_nway where pk_id = ?", new Object[]{example.getId()}, 1, 1, ExampleEntity.class);
        Assertions.assertEquals(mapPage.getPageData().size(), 1);
        Assertions.assertEquals(mapPage.getPageData().get(0).getId(), example.getId());
    }

    @Test
    void testQueryPage1() {
        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));
        Page<ExampleEntity> mapPage = sqlExecutor.queryPage("select * from t_nway where pk_id = ?", new Object[]{example.getId()}, 1, 1, ExampleEntity.class);
        Assertions.assertEquals(mapPage.getPageData().size(), 1);
        Assertions.assertEquals(mapPage.getPageData().get(0).getId(), example.getId());
    }

    @Test
    void testQueryPage2() {
        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));
        QueryBuilder queryBuilder = SQL.query(ExampleEntity.class);
        queryBuilder.distinct().withColumn(ExampleEntity::getId, ExampleEntity::getUtilDate, ExampleEntity::getString)
                .eq(example::getId)
                .orderBy(ExampleEntity::getUtilDate, ExampleEntity::getString);
        Page<ExampleEntity> mapPage = sqlExecutor.queryPage(queryBuilder, 1, 1);
        Assertions.assertEquals(mapPage.getPageData().size(), 1);
        Assertions.assertEquals(mapPage.getPageData().get(0).getId(), example.getId());
    }

    @Test
    void count() {
        int count = sqlExecutor.count(SQL.query(ExampleEntity.class));
        Assertions.assertTrue(count > 0);
        count = sqlExecutor.count(SQL.query(ExampleEntity.class).eq(ExampleEntity::getString, UUID.randomUUID().toString()));
        Assertions.assertEquals(0, count);
    }

    @Test
    void exist() {
        boolean exist = sqlExecutor.exist(SQL.query(ExampleEntity.class));
        Assertions.assertTrue(exist);
        exist = sqlExecutor.exist(SQL.query(ExampleEntity.class).eq(ExampleEntity::getString, UUID.randomUUID().toString()));
        Assertions.assertFalse(exist);
    }

    @Test
    void mvInsertTest() {
        ExampleEntity obj = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class).orderByDesc(ExampleEntity::getId));
        obj.setMv(Arrays.asList(UUID.randomUUID().toString().substring(0, 3), "2", UUID.randomUUID().toString().substring(0, 3)));
        obj.setMv2(Arrays.asList(UUID.randomUUID().toString().substring(0, 3), "2", UUID.randomUUID().toString().substring(0, 3)));
        obj.setId(obj.getId() + 1);
        sqlExecutor.insert(obj);

        ExampleEntity exampleEntity = sqlExecutor.queryById(obj.getId(), ExampleEntity.class, ExampleEntity::getMv, ExampleEntity::getMv2);

        for (int i = 0; i < obj.getMv().size(); i++) {
            Assertions.assertEquals(obj.getMv().get(i), exampleEntity.getMv().get(i));
        }

        for (int i = 0; i < obj.getMv2().size(); i++) {
            Assertions.assertEquals(obj.getMv2().get(i), exampleEntity.getMv2().get(i));
        }
    }

    @Test
    void mvQueryTest() {
        mvInsertTest();
        List<String> fks = sqlExecutor.getJdbcTemplate().queryForList("select distinct fk from t_nway_mv", String.class);
        ExampleEntity obj = sqlExecutor.queryById(fks.get(0), ExampleEntity.class, ExampleEntity::getMv, ExampleEntity::getMv2);
        Assertions.assertFalse(obj.getMv().isEmpty());
        Assertions.assertFalse(obj.getMv2().isEmpty());
    }

    @Test
    void mvQueryTest2() {
        mvInsertTest();
        List<String> fks = sqlExecutor.getJdbcTemplate().queryForList("select distinct fk from t_nway_mv", String.class);
        ExampleEntity obj = sqlExecutor.queryById(fks.get(0), ExampleEntity.class, "mv");
        Assertions.assertFalse(obj.getMv().isEmpty());
        Assertions.assertNull(obj.getMv2());
    }

    @Test
    void mvQueryTest3() {
        mvInsertTest();
        List<String> fks = sqlExecutor.getJdbcTemplate().queryForList("select distinct fk from t_nway_mv", String.class);
        ExampleEntity obj = sqlExecutor.queryById(fks.get(0), ExampleEntity.class);
        Assertions.assertNull(obj.getMv());
        Assertions.assertNull(obj.getMv2());
    }

    @Test
    void mvListTest() {
        List<String> fks = sqlExecutor.getJdbcTemplate().queryForList("select distinct fk from t_nway_mv limit 10", String.class);
        List<ExampleEntity> objectList = sqlExecutor.queryList(SQL.query(ExampleEntity.class).withMVColumn(ExampleEntity::getMv).in(ExampleEntity::getId, fks));
        boolean anyMatch = objectList.stream().anyMatch(obj -> obj.getMv() != null && !obj.getMv().isEmpty());
        boolean anyMatch2 = objectList.stream().anyMatch(obj -> obj.getMv2() != null && !obj.getMv2().isEmpty());
        Assertions.assertTrue(anyMatch);
        Assertions.assertFalse(anyMatch2);
    }

    @Test
    void mvListTest2() {
        List<String> fks = sqlExecutor.getJdbcTemplate().queryForList("select distinct fk from t_nway_mv limit 10", String.class);
        List<ExampleEntity> objectList = sqlExecutor.queryList(SQL.query(ExampleEntity.class).withMVColumn(ExampleEntity::getMv, ExampleEntity::getMv2).in(ExampleEntity::getId, fks));
        boolean anyMatch = objectList.stream().anyMatch(obj -> obj.getMv() != null && !obj.getMv().isEmpty());
        boolean anyMatch2 = objectList.stream().anyMatch(obj -> obj.getMv2() != null && !obj.getMv2().isEmpty());
        Assertions.assertTrue(anyMatch);
        Assertions.assertTrue(anyMatch2);
    }

    @Test
    void mvListTest3() {
        List<String> fks = sqlExecutor.getJdbcTemplate().queryForList("select distinct fk from t_nway_mv limit 10", String.class);
        List<ExampleEntity> objectList = sqlExecutor.queryList(SQL.query(ExampleEntity.class).in(ExampleEntity::getId, fks));
        boolean anyMatch = objectList.stream().anyMatch(obj -> obj.getMv() == null || obj.getMv().isEmpty());
        boolean anyMatch2 = objectList.stream().anyMatch(obj -> obj.getMv2() == null || obj.getMv2().isEmpty());
        Assertions.assertTrue(anyMatch);
        Assertions.assertTrue(anyMatch2);
    }

    @Test
    public void queryFirstTest() {
        ExampleEntity first = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class).ne(ExampleEntity::getString, "b").orderByDesc(ExampleEntity::getUtilDate));
        Assertions.assertNotNull(first);
        first = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class).orderByDesc(ExampleEntity::getUtilDate));
        Assertions.assertNotNull(first);
    }

    @Test
    public void queryOneTest() {
        ExampleEntity max = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class).orderByDesc(ExampleEntity::getId));
        max.setId(max.getId() + 1);
        max.setString(UUID.randomUUID().toString());
        sqlExecutor.insert(max);
        ExampleEntity one = sqlExecutor.queryOne(SQL.query(ExampleEntity.class).eq(max::getString));
        Assertions.assertEquals(max.getId(), one.getId());
    }

    @Test
    public void queryOneTest2() {

        ExampleEntity max = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class).orderByDesc(ExampleEntity::getId));
        max.setId(max.getId() + 1);
        max.setString("abc");

        ExampleEntity similar = JSON.parseObject(JSON.toJSONString(max), ExampleEntity.class);
        similar.setId(max.getId() + 1);
        similar.setString(max.getString());

        sqlExecutor.batchInsert(Arrays.asList(max, similar));

        Assertions.assertThrows(IncorrectResultSizeDataAccessException.class,
                () -> sqlExecutor.queryOne(SQL.query(ExampleEntity.class).eq(max::getString)));
    }

    @Test
    public void countSql() {

        StringBuilder countSql = new StringBuilder();

        // countSql.append("select DISTINCT aaa,bbb from t_nway".toUpperCase());
        countSql.append("select top 50 PERCENT * from t_nway".toUpperCase());

        int firstFromIndex = countSql.indexOf(" FROM ");

        String selectedColumns = countSql.substring(0, firstFromIndex + 1);

        if (!selectedColumns.contains(" DISTINCT ")
                && !selectedColumns.matches(".+TOP\\p{Blank}+\\d+\\p{Blank}+.+")) {

            countSql = countSql.delete(0, firstFromIndex).insert(0, "SELECT COUNT(1)");
        } else {

            countSql.insert(0, "SELECT COUNT(1) FROM (").append(')');
        }

        System.out.println(countSql);
    }

    @Test
    public void countTest2() {

        String sql = "select * from t_nway";
        Page<Map<String, Object>> page = sqlExecutor.queryPage(sql, null, 1, 3);
        Assertions.assertEquals(page.getPageData().size(), 3);

        sql = "select * from t_nway order by pk_id";
        page = sqlExecutor.queryPage(sql, null, 1, 3);
        Assertions.assertEquals(page.getPageData().size(), 3);

        ExampleEntity first = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));

        sql = "select * from t_nway where pk_id = ? order by pk_id";
        page = sqlExecutor.queryPage(sql, new Object[]{first.getId()}, 1, 10);
        Assertions.assertEquals(page.getPageData().size(), 1);

        sql = "select distinct pk_id,p_int from t_nway where pk_id = ? order by pk_id";
        page = sqlExecutor.queryPage(sql, new Object[]{first.getId()}, 1, 10);
        Assertions.assertEquals(page.getPageData().size(), 1);


        sql = "select * from t_nway";
        page = sqlExecutor.queryPage(sql, null, 1, 3);
        Assertions.assertEquals(page.getPageData().size(), 3);

        sql = "select * from t_nway order by pk_id";
        page = sqlExecutor.queryPage(sql, null, 1, 3);
        Assertions.assertEquals(page.getPageData().size(), 3);

        sql = "select * from t_nway where pk_id = ? order by pk_id";
        page = sqlExecutor.queryPage(sql, new Object[]{first.getId()}, 1, 10);
        Assertions.assertEquals(page.getPageData().size(), 1);

        sql = "select DISTINCT pk_id,p_int from t_nway where pk_id = ? order by pk_id";
        page = sqlExecutor.queryPage(sql, new Object[]{first.getId()}, 1, 10);
        Assertions.assertEquals(page.getPageData().size(), 1);

        sql = "select a.pk_id,a.p_int from t_nway a, (select * from t_nway where pk_id <> 100 order by pk_id) b where a.pk_id = b.pk_id and a.pk_id = ? order by a.pk_id";
        page = sqlExecutor.queryPage(sql, new Object[]{first.getId()}, 1, 10);
        Assertions.assertEquals(page.getPageData().size(), 1);
    }

    @Test
    public void regex() {

        String input = "a order by abc1,a.ab2  ";

        Pattern ORDER_BY_PATTERN = Pattern
                .compile(".+\\p{Blank}+(ORDER|order)\\p{Blank}+(BY|by)[\\,\\p{Blank}\\w\\.]+");

        System.out.println(ORDER_BY_PATTERN.matcher(input.toLowerCase()).matches());
    }

    @Test
    public void argTest() {
        arg(new Object[]{"abc", 123});
    }

    private void arg(Object... abc) {
        System.out.println(Arrays.deepToString(abc));
    }

    @Test
    public void updateSqlTest() {

        UpdateBuilder sqlBuilder = SQL.update(ExampleEntity.class);
        sqlBuilder.ignoreInvalid(true);
        sqlBuilder.set(ExampleEntity::getString, null)
                .set(ExampleEntity::getId, 0);
        Assertions.assertFalse(sqlBuilder.getSql().contains(" string = "));
        Assertions.assertTrue(sqlBuilder.getSql().contains(" pk_id = "));
    }

    @Test
    public void increaseTest() {

        ExampleEntity first = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));

        sqlExecutor.update(SQL.update(ExampleEntity.class).increase(ExampleEntity::getPpLong).eq(first::getId));

        ExampleEntity example = sqlExecutor.queryById(first.getId(), ExampleEntity.class);

        Assertions.assertEquals(first.getPpLong() + 1, example.getPpLong());
    }

    private final AtomicInteger atomicInteger = new AtomicInteger(5041731);

    @Test
    public void initData() throws InterruptedException {
        int times = 1;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        Collection<Callable<Void>> initDataTask = new ArrayList<>(times);

        for (int i = 0; i < times; i++) {
            initDataTask.add(() -> {
                initTable();
                return null;
            });
        }
        long begin = System.currentTimeMillis();
        executorService.invokeAll(initDataTask);
        System.out.println("initDataTask = " + (System.currentTimeMillis() - begin));
    }

    private void initTable() throws SQLException, SQLException {

        List<ExampleEntity> exampleEntityList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {

            ExampleEntity example = new ExampleEntity();
            example.setId(atomicInteger.getAndIncrement());
            DecimalFormat numberFormat = new DecimalFormat("0000000000.00000000000000");
            example.setPpBoolean(0 == ((Math.random() * 10000) % 2));
            example.setPpByte(Double.valueOf(Math.random() * Byte.MAX_VALUE).byteValue());
            example.setPpShort(Double.valueOf(Math.random() * Short.MAX_VALUE).shortValue());
            example.setPpInt(Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue());
            example.setPpLong(Double.valueOf(Math.random() * Long.MAX_VALUE).longValue());
            example.setPpFloat(Float.parseFloat(numberFormat.format((Math.random() * Float.MAX_VALUE))));
            example.setPpDouble(Double.parseDouble(numberFormat.format((Math.random() * Double.MAX_VALUE))));
            example.setPpByteArr(UUID.randomUUID().toString().getBytes());
            example.setWwBoolean(1 == Double.valueOf(Math.random() * 10000).intValue() % 2);
            example.setWwByte(Double.valueOf(Math.random() * Byte.MAX_VALUE).byteValue());
            example.setWwShort(Double.valueOf(Math.random() * Short.MAX_VALUE).shortValue());
            example.setWwInt(Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue());
            example.setWwFloat(Float.valueOf(numberFormat.format((Math.random() * Float.MAX_VALUE))));
            example.setWwDouble(Double.valueOf(numberFormat.format((Math.random() * Double.MAX_VALUE))));
            example.setString(UUID.randomUUID().toString());
            example.setUtilDate(new Date());
            example.setSqlDate(new java.sql.Date(System.currentTimeMillis()));
            example.setTimestamp(new Timestamp(System.currentTimeMillis()));
            example.setClob(new SerialClob("nway".toCharArray()));
            example.setBlob(new SerialBlob("nway".getBytes()));
            example.setBigDecimal(BigDecimal.valueOf(Math.random() * 10000));
            example.setLocalDate(LocalDate.now());
            example.setLocalDateTime(LocalDateTime.now());
            example.setBigDecimal(BigDecimal.valueOf(Math.random() * Integer.MAX_VALUE));
//			example.setInputStream(Hibernate.getLobCreator(session).createBlob("nway".getBytes()).getBinaryStream());
            exampleEntityList.add(example);

        }

        long begin = System.currentTimeMillis();
        sqlExecutor.batchInsert(exampleEntityList);
        System.out.println("batchInsert = " + (System.currentTimeMillis() - begin));

    }
}