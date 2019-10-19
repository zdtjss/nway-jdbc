package com.nway.spring.jdbc.performance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;
import com.nway.spring.jdbc.performance.mybatisplus.ComputerDao;
import com.nway.spring.jdbc.performance.mybatisplus.MonitorDao;

@Service("myBatisPlusPerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class MyBatisPlusPerformance implements Performance , JsonQueryPerformance {

    private Gson gson = new Gson();
    
	@Autowired
	private MonitorDao monitorDao;
	@Autowired
	private ComputerDao computerDao;
	
	@Override
	public Computer getComputerById(int id) {
		
		return computerDao.selectById(id);
	}

	@Override
	public List<Computer> listComputer() {
		
		return computerDao.selectList(Wrappers.query());
	}

	@Override
	public Monitor getMonitorById(int id) {
	    
		return monitorDao.selectById(id);
	}

	@Override
	public List<Monitor> listMonitor() {
		
		return monitorDao.selectList(Wrappers.query());
	}

    @Override
    public String queryMonitorJsonList()
    {
        return gson.toJson(listMonitor());
    }

}
