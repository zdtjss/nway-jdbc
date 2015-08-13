package com.nway.spring.jdbc.performance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nway.spring.jdbc.SqlExecutor;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Keyboard;
import com.nway.spring.jdbc.performance.entity.Mainframe;
import com.nway.spring.jdbc.performance.entity.Monitor;
import com.nway.spring.jdbc.performance.entity.Mouse;

@Transactional
@Service("nwayPerformance")
public class NwayPerformance implements Performance {

	@Autowired
	private SqlExecutor sqlExecutor;

	@Override
	public Computer getComputer(int id) {

		String computerSql = "select * from t_computer where id = ?";
		String mainframeSql = "select * from t_mainframe where id = ?";
		String monitorSql = "select * from t_monitor where id = ?";
		String mouseSql = "select * from t_mouse where id = ?";
		String keyboardSql = "select * from t_keyboard where id = ?";

		Computer computer = sqlExecutor.queryForBean(computerSql, Computer.class, id);

		computer.setMainframe(sqlExecutor.queryForBean(mainframeSql, Mainframe.class, computer.getMainframeId()));
		computer.setMonitor(sqlExecutor.queryForBean(monitorSql, Monitor.class, computer.getMonitorId()));
		computer.setMouse(sqlExecutor.queryForBean(mouseSql, Mouse.class, computer.getMouseId()));
		computer.setKeyboard(sqlExecutor.queryForBean(keyboardSql, Keyboard.class, computer.getKeyboardId()));

		return computer;
	}

	@Override
	public List<Computer> listComputer() {

		String computerSql = "select * from t_computer";
		String mainframeSql = "select * from t_mainframe where id = ?";
		String monitorSql = "select * from t_monitor where id = ?";
		String mouseSql = "select * from t_mouse where id = ?";
		String keyboardSql = "select * from t_keyboard where id = ?";

		List<Computer> computers = sqlExecutor.queryForBeanList(computerSql, Computer.class);

		for (Computer computer : computers) {

			computer.setMainframe(sqlExecutor.queryForBean(mainframeSql, Mainframe.class, computer.getMainframeId()));
			computer.setMonitor(sqlExecutor.queryForBean(monitorSql, Monitor.class, computer.getMonitorId()));
			computer.setMouse(sqlExecutor.queryForBean(mouseSql, Mouse.class, computer.getMouseId()));
			computer.setKeyboard(sqlExecutor.queryForBean(keyboardSql, Keyboard.class, computer.getKeyboardId()));
		}

		return computers;
	}

	@Override
	public Monitor getMonitor(int id) {

		return sqlExecutor.queryForBean("select * from t_monitor where id = ?", Monitor.class, id);
	}

	@Override
	public List<Monitor> listMonitor(int num) {

		return sqlExecutor.queryForBeanList("select * from t_monitor limit ?", Monitor.class, num);
	}
	
	public String queryComputerJson(int id) {
		
		return sqlExecutor.queryForJson("select * from t_computer where id = ?", Computer.class, id);
	}
	
	public String queryComputerListJson() {
		
		return sqlExecutor.queryForJson("select * from t_computer", Computer.class);
	}

}
