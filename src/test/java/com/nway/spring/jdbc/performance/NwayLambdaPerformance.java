package com.nway.spring.jdbc.performance;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.nway.spring.jdbc.SqlExecutor;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.ComputerSoftware;
import com.nway.spring.jdbc.performance.entity.Keyboard;
import com.nway.spring.jdbc.performance.entity.Mainframe;
import com.nway.spring.jdbc.performance.entity.Monitor;
import com.nway.spring.jdbc.performance.entity.Mouse;
import com.nway.spring.jdbc.performance.entity.Software;
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.builder.SqlBuilder;
import com.nway.spring.jdbc.sql.builder.QueryBuilder;
import com.nway.spring.jdbc.sql.builder.ISqlBuilder;

@Service("nwayLambdaPerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class NwayLambdaPerformance implements Performance {

    private Gson gson = new Gson();
    
	@Autowired
	private SqlExecutor sqlExecutor;

	@Override
	public Computer getComputerById(int id) {

		ISqlBuilder computerSql = SQL.query(Computer.class).eq(Computer::getId, id);
		QueryBuilder mainframeSql = SQL.query(Mainframe.class);
		QueryBuilder monitorSql = SQL.query(Monitor.class);
		QueryBuilder mouseSql = SQL.query(Mouse.class);
		QueryBuilder keyboardSql = SQL.query(Keyboard.class);
		QueryBuilder softwareSql = SQL.query(Software.class);
		SqlBuilder somputerSoftwareSql = SQL.query(ComputerSoftware.class).eq(ComputerSoftware::getComputerId, id);
		
		Computer computer = sqlExecutor.queryBean(computerSql);

		computer.setMainframe(sqlExecutor.queryBean(mainframeSql.eq(Mainframe::getId, computer.getMainframeId()) ));
		computer.setMonitor(sqlExecutor.queryBean(monitorSql.eq(Monitor::getId, computer.getMonitorId())));
		computer.setMouse(sqlExecutor.queryBean(mouseSql.eq(Mouse::getId, computer.getMouseId())));
		computer.setKeyboard(sqlExecutor.queryBean(keyboardSql.eq(Keyboard::getId, computer.getKeyboardId())));
		List<ComputerSoftware> computerSoftwareList = sqlExecutor.queryList(somputerSoftwareSql);
		List<Integer> softwareIdList = computerSoftwareList.stream().map(e -> e.getSoftwareId()).collect(Collectors.toList());
		computer.setSoftware(sqlExecutor.queryList(softwareSql.in(Software::getId, softwareIdList)));
		
		return computer;
	}

	@Override
	public List<Computer> listComputer() {

		QueryBuilder computerSql = SQL.query(Computer.class);
		QueryBuilder mainframeSql = SQL.query(Mainframe.class);
		QueryBuilder monitorSql = SQL.query(Monitor.class);
		QueryBuilder mouseSql = SQL.query(Mouse.class);
		QueryBuilder keyboardSql = SQL.query(Keyboard.class);
		QueryBuilder softwareSql = SQL.query(Software.class);
		

		List<Computer> computers = sqlExecutor.queryList(computerSql);

		for (Computer computer : computers) {

			computer.setMainframe(sqlExecutor.queryBean(mainframeSql.eq(Mainframe::getId, computer.getMainframeId()) ));
			computer.setMonitor(sqlExecutor.queryBean(monitorSql.eq(Monitor::getId, computer.getMonitorId())));
			computer.setMouse(sqlExecutor.queryBean(mouseSql.eq(Mouse::getId, computer.getMouseId())));
			computer.setKeyboard(sqlExecutor.queryBean(keyboardSql.eq(Keyboard::getId, computer.getKeyboardId())));
			
			SqlBuilder somputerSoftwareSql = SQL.query(ComputerSoftware.class).eq(ComputerSoftware::getComputerId, computer.getId());
			List<ComputerSoftware> computerSoftwareList = sqlExecutor.queryList(somputerSoftwareSql);
			List<Integer> softwareIdList = computerSoftwareList.stream().map(e -> e.getSoftwareId()).collect(Collectors.toList());
			computer.setSoftware(sqlExecutor.queryList(softwareSql.in(Software::getId, softwareIdList)));
		}

		return computers;
	}

	@Override
	public Monitor getMonitorById(int id) {

		return sqlExecutor.queryBean(SQL.query(Monitor.class).where().eq(Monitor::getId, id));
	}

	@Override
	public List<Monitor> listMonitor() {

	    return sqlExecutor.queryList(SQL.query(Monitor.class));
	}

}
