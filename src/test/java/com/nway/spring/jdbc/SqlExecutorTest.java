package com.nway.spring.jdbc;

import com.nway.spring.jdbc.pagination.Page;
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.builder.SqlBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

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

class SqlExecutorTest extends BaseTest {

    @Autowired
    private SqlExecutor sqlExecutor;

    @Test
    void update() {
        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));
        String str = UUID.randomUUID().toString();
        sqlExecutor.update(SQL.update(ExampleEntity.class).set(ExampleEntity::getString, str).eq(example::getId));
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
        ExampleEntity example = sqlExecutor.queryFirst(SQL.query(ExampleEntity.class));
        int effectCount = sqlExecutor.deleteById(example.getId(), ExampleEntity.class);
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
        exampleEntity.setId(Double.valueOf(Math.random() * Integer.MAX_VALUE).intValue());
        exampleEntity.setString(UUID.randomUUID().toString());
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

        ExampleEntity2 example = sqlExecutor.queryFirst(SQL.query(ExampleEntity2.class));

        ExampleEntity2 example1 = sqlExecutor.queryById(example.getId(), ExampleEntity2.class);

//        Assertions.assertEquals(example.getString(), example1.getString());
    }

    @Test
    void queryList() {
        List<ExampleEntity> objectList = sqlExecutor.queryList(SQL.query(ExampleEntity.class).le(ExampleEntity::getId, 100));
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
        Map<Integer, ExampleEntity> objectMap = sqlExecutor.queryListMap(SQL.query(ExampleEntity.class).le(ExampleEntity::getId, 100), ExampleEntity::getId);
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
        Assertions.assertEquals(mapPage.getPageData().get(0).get("pk_id"), example.getId());
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
        Page<ExampleEntity> mapPage = sqlExecutor.queryPage(SQL.query(ExampleEntity.class).eq(example::getId), 1, 1);
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
        obj.setId(obj.getId() + 1);
        sqlExecutor.insert(obj);
        Assertions.assertFalse(obj.getMv().isEmpty());
    }

    @Test
    void mvQueryTest() {
        mvInsertTest();
        List<String> fks = sqlExecutor.getJdbcTemplate().queryForList("select distinct fk from t_nway_mv", String.class);
        ExampleEntity obj = sqlExecutor.queryById(fks.get(0), ExampleEntity.class, ExampleEntity::getMv);
        Assertions.assertFalse(obj.getMv().isEmpty());
    }

    @Test
    void mvListTest() {
        List<String> fks = sqlExecutor.getJdbcTemplate().queryForList("select distinct fk from t_nway_mv limit 10", String.class);
        List<ExampleEntity> objectList = sqlExecutor.queryList(SQL.query(ExampleEntity.class).in(ExampleEntity::getId, fks));
        boolean anyMatch = objectList.stream().anyMatch(obj -> obj.getMv() != null && !obj.getMv().isEmpty());
        Assertions.assertTrue(anyMatch);
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
        Assertions.assertThrows(IncorrectResultSizeDataAccessException.class, () -> sqlExecutor.queryOne(SQL.query(ExampleEntity.class)));
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
    public void sqlTest() {

        ExampleEntity exampleEntity = new ExampleEntity();

        exampleEntity.setString("strcolumn");

        SqlBuilder builder = SQL.query(ExampleEntity.class)
                
                .eq(ExampleEntity::getId, 1)
                .eq(exampleEntity::getId)
                .eq("pk_id", exampleEntity.getId())
                .ne(ExampleEntity::getString, "ne")
                .ne(exampleEntity::getString)
                .ne("string", "str")
                .ge(ExampleEntity::getId, 11)
                .ge(exampleEntity::getId)
                .ge("pk_id", exampleEntity.getId())
                .gt(ExampleEntity::getId, 11)
                .gt(exampleEntity::getId)
                .gt("pk_id", exampleEntity.getId())
                .lt(ExampleEntity::getId, 11)
                .lt(exampleEntity::getId)
                .lt("pk_id", exampleEntity.getId())
                .le(ExampleEntity::getId, 11)
                .le(exampleEntity::getId)
                .le("pk_id", exampleEntity.getId())
                .between(ExampleEntity::getString, exampleEntity::getPpLong, exampleEntity::getPpDouble)
                .between(ExampleEntity::getPpDouble, exampleEntity.getPpDouble(), exampleEntity.getPpDouble())
                .between("p_double", 1, 2)
                .between("p_double", exampleEntity::getPpDouble, exampleEntity::getPpDouble)
                .notBetween(ExampleEntity::getPpDouble, exampleEntity::getPpLong, exampleEntity::getPpLong)
                .notBetween(ExampleEntity::getPpDouble, exampleEntity.getPpDouble(), exampleEntity.getPpDouble())
                .notBetween("p_double", 1, 2)
                .notBetween("p_double", exampleEntity::getPpDouble, exampleEntity::getPpDouble)
                .like(exampleEntity::getString)
                .like("string", exampleEntity.getString())
                .like(ExampleEntity::getString, exampleEntity::getString)
                .like(ExampleEntity::getString, exampleEntity.getString())
                .notLike(exampleEntity::getString)
                .notLike("string", exampleEntity.getString())
                .notLike(ExampleEntity::getString, exampleEntity::getString)
                .notLike(ExampleEntity::getString, exampleEntity.getString())
                .likeLeft(exampleEntity::getString)
                .likeLeft("string", exampleEntity.getString())
                .likeLeft(ExampleEntity::getString, exampleEntity::getString)
                .likeLeft(ExampleEntity::getString, exampleEntity.getString())
                .likeRight(exampleEntity::getString)
                .likeRight("string", exampleEntity.getString())
                .likeRight(ExampleEntity::getString, exampleEntity::getString)
                .likeRight(ExampleEntity::getString, exampleEntity.getString())
                .in(ExampleEntity::getString, Collections.singletonList("aa"))
                .in("string", Collections.singletonList("aa"))
                .notIn(ExampleEntity::getString, Collections.singletonList("aa"))
                .notIn("string", Collections.singletonList("aa"))
                .or()
                .eq(exampleEntity::getId)
                .or(e -> e.eq(exampleEntity::getId).ne(exampleEntity::getId))
                .and(e -> e.eq(ExampleEntity::getId, 1))
                .groupBy(ExampleEntity::getUtilDate)
                .having(e -> e.eq(ExampleEntity::getString, 100))
//				.orderBy(ExampleEntity::getString, ExampleEntity::getPpDouble)
//				.appendOrderBy("string", "p_double")
                .orderByDesc(ExampleEntity::getString, ExampleEntity::getPpDouble)
                .andOrderByDesc("string", "p_double")
                .andOrderByAsc(ExampleEntity::getString)
                .andOrderByDesc(ExampleEntity::getPpDouble);

        sqlExecutor.queryFirst(builder);

        builder = SQL.query(ExampleEntity.class).orderByDesc(ExampleEntity::getUtilDate);
        sqlExecutor.queryFirst(builder);
    }

    @Test
    void groupSqlTest() {
        ExampleEntity exampleEntity = new ExampleEntity();

        exampleEntity.setString("strcolumn");

        SqlBuilder builder = SQL.query(ExampleEntity.class).withColumn(ExampleEntity::getString, ExampleEntity::getUtilDate)
                .groupBy(ExampleEntity::getString, ExampleEntity::getUtilDate)
//				.groupBy("string", "p_double")
                .having(e -> e.eq(exampleEntity::getString).ne(exampleEntity::getUtilDate));
        sqlExecutor.queryFirst(builder);
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