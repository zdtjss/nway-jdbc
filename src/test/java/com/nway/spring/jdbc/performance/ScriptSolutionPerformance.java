package com.nway.spring.jdbc.performance;

import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.ibatis.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.nway.spring.jdbc.SqlExecutor;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;

@Service("scriptSolutionPerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class ScriptSolutionPerformance implements Performance, JsonQueryPerformance
{
    private ScriptEngine engine;
    
    private Gson gson = new Gson();
    
    @Value("script-sql.js")
    private String sqlConfigFile;
    
    @Autowired
    private SqlExecutor sqlExecutor;
    
    public String queryMonitorJsonList()
    {
        String jsonData = "[]";
        JdbcSql jdbcSql = getJdbcSql("listMonitor", null);
        
        if (jdbcSql.getSql().length() != 0)
        {
            jsonData = sqlExecutor.queryForJsonList(jdbcSql.getSql().toString(), jdbcSql.getCondition().toArray());
        }
        
        return jsonData;
    }
    
    private JdbcSql getJdbcSql(String functionName, Map<String, String> inParam)
    {
        
        JdbcSql jdbcSql = new JdbcSql();
        
        try
        {
            ((Invocable) engine).invokeFunction(functionName, inParam, jdbcSql);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch(ScriptException e) {
        	
        	e.printStackTrace();
        }
        
        return jdbcSql;
    }
    
    @PostConstruct
    public void afterPropertiesSet() throws Exception
    {
        
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByExtension("js");
        
        // evaluate JavaScript code that defines an object with one method
        engine.eval(new FileReader(Resources.getResourceAsFile(sqlConfigFile)));
    }
    
    @Override
    public Computer getComputerById(int id)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<Computer> listComputer()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Monitor getMonitorById(int id)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<Monitor> listMonitor()
    {
        List<Monitor> monitors = Collections.emptyList();
        
        JdbcSql jdbcSql = getJdbcSql("listMonitor", null);
        
        if (jdbcSql.getSql().length() != 0)
        {
            monitors = sqlExecutor.queryForBeanList(jdbcSql.getSql().toString(), jdbcSql.getCondition().toArray(), Monitor.class);
        }
        
        return monitors;
    }
    
    public String queryMonitorJsonList1()
    {
        return gson.toJson(listMonitor());
    }
    
}
