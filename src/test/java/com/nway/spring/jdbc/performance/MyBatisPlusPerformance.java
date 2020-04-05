package com.nway.spring.jdbc.performance;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nway.spring.jdbc.performance.entity.*;
import com.nway.spring.jdbc.performance.mybatisplus.*;
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.builder.QueryBuilder;
import com.nway.spring.jdbc.sql.builder.SqlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;

@Service("myBatisPlusPerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class MyBatisPlusPerformance implements Performance {

    private Gson gson = new Gson();
    
	@Autowired
	private MonitorDao monitorDao;
	@Autowired
	private ComputerDao computerDao;
	@Autowired
	private KeyboardDao keyboardDao;
	@Autowired
	private MainframeDao mainframeDao;
	@Autowired
	private MouseDao mouseDao;
	@Autowired
	private ComputerSoftwareDao computerSoftwareDao;
	@Autowired
	private SoftwareDao softwareDao;

	@Override
	public Computer getComputerById(int id) {

		LambdaQueryWrapper<Mainframe> mainframeSql = Wrappers.lambdaQuery();
		LambdaQueryWrapper<Monitor> monitorSql = Wrappers.lambdaQuery();
		LambdaQueryWrapper<Mouse> mouseSql = Wrappers.lambdaQuery();
		LambdaQueryWrapper<Keyboard> keyboardSql = Wrappers.lambdaQuery();
		LambdaQueryWrapper<Software> softwareSql = Wrappers.lambdaQuery();

		Computer computer = computerDao.selectById(id);
		computer.setMainframe(mainframeDao.selectOne(mainframeSql.eq(Mainframe::getId, computer.getMainframeId()) ));
		computer.setMonitor(monitorDao.selectOne(monitorSql.eq(Monitor::getId, computer.getMonitorId())));
		computer.setMouse(mouseDao.selectOne(mouseSql.eq(Mouse::getId, computer.getMouseId())));
		computer.setKeyboard(keyboardDao.selectOne(keyboardSql.eq(Keyboard::getId, computer.getKeyboardId())));

		LambdaQueryWrapper<ComputerSoftware> computerSoftwareSql = Wrappers.lambdaQuery();
		computerSoftwareSql.eq(ComputerSoftware::getComputerId, computer.getId());
		List<ComputerSoftware> computerSoftwareList = computerSoftwareDao.selectList(computerSoftwareSql);
		List<Integer> softwareIdList = computerSoftwareList.stream().map(e -> e.getSoftwareId()).collect(Collectors.toList());
		computer.setSoftware(softwareDao.selectList(softwareSql.in(Software::getId, softwareIdList)));

		return computer;
	}

	@Override
	public List<Computer> listComputer() {

		LambdaQueryWrapper<Computer> computerSql = Wrappers.lambdaQuery(Computer.class);
		LambdaQueryWrapper<Mainframe> mainframeSql = Wrappers.lambdaQuery();
		LambdaQueryWrapper<Monitor> monitorSql = Wrappers.lambdaQuery();
		LambdaQueryWrapper<Mouse> mouseSql = Wrappers.lambdaQuery();
		LambdaQueryWrapper<Keyboard> keyboardSql = Wrappers.lambdaQuery();
		LambdaQueryWrapper<Software> softwareSql = Wrappers.lambdaQuery();

		List<Computer> computers = computerDao.selectList(computerSql);

		for (Computer computer : computers) {

			computer.setMainframe(mainframeDao.selectOne(mainframeSql.eq(Mainframe::getId, computer.getMainframeId()) ));
			computer.setMonitor(monitorDao.selectOne(monitorSql.eq(Monitor::getId, computer.getMonitorId())));
			computer.setMouse(mouseDao.selectOne(mouseSql.eq(Mouse::getId, computer.getMouseId())));
			computer.setKeyboard(keyboardDao.selectOne(keyboardSql.eq(Keyboard::getId, computer.getKeyboardId())));

			LambdaQueryWrapper<ComputerSoftware> computerSoftwareSql = Wrappers.lambdaQuery();
			computerSoftwareSql.eq(ComputerSoftware::getComputerId, computer.getId());
			List<ComputerSoftware> computerSoftwareList = computerSoftwareDao.selectList(computerSoftwareSql);
			List<Integer> softwareIdList = computerSoftwareList.stream().map(e -> e.getSoftwareId()).collect(Collectors.toList());
			computer.setSoftware(softwareDao.selectList(softwareSql.in(Software::getId, softwareIdList)));
		}

		return computers;
	}

	@Override
	public Monitor getMonitorById(int id) {
	    
		return monitorDao.selectById(id);
	}

	@Override
	public List<Monitor> listMonitor() {
		
		return monitorDao.selectList(Wrappers.query());
	}

}
