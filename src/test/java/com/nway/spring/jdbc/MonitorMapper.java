package com.nway.spring.jdbc;

import com.nway.spring.jdbc.bean.processor.RowMapper;
import com.nway.spring.jdbc.performance.entity.Monitor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MonitorMapper extends RowMapper<Monitor> {

    @Override
    public Monitor mapRow(ResultSet rs) throws SQLException {

        Monitor monitor = new Monitor();

        monitor.setType(rs.getInt(1));
        monitor.setPhoto(rs.getBytes(2));
        monitor.setPrice(rs.getFloat(3));
        monitor.setId(rs.getInt(15));

        return monitor;
    }
}
