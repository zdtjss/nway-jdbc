package com.nway.spring.jdbc;

import com.nway.spring.jdbc.pagination.Page;
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.builder.SqlBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
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
        Page<ExampleEntity> mapPage = sqlExecutor.queryPage(SQL.query(ExampleEntity.class).where().eq(example::getId), 1, 1);
        Assertions.assertEquals(mapPage.getPageData().size(), 1);
        Assertions.assertEquals(mapPage.getPageData().get(0).getId(), example.getId());
    }

    @Test
    void count() {
        int count = sqlExecutor.count(SQL.query(ExampleEntity.class));
        Assertions.assertTrue(count > 0);
        count = sqlExecutor.count(SQL.query(ExampleEntity.class).where().eq(ExampleEntity::getString, UUID.randomUUID().toString()));
        Assertions.assertEquals(0, count);
    }

    @Test
    void exist() {
        boolean exist = sqlExecutor.exist(SQL.query(ExampleEntity.class));
        Assertions.assertTrue(exist);
        exist = sqlExecutor.exist(SQL.query(ExampleEntity.class).where().eq(ExampleEntity::getString, UUID.randomUUID().toString()));
        Assertions.assertFalse(exist);
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
    public void sqlTest() {

        ExampleEntity exampleEntity = new ExampleEntity();

        exampleEntity.setString("strcolumn");

        SqlBuilder builder = SQL.query(ExampleEntity.class)
                .where()
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
//				.orderBy(ExampleEntity::getString, ExampleEntity::getPpDouble)
//				.appendOrderBy("string", "p_double")
                .orderByDesc(ExampleEntity::getString, ExampleEntity::getPpDouble)
                .andOrderByDesc("string", "p_double")
                .andOrderByAsc(ExampleEntity::getString)
                .andOrderByDesc(ExampleEntity::getPpDouble);

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

}