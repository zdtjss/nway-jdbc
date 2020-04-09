package com.nway.spring.jdbc.performance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
import org.springframework.util.CollectionUtils;

@Service("nwayLambdaPerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class NwayLambdaPerformance implements Performance {

	@Autowired
	private SqlExecutor sqlExecutor;

	@Override
	public Computer getComputerById(int id) {

		SqlBuilder computerSql = SQL.query(Computer.class).where().eq(Computer::getId, id);
		QueryBuilder mainframeSql = SQL.query(Mainframe.class);
		QueryBuilder monitorSql = SQL.query(Monitor.class);
		QueryBuilder mouseSql = SQL.query(Mouse.class);
		QueryBuilder keyboardSql = SQL.query(Keyboard.class);
		QueryBuilder softwareSql = SQL.query(Software.class);
		SqlBuilder computerSoftwareSql = SQL.query(ComputerSoftware.class).where().eq(ComputerSoftware::getComputerId, id);
		
		Computer computer = sqlExecutor.queryBean(computerSql);

		computer.setMainframe(sqlExecutor.queryBean(mainframeSql.where().eq(Mainframe::getId, computer.getMainframeId()) ));
		computer.setMonitor(sqlExecutor.queryBean(monitorSql.where().eq(Monitor::getId, computer.getMonitorId())));
		computer.setMouse(sqlExecutor.queryBean(mouseSql.where().eq(Mouse::getId, computer.getMouseId())));
		computer.setKeyboard(sqlExecutor.queryBean(keyboardSql.where().eq(Keyboard::getId, computer.getKeyboardId())));
		List<ComputerSoftware> computerSoftwareList = sqlExecutor.queryList(computerSoftwareSql);
		List<Integer> softwareIdList = computerSoftwareList.stream().map(e -> e.getSoftwareId()).collect(Collectors.toList());
		computer.setSoftware(sqlExecutor.queryList(softwareSql.where().in(Software::getId, softwareIdList)));
		
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

		List<Integer> mainframeIds = computers.stream().map(e -> e.getMainframeId()).collect(Collectors.toList());
		List<Integer> monitorIds = computers.stream().map(e -> e.getMonitorId()).collect(Collectors.toList());
		List<Integer> mouseIds = computers.stream().map(e -> e.getMouseId()).collect(Collectors.toList());
		List<Integer> keyboardIds = computers.stream().map(e -> e.getKeyboardId()).collect(Collectors.toList());

		List<Mainframe> mainframeList = sqlExecutor.queryList(mainframeIds, Mainframe.class);
		List<Monitor> monitorList = sqlExecutor.queryList(monitorIds, Monitor.class);
		List<Mouse> mouseList = sqlExecutor.queryList(mouseIds, Mouse.class);
		List<Keyboard> keyboardList = sqlExecutor.queryList(keyboardIds, Keyboard.class);

		Map<Integer, Mainframe> mainframeMap = mainframeList.stream().collect(Collectors.toMap(Mainframe::getId, Function.identity()));
		Map<Integer, Monitor> monitorMap = monitorList.stream().collect(Collectors.toMap(Monitor::getId, Function.identity()));
		Map<Integer, Mouse> mouseMap = mouseList.stream().collect(Collectors.toMap(Mouse::getId, Function.identity()));
		Map<Integer, Keyboard> keyboardMap = keyboardList.stream().collect(Collectors.toMap(Keyboard::getId, Function.identity()));

		SqlBuilder computerSoftwareSql = SQL.query(ComputerSoftware.class)
				.where().in(ComputerSoftware::getComputerId, computers.stream().map(e -> e.getId()).collect(Collectors.toList()));
		List<ComputerSoftware> computerSoftwareList = sqlExecutor.queryList(computerSoftwareSql);

		Map<Integer, List<Software>> computerSoftwareMap = new HashMap<>();
		if(!CollectionUtils.isEmpty(computerSoftwareList)) {
			List<Integer> softIds = computerSoftwareList.stream().map(e -> e.getSoftwareId()).collect(Collectors.toList());
			List<Software> softwareList = sqlExecutor.queryList(softIds, Software.class);
			Map<Integer, Software> softwareMap = softwareList.stream().collect(Collectors.toMap(Software::getId, Function.identity()));
			computerSoftwareList.stream().collect(Collectors.groupingBy(ComputerSoftware::getComputerId)).entrySet().stream().forEach(e -> {
				computerSoftwareMap.put(e.getKey(), e.getValue().stream().map(cs -> softwareMap.get(cs.getSoftwareId())).collect(Collectors.toList()));
			});
		}

		for (Computer computer : computers) {
			computer.setMainframe(mainframeMap.get(computer.getMainframeId()));
			computer.setMonitor(monitorMap.get(computer.getMonitorId()));
			computer.setMouse(mouseMap.get(computer.getMouseId()));
			computer.setKeyboard(keyboardMap.get(computer.getKeyboardId()));
			computer.setSoftware(computerSoftwareMap.get(computer.getId()));
		}

		return computers;
	}

	@Override
	public Monitor getMonitorById(int id) {

		return sqlExecutor.queryBean(id, Monitor.class);
	}

	@Override
	public List<Monitor> listMonitor() {

	    return sqlExecutor.queryList(SQL.query(Monitor.class));
	}

}
