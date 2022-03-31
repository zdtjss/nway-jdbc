package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.sql.builder.ISqlBuilder;
import com.nway.spring.jdbc.sql.builder.QueryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class QueryBuilderTest {

    @Test
    public void getSqlTest() {

        Computer computer = new Computer();
        computer.setBrand("abc哈哈");

        ISqlBuilder builder = SQL.query(Computer.class).withColumn(Computer::getBrand).like(computer::getBrand).notLike(computer::getModel);

        System.out.println(builder.getSql());
        System.out.println(builder.getParam());

        builder = SQL.insert(Computer.class).use(new Computer());
    }

    @Test
    public void duration() {
        LocalDate begin = LocalDate.of(2020, 10, 11);
        System.out.println(ChronoUnit.DAYS.between(begin, LocalDate.now()));
    }

    @Test
    public void orExpTest() {
        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);

        queryBuilder.or((sql) -> sql.eq(Computer::getBrand, "abc").like(Computer::getKeyboardId, "aa"));

        Assertions.assertTrue(queryBuilder.getSql().endsWith(" from t_computer where ( brand = ? and keyboard_id like ?)"));
    }

    @Test
    public void orExpTest2() {
        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);

        queryBuilder.or((sql) -> sql.eq(Computer::getBrand, "abc").like(Computer::getMainframeId, "ddd")).eq(Computer::getModel, "123");

        Assertions.assertTrue(queryBuilder.getSql().endsWith(" from t_computer where ( brand = ? and mainframe_id like ?) and model = ?"));
    }

    @Test
    public void orExpTest3() {
        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);

        queryBuilder.eq(Computer::getId, 11).or((sql) -> sql.eq(Computer::getBrand, "abc")).eq(Computer::getModel, "aa");

        Assertions.assertTrue(queryBuilder.getSql().endsWith(" from t_computer where id = ? or ( brand = ?) and model = ?"));
    }

    @Test
    public void orExpTest4() {
        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);

        queryBuilder.or((sql) -> sql.eq(Computer::getBrand, "abc")).or().eq(Computer::getModel, "aa");

        Assertions.assertTrue(queryBuilder.getSql().endsWith(" from t_computer where ( brand = ?) or model = ?"));
    }

    @Test
    public void orExpTest5() {
        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);

        queryBuilder.or((sql) -> sql.eq(Computer::getBrand, "abc")).or((sql) -> sql.eq(Computer::getModel, "aa"));

        Assertions.assertTrue(queryBuilder.getSql().endsWith(" from t_computer where ( brand = ?) or ( model = ?)"));
    }

    @Test
    public void orTest() {
        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);
        queryBuilder.eq(Computer::getId, 123).or().eq(Computer::getBrand, "abc").eq(Computer::getModel, "aa");
        Assertions.assertTrue(queryBuilder.getSql().endsWith(" from t_computer where id = ? or brand = ? and model = ?"));
    }

    @Test
    public void andExpTest() {
        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);

        queryBuilder.and((sql) -> sql.eq(Computer::getBrand, "abc"));

        Assertions.assertTrue(queryBuilder.getSql().endsWith(" from t_computer where  brand = ?"));
    }

    @Test
    public void andExpTest2() {
        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);

        queryBuilder.and((sql) -> sql.eq(Computer::getBrand, "abc")).like(Computer::getKeyboardId, "dd");

        Assertions.assertTrue(queryBuilder.getSql().endsWith(" from t_computer where ( brand = ?) and keyboard_id like ?"));
    }

    @Test
    public void andExpTest3() {
        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);

        queryBuilder.eq(Computer::getId, 11).and((sql) -> sql.eq(Computer::getBrand, "abc")).eq(Computer::getModel, "aa");

        Assertions.assertTrue(queryBuilder.getSql().endsWith(" from t_computer where id = ? and ( brand = ?) and model = ?"));
    }

    @Test
    public void ignoreInvalidTest() {

        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);
        queryBuilder.le(Computer::getModel, null).eq(Computer::getBrand, "").eq(Computer::getId, "abc");

        String sql = queryBuilder.getSql();
        String where = sql.substring(sql.indexOf(" where "));

        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getModel) + " "));
        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getBrand) + " "));
        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getId) + " "));
    }

    @Test
    public void ignoreInvalidDeepTest2() {

        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);
        queryBuilder.ignoreInvalidDeep(true)
                .le(Computer::getModel, null)
                .eq(Computer::getBrand, "")
                .eq(Computer::getId, "abc")
                .in(Computer::getKeyboardId, Arrays.asList(null, null))
                .in(Computer::getMouseId, Collections.singleton(""));

        String sql = queryBuilder.getSql();
        String where = sql.substring(sql.indexOf(" where "));

        Assertions.assertFalse(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getModel) + " "));
        Assertions.assertFalse(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getBrand) + " "));
        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getId) + " "));
        Assertions.assertFalse(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getKeyboardId) + " "));
        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getMouseId) + " "));
    }

    @Test
    public void ignoreInvalidTest2() {

        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);
        queryBuilder.ignoreInvalid(true).le(Computer::getModel, null).eq(Computer::getBrand, "").eq(Computer::getId, "abc");

        String sql = queryBuilder.getSql();
        String where = sql.substring(sql.indexOf(" where "));

        Assertions.assertFalse(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getModel) + " "));
        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getBrand) + " "));
        Assertions.assertTrue(where.contains(" " + SqlBuilderUtils.getColumn(Computer.class, Computer::getId) + " "));
    }

    @Test
    public void permissionTest() {
        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);
        String sql = queryBuilder.getSql();
        String where = sql.substring(sql.indexOf(" where "));
        List<Object> params = queryBuilder.getParam();

        Assertions.assertTrue(where.contains("( production_date = ? or creator_id = ? )"));
        Assertions.assertEquals(2, params.size());
        Assertions.assertTrue(params.get(0).getClass().isAssignableFrom(Date.class));
        Assertions.assertTrue(params.get(1).getClass().isAssignableFrom(String.class));
    }
}
