package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.sql.builder.ISqlBuilder;
import com.nway.spring.jdbc.sql.builder.QueryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

        queryBuilder.or((sql) -> sql.eq(Computer::getBrand, "abc"));

        Assertions.assertTrue(queryBuilder.getSql().endsWith(" from t_computer where  brand = ?"));
    }

    @Test
    public void orExpTest2() {
        QueryBuilder<Computer> queryBuilder = SQL.query(Computer.class);

        queryBuilder.eq(Computer::getId, 11).or((sql) -> sql.eq(Computer::getBrand, "abc")).eq(Computer::getModel, "aa");

        Assertions.assertTrue(queryBuilder.getSql().endsWith(" from t_computer where id = ? or ( brand = ?) and model = ?"));
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

        queryBuilder.eq(Computer::getId, 11).and((sql) -> sql.eq(Computer::getBrand, "abc")).eq(Computer::getModel, "aa");

        Assertions.assertTrue(queryBuilder.getSql().endsWith(" from t_computer where id = ? and  ( brand = ?) and model = ?"));
    }

}
