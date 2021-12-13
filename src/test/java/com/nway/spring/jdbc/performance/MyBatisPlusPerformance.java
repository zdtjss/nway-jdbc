package com.nway.spring.jdbc.performance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
import org.springframework.util.CollectionUtils;

@Service("myBatisPlusPerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class MyBatisPlusPerformance implements Performance {

    private Gson gson = new Gson();
    
	@Autowired
	private MonitorDao monitorDao;
	@Autowired
	private ComputerDao computerDao;
	@Autowired
	private ComputerUserDao computerUserDao;
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

		List<Computer> computers = computerDao.selectList(Wrappers.lambdaQuery(Computer.class));

		List<Integer> computerIds = computers.stream().map(Computer::getId).collect(Collectors.toList());
		List<Integer> mainframeIds = computers.stream().map(Computer::getMainframeId).collect(Collectors.toList());
		List<Integer> monitorIds = computers.stream().map(Computer::getMonitorId).collect(Collectors.toList());
		List<Integer> mouseIds = computers.stream().map(Computer::getMouseId).collect(Collectors.toList());
		List<Integer> keyboardIds = computers.stream().map(Computer::getKeyboardId).collect(Collectors.toList());

		List<ComputerUser> computerUsers = computerUserDao.selectList(Wrappers.lambdaQuery(ComputerUser.class).in(ComputerUser::getForeignKey, computerIds));
		List<Mainframe> mainframeList = mainframeDao.selectBatchIds(mainframeIds);
		List<Monitor> monitorList = monitorDao.selectBatchIds(monitorIds);
		List<Mouse> mouseList = mouseDao.selectBatchIds(mouseIds);
		List<Keyboard> keyboardList = keyboardDao.selectBatchIds(keyboardIds);

		Map<Integer, List<String>> computerUserMap = computerUsers.stream().collect(Collectors.groupingBy(ComputerUser::getForeignKey))
				.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(ComputerUser::getUser).collect(Collectors.toList())));
		Map<Integer, Mainframe> mainframeMap = mainframeList.stream().collect(Collectors.toMap(Mainframe::getId, Function.identity()));
		Map<Integer, Monitor> monitorMap = monitorList.stream().collect(Collectors.toMap(Monitor::getId, Function.identity()));
		Map<Integer, Mouse> mouseMap = mouseList.stream().collect(Collectors.toMap(Mouse::getId, Function.identity()));
		Map<Integer, Keyboard> keyboardMap = keyboardList.stream().collect(Collectors.toMap(Keyboard::getId, Function.identity()));

		LambdaQueryWrapper<ComputerSoftware> computerSoftwareSql = Wrappers.lambdaQuery();
		computerSoftwareSql.in(ComputerSoftware::getComputerId, computers.stream().map(Computer::getId).collect(Collectors.toList()));
		List<ComputerSoftware> computerSoftwareList = computerSoftwareDao.selectList(computerSoftwareSql);

		Map<Integer, List<Software>> computerSoftwareMap = new HashMap<>();
		if(!CollectionUtils.isEmpty(computerSoftwareList)) {
			List<Integer> softIds = computerSoftwareList.stream().map(ComputerSoftware::getSoftwareId).collect(Collectors.toList());
			List<Software> softwareList = softwareDao.selectBatchIds(softIds);
			Map<Integer, Software> softwareMap = softwareList.stream().collect(Collectors.toMap(Software::getId, Function.identity()));
			computerSoftwareList.stream().collect(Collectors.groupingBy(ComputerSoftware::getComputerId)).entrySet().forEach(e -> {
				computerSoftwareMap.put(e.getKey(), e.getValue().stream().map(cs -> softwareMap.get(cs.getSoftwareId())).collect(Collectors.toList()));
			});
		}

		for (Computer computer : computers) {
			computer.setUserList(computerUserMap.get(computer.getId()));
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
	    
		return monitorDao.selectById(id);
	}

	@Override
	public List<Monitor> listMonitor() {
		
		return monitorDao.selectList(Wrappers.query());
	}

	public List<Computer> lambdaTest() {
		LambdaQueryWrapper<Computer> sqlBuilder = Wrappers.lambdaQuery(Computer.class).select(Computer::getId)
				.eq(Computer::getKeyboardId, 0)
				.ne(Computer::getMainframeId, 1)
				.ge(Computer::getMouseId, 100)
				.likeLeft(Computer::getBrand, "a")
				.or((e) -> e.eq(Computer::getMonitorId, 10)
						.and((s) -> s.likeLeft(Computer::getModel, "o")));
		return computerDao.selectList(sqlBuilder);
	}

}
