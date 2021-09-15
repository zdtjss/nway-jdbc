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

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;

@Service("jdbcPerformance")
public class JdbcPerformance implements Performance, JsonQueryPerformance {

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
        List<Monitor> monitors = new ArrayList<Monitor>();
        
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        
        try
        {
        	con = dataSource.getConnection();
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
            JdbcUtils.closeConnection(con);
        }
        
        return monitors;
    }
    
    private Monitor createMonitor(ResultSet rs) throws SQLException {
        
        Monitor monitor = new Monitor();
        
        monitor.setId(rs.getInt(1));
        monitor.setBrand(rs.getString(2));
        monitor.setMaxResolution(rs.getString(3));
        monitor.setModel(rs.getString(4));
//        monitor.setPhoto(rs.getBytes(5));
        monitor.setPrice(rs.getFloat(6));
        monitor.setProductionDate(rs.getTimestamp(7));
        monitor.setType(rs.getInt(8));
        
        return monitor;
    }

	@Override
	public String queryMonitorJsonList() {

		return null;
	}
    
}
