package com.nway.spring.jdbc.performance;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;
import com.nway.spring.jdbc.performance.mapper.ComputerMapper;

@Service("myBatisPerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class MyBatisPerformance implements Performance {

	@Autowired
	private ComputerMapper computerMapper;

	@Override
	public Computer getComputerById(int id) {
		
		return computerMapper.getComputerById(id);
	}

	@Override
	public List<Computer> listComputer() {

		return computerMapper.listComputer();
	}

	@Override
	public Monitor getMonitorById(int id) {
	    
		return computerMapper.getMonitorById(id);
	}

	@Override
	public List<Monitor> listMonitor() {
		
		return computerMapper.listMonitor();
	}

}
