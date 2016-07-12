package com.nway.spring.jdbc.performance;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.nway.spring.jdbc.SqlExecutor;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;

@Service("scriptPerformance")
public class ScriptSolutionPerformance implements Performance {

	private ScriptEngine engine;
	
	@Value("script-sql.js")
	private String sqlConfigFile;

	@Autowired
	private SqlExecutor sqlExecutor;

	public List<Monitor> listMonitor() {
	    
		StringBuilder sql = new StringBuilder();
		List<Object> sqlParam = new ArrayList<>();
		Map<String,String> inParam = new HashMap<>();
		    
		try
        {
            ((Invocable) engine).invokeFunction("listMonitor", inParam, sql, sqlParam);
        }
        catch (NoSuchMethodException | ScriptException e)
        {
            e.printStackTrace();
        }
		
        if (sql.length() != 0)
        {
            sqlExecutor.queryForJson(sql.toString(), sqlParam.toArray());
        }
        
        return null;
	}

	@PostConstruct
	public void afterPropertiesSet() throws Exception {

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

}
