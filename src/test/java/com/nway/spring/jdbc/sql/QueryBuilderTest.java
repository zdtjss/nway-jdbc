package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.sql.builder.ISqlBuilder;
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
}
