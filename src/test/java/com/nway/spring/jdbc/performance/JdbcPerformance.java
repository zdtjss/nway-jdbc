package com.nway.spring.jdbc.performance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;

@Service("jdbcPerformance")
public class JdbcPerformance implements Performance
{
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Computer getComputerById(int id)
    {
        return null;
    }
    
    @Override
    public List<Computer> listComputer()
    {
        return null;
    }
    
    @Override
    public Monitor getMonitorById(int id)
    {
        return null;
    }
    
    @Override
    public List<Monitor> listMonitor()
    {
        List<Monitor> monitors = new ArrayList<>();
        
        try (Connection con = dataSource.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select * from t_monitor"))
        {
            while(rs.next()) {
                
                monitors.add(createMonitor(rs));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return monitors;
    }
    
    private Monitor createMonitor(ResultSet rs) throws SQLException {
        
        Monitor monitor = new Monitor();
        
        monitor.setId(rs.getInt(1));
        monitor.setBrand(rs.getString(2));
        monitor.setMaxResolution(rs.getString(3));
        monitor.setModel(rs.getString(4));
        monitor.setPhoto(rs.getBytes(5));
        monitor.setPrice(rs.getFloat(6));
        monitor.setProductionDate(rs.getTimestamp(7));
        monitor.setType(rs.getInt(8));
        
        return monitor;
    }
}
