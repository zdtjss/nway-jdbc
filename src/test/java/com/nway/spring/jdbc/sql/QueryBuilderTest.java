package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.ExampleEntity;
import com.nway.spring.jdbc.SqlExecutor;
import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.annotation.enums.ColumnType;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.sql.builder.ISqlBuilder;
import com.nway.spring.jdbc.sql.builder.QueryBuilder;
import com.nway.spring.jdbc.sql.builder.UpdateBuilder;
import com.nway.spring.jdbc.sql.permission.WhereCondition;
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
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SpringJUnitConfig(classes = QueryBuilderTest.Config.class)
public class QueryBuilderTest {

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
        @DependsOn("dataSource")
        public SqlExecutor sqlExecutor() {
            return new SqlExecutor(dataSource);
        }

    }

    @Test
    public void getSqlTest() {

        Computer computer = new Computer();
        computer.setBrand("abc哈哈");

        ISqlBuilder builder = SQL.query(Computer.class).withColumn(Computer::getBrand).like(computer::getBrand).notLike(computer::getModel);

        System.out.println(builder.getSql());
        System.out.println(builder.getParam());

        builder = SQL.insert(Computer.class).use(new Computer());

        System.out.println(builder.getSql());
        System.out.println(builder.getParam());
    }

    @Test
    public void duration() {
        LocalDate begin = LocalDate.of(2020, 10, 11);
        System.out.println(ChronoUnit.DAYS.between(begin, LocalDate.now()));
    }

    @Test
    public void customColumn() {
        QueryBuilder queryBuilder = SQL.query(Computer.class);
        queryBuilder.withColumn("max(id)");
        Assertions.assertTrue(queryBuilder.getSql().contains("select max(id) from t_computer where "));
    }

    @Test
    public void customCondition() {
        QueryBuilder queryBuilder = SQL.query(Computer.class);
        WhereCondition condition = new WhereCondition();
        condition.setExpr(SqlBuilderUtils.getColumn(Computer::getProductionDate) + " >= now()");
        condition.setValue(Collections.emptyList());
        queryBuilder.appendCondition(condition);
        Assertions.assertTrue(queryBuilder.getSql().contains(" production_date >= now() "));
    }

    @Test
    public void orExpTest() {
        QueryBuilder queryBuilder = SQL.query(Computer.class);

        queryBuilder.or((sql) -> sql.eq(Computer::getBrand, "abc").like(Computer::getKeyboardId, "aa"));

        String sql = queryBuilder.getSql();
        Assertions.assertTrue(sql.contains(" from t_computer where ( brand = ? and keyboard_id like ?)"));

        Assertions.assertEquals(countQuestionMark(sql), queryBuilder.getParam().size());
    }

    @Test
    public void orExpTest2() {
        QueryBuilder queryBuilder = SQL.query(Computer.class);

        queryBuilder.or((sql) -> sql.eq(Computer::getBrand, "abc").like(Computer::getMainframeId, "ddd")).eq(Computer::getModel, "123");

        Assertions.assertTrue(queryBuilder.getSql().contains(" from t_computer where ( brand = ? and mainframe_id like ?) and model = ?"));
    }

    @Test
    public void orExpTest3() {
        QueryBuilder queryBuilder = SQL.query(Computer.class);

        queryBuilder.eq(Computer::getId, 11).or((sql) -> sql.eq(Computer::getBrand, "abc")).eq(Computer::getModel, "aa");

        Assertions.assertTrue(queryBuilder.getSql().contains(" from t_computer where id = ? or ( brand = ?) and model = ?"));
    }

    @Test
    public void orExpTest4() {
        QueryBuilder queryBuilder = SQL.query(Computer.class);

        queryBuilder.or((sql) -> sql.eq(Computer::getBrand, "abc")).or().eq(Computer::getModel, "aa");

        Assertions.assertTrue(queryBuilder.getSql().contains(" from t_computer where ( brand = ?) or model = ?"));
    }

    @Test
    public void orExpTest5() {
        QueryBuilder queryBuilder = SQL.query(Computer.class);

        queryBuilder.or((sql) -> sql.eq(Computer::getBrand, "abc")).or((sql) -> sql.eq(Computer::getModel, "aa"));

        Assertions.assertTrue(queryBuilder.getSql().contains(" from t_computer where ( brand = ?) or ( model = ?)"));
    }

    @Test
    public void orTest() {
        QueryBuilder queryBuilder = SQL.query(Computer.class);
        queryBuilder.eq(Computer::getId, 123).or().eq(Computer::getBrand, "abc").eq(Computer::getModel, "aa");
        Assertions.assertTrue(queryBuilder.getSql().contains(" from t_computer where id = ? or brand = ? and model = ?"));
    }

    @Test
    public void andExpTest() {
        QueryBuilder queryBuilder = SQL.query(Computer.class);

        queryBuilder.and((sql) -> sql.eq(Computer::getBrand, "abc"));

        Assertions.assertTrue(queryBuilder.getSql().contains(" from t_computer where ( brand = ?)"));
    }

    @Test
    public void andExpTest2() {
        QueryBuilder queryBuilder = SQL.query(Computer.class);

        queryBuilder.and((sql) -> sql.eq(Computer::getBrand, "abc")).like(Computer::getKeyboardId, "dd");

        Assertions.assertTrue(queryBuilder.getSql().contains(" from t_computer where ( brand = ?) and keyboard_id like ?"));
    }

    @Test
    public void andExpTest3() {
        QueryBuilder queryBuilder = SQL.query(Computer.class);

        queryBuilder.eq(Computer::getId, 11).and((sql) -> sql.eq(Computer::getBrand, "abc")).eq(Computer::getModel, "aa");

        Assertions.assertTrue(queryBuilder.getSql().contains(" from t_computer where id = ? and ( brand = ?) and model = ?"));
    }

    @Test
    public void ignoreInvalidTest() {

        QueryBuilder queryBuilder = SQL.query(Computer.class);
        queryBuilder.le(Computer::getModel, null).eq(Computer::getBrand, "").eq(Computer::getId, "abc");

        String sql = queryBuilder.getSql();
        String where = sql.substring(sql.indexOf(" where "));

        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getModel) + " "));
        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getBrand) + " "));
        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getId) + " "));
    }

    @Test
    public void ignoreInvalidDeepTest2() {

        QueryBuilder queryBuilder = SQL.query(Computer.class);
        queryBuilder.ignoreInvalidDeep(true)
                .le(Computer::getModel, null)
                .eq(Computer::getBrand, "")
                .eq(Computer::getId, "abc")
                .in(Computer::getMainframeId, Arrays.asList(null, null))
                .in(Computer::getMouseId, Collections.singleton(""));

        String sql = queryBuilder.getSql();
        String where = sql.substring(sql.indexOf(" where "));

        Assertions.assertFalse(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getModel) + " "));
        Assertions.assertFalse(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getBrand) + " "));
        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getId) + " "));
        Assertions.assertFalse(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getMainframeId) + " "));
        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getMouseId) + " "));
    }

    @Test
    public void ignoreInvalidTest2() {

        QueryBuilder queryBuilder = SQL.query(Computer.class);
        queryBuilder.ignoreInvalid(true).le(Computer::getModel, null).eq(Computer::getBrand, "").eq(Computer::getId, "abc");

        String sql = queryBuilder.getSql();
        String where = sql.substring(sql.indexOf(" where "));

        Assertions.assertFalse(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getModel) + " "));
        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getBrand) + " "));
        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getId) + " "));
    }

    @Test
    public void permissionTest() {
        QueryBuilder queryBuilder = SQL.query(Computer.class);
        String sql = queryBuilder.getSql();
        String where = sql.substring(sql.indexOf(" where "));
        List<Object> params = queryBuilder.getParam();

        Assertions.assertTrue(where.contains("( production_date <> ? or keyboard_id <> ? )"));
        Assertions.assertEquals(2, params.size());
        Assertions.assertTrue(params.get(0).getClass().isAssignableFrom(Date.class));
        Assertions.assertTrue(params.get(1).getClass().isAssignableFrom(String.class));

        queryBuilder = SQL.query(Computer.class);
        queryBuilder.ignorePermission();
        sql = queryBuilder.getSql();
        params = queryBuilder.getParam();

        Assertions.assertEquals(-1, sql.indexOf(" where "));
        Assertions.assertEquals(0, params.size());

    }

    @Test
    public void permissionUpdateTest() {
        UpdateBuilder queryBuilder = SQL.update(Computer.class).set(Computer::getBrand, "aa");
        String sql = queryBuilder.getSql();
        String where = sql.substring(sql.indexOf(" where "));
        List<Object> params = queryBuilder.getParam();

        Assertions.assertTrue(where.contains("( production_date <> ? or keyboard_id <> ? )"));
        Assertions.assertEquals(3, params.size());
        Assertions.assertTrue(params.get(1).getClass().isAssignableFrom(Date.class));
        Assertions.assertTrue(params.get(2).getClass().isAssignableFrom(String.class));

        queryBuilder = SQL.update(Computer.class).set(Computer::getBrand, "aa");
        queryBuilder.ignorePermission();
        sql = queryBuilder.getSql();
        params = queryBuilder.getParam();

        Assertions.assertEquals(-1, sql.indexOf(" where "));
        Assertions.assertEquals(1, params.size());

    }

    @Test
    public void excludeColumnTest() {
        QueryBuilder queryBuilder = SQL.query(Computer.class).excludeColumn(Computer::getMainframeId);
        String sql = queryBuilder.getSql();
        String selectColumn = sql.substring(0, sql.indexOf(" from "));
        String excludeColumn = SqlBuilderUtils.getColumn(Computer::getMainframeId);
        Assertions.assertFalse(selectColumn.contains(excludeColumn));
    }

    @Test
    public void withMVColumnTest() {

        String mvColumn = SqlBuilderUtils.getColumn(Computer::getUserList);

        QueryBuilder queryBuilder = SQL.query(Computer.class);
        String sql = queryBuilder.getSql();
        String selectColumn = sql.substring(0, sql.indexOf(" from "));
        Assertions.assertFalse(selectColumn.contains(mvColumn));

        queryBuilder = SQL.query(Computer.class).withMVColumn(Computer::getUserList);
        sql = queryBuilder.getSql();
        selectColumn = sql.substring(0, sql.indexOf(" from "));
        Assertions.assertFalse(selectColumn.contains(mvColumn));
        Assertions.assertTrue(queryBuilder.getMultiValColumn().size() == 1 && queryBuilder.getMultiValColumn().contains(mvColumn));
    }

    @Test
    public void groupByTest() {

        QueryBuilder queryBuilder = SQL.query(Computer.class).groupBy(Computer::getBrand);
        String sql = queryBuilder.getSql();
        int groupByIdx = sql.indexOf(" group by ");
        Assertions.assertTrue(groupByIdx > 0);

        String groupExp = sql.substring(groupByIdx);
        String groupColumn = SqlBuilderUtils.getColumn(Computer::getBrand);

        Assertions.assertTrue(groupExp.contains(groupColumn));
    }

    @Test
    public void havingTest() {
        QueryBuilder queryBuilder = SQL.query(Computer.class)
                .having(builder -> builder.eq(Computer::getBrand, "0").ne(Computer::getMainframeId, "b"));
        String sql = queryBuilder.getSql();
        int havingByIdx = sql.indexOf(" having ");
        Assertions.assertTrue(havingByIdx > 0);

        String havingExp = sql.substring(havingByIdx);

        Assertions.assertEquals(havingExp, " having brand = ? and mainframe_id <> ?");
    }

    @Test
    public void orderByTest() {

        QueryBuilder queryBuilder = SQL.query(Computer.class).orderBy(Computer::getBrand);
        String sql = queryBuilder.getSql();
        int orderByIdx = sql.indexOf(" order by ");
        Assertions.assertTrue(orderByIdx > 0);

        String orderExp = sql.substring(orderByIdx);
        String orderColumn = SqlBuilderUtils.getColumn(Computer::getBrand);

        Assertions.assertTrue(orderExp.contains(orderColumn));
    }

    @Test
    public void orderByTest2() {

        QueryBuilder queryBuilder = SQL.query(Computer.class).orderBy(Computer::getBrand).andOrderByAsc(Computer::getModel);
        String sql = queryBuilder.getSql();
        int orderByIdx = sql.indexOf(" order by ");
        Assertions.assertTrue(orderByIdx > 0);

        String orderExp = sql.substring(orderByIdx);

        Assertions.assertEquals(orderExp, " order by brand,model asc");
    }


    @Test
    public void sqlTest() {

        ExampleEntity exampleEntity = new ExampleEntity();

        exampleEntity.setString("strcolumn");

        QueryBuilder builder = SQL.query(ExampleEntity.class);

        builder.distinct()
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
                .isNull(ExampleEntity::getString)
                .isNull(exampleEntity::getString)
                .isNull("pk_id")
                .isNotNull(ExampleEntity::getString)
                .isNotNull(exampleEntity::getString)
                .isNull("pk_id")
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
//                .groupBy(ExampleEntity::getUtilDate)
//                .having(e -> e.eq(ExampleEntity::getString, 100))
//				.orderBy(ExampleEntity::getString, ExampleEntity::getPpDouble)
//				.appendOrderBy("string", "p_double")
                .orderByDesc(ExampleEntity::getString, ExampleEntity::getPpDouble)
                .andOrderByDesc("string", "p_double")
                .andOrderByAsc(ExampleEntity::getString)
                .andOrderByDesc(ExampleEntity::getPpDouble);

        ExampleEntity first = sqlExecutor.queryFirst(builder);
        Assertions.assertNull(first);

        QueryBuilder builder2 = SQL.query(ExampleEntity.class).orderByDesc(ExampleEntity::getUtilDate);
        first = sqlExecutor.queryFirst(builder2);
        Assertions.assertNotNull(first);
    }

    @Test
    void groupSqlTest() {
        ExampleEntity exampleEntity = new ExampleEntity();

        exampleEntity.setString("strcolumn");

        QueryBuilder builder = SQL.query(ExampleEntity.class).withColumn(ExampleEntity::getString, ExampleEntity::getUtilDate)
                .isNull(ExampleEntity::getId)
                .groupBy(ExampleEntity::getString, ExampleEntity::getUtilDate)
//				.groupBy("string", "p_double")
                .having(e -> e.eq(exampleEntity::getString).isNotNull(exampleEntity::getUtilDate));
        ExampleEntity first = sqlExecutor.queryFirst(builder);
        Assertions.assertNull(first);
    }

    @Test
    public void noneParamQuery() {

        QueryBuilder queryBuilder = SQL.query(Pure.class)
                .isNull(Pure::getId)
                .isNotNull(Pure::getString);

        List<Pure> list = sqlExecutor.queryList(queryBuilder);
        Assertions.assertEquals(list.size(), 0);
    }

    @Data
    @Table("t_nway")
    public static class Pure {
        @Column(name = "pk_id", type = ColumnType.ID)
        private Integer id;
        private String string;
    }

    private int countQuestionMark(String str) {

        int count = 0;
        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (str.charAt(i) == '?') {
                count++;
            }
        }
        return count;
    }
}
