package com.nway.spring.jdbc.performance;

import com.nway.spring.jdbc.SqlExecutor;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;
import org.apache.ibatis.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service("scriptSolutionPerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class ScriptSolutionPerformance implements Performance, JsonQueryPerformance
{
    private ScriptEngine engine;
    
    @Value("script-sql.js")
    private String sqlConfigFile;
    
    @Autowired
    private SqlExecutor sqlExecutor;
    
    private JdbcSql getJdbcSql(String functionName, Map<String, String> inParam)
    {
        
        JdbcSql jdbcSql = new JdbcSql();
        
        try
        {
            ((Invocable) engine).invokeFunction(functionName, inParam, jdbcSql);
        }
        catch (NoSuchMethodException | ScriptException e)
        {
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
            monitors = sqlExecutor.queryList(jdbcSql.getSql().toString(), Monitor.class, jdbcSql.getCondition().toArray());
        }
        
        return monitors;
    }
    
	@Override
	public String queryMonitorJsonList() {

		return null;
	}
    
}
