package com.nway.spring.jdbc.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;

import com.nway.spring.jdbc.json.JsonBuilder;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;

@Service("jdbcPerformance")
public class JdbcPerformance extends JsonBuilder implements Performance, JsonQueryPerformance {

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
        
        ResultSet rs = null;
        PreparedStatement stmt = null;
        
        try (Connection con = dataSource.getConnection();)
        {
            con.setReadOnly(true);
            stmt = con.prepareStatement("select * from t_monitor");
            rs = stmt.executeQuery();
                    
            while(rs.next()) {
                
                monitors.add(createMonitor(rs));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(stmt);
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
    
    @Override
    public String queryMonitorJsonList()
    {
        StringBuilder json = new StringBuilder(1000);
        
        ResultSet rs = null;
        PreparedStatement stmt = null;
        
        try (Connection con = dataSource.getConnection();)
        {
            con.setReadOnly(true);
            stmt = con.prepareStatement("select * from t_monitor");
            rs = stmt.executeQuery();
                    
            while(rs.next()) {
                
                json.append(buildJson(rs));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(stmt);
        }
        
        return json.toString();
    }
    
    protected String buildJson(ResultSet rs) throws SQLException
    {
        StringBuilder json = new StringBuilder(100);
        json.append("{");
        json.append(",\"id\":");
        integerValue(rs.getInt(1), rs.wasNull(), json);
        json.append(",\"brand\":");
        stringValue(rs.getString(2), json);
        json.append(",\"max_resolution\":");
        stringValue(rs.getString(3), json);
        json.append(",\"model\":");
        stringValue(rs.getString(4), json);
        json.append(",\"photo\":");
        json.append('"').append(rs.getObject(5)).append('"');
        json.append(",\"price\":");
        doubleValue(rs.getDouble(6), rs.wasNull(), json);
        json.append(",\"production_date\":");
        dateValue(rs.getTimestamp(7), "yyyy-MM-dd HH:mm:ss.SSS", json);
        json.append(",\"type\":");
        integerValue(rs.getInt(8), rs.wasNull(), json);
        if (json.length() > 1)
        {
            json = json.deleteCharAt(1);
        }
        json.append('}');
        return json.toString();
    }
}
