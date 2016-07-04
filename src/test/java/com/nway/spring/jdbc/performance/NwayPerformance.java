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
import com.nway.spring.jdbc.performance.entity.Software;

@Service("nwayPerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class NwayPerformance implements Performance {

	@Autowired
	private SqlExecutor sqlExecutor;

	@Override
	public Computer getComputerById(int id) {

		String computerSql = "select * from t_computer where id = ?";
		String mainframeSql = "select * from t_mainframe where id = ?";
		String monitorSql = "select * from t_monitor where id = ?";
		String mouseSql = "select * from t_mouse where id = ?";
		String keyboardSql = "select * from t_keyboard where id = ?";
		String softwareSql = "select s.* from t_software s join t_computer_software cs on s.id = cs.software_id and cs.computer_id = ?";

		Computer computer = sqlExecutor.queryForBean(computerSql, Computer.class, id);

		computer.setMainframe(sqlExecutor.queryForBean(mainframeSql, Mainframe.class, computer.getMainframeId()));
		computer.setMonitor(sqlExecutor.queryForBean(monitorSql, Monitor.class, computer.getMonitorId()));
		computer.setMouse(sqlExecutor.queryForBean(mouseSql, Mouse.class, computer.getMouseId()));
		computer.setKeyboard(sqlExecutor.queryForBean(keyboardSql, Keyboard.class, computer.getKeyboardId()));
		computer.setSoftware(sqlExecutor.queryForBeanList(softwareSql, Software.class, computer.getId()));
		
		return computer;
	}

	@Override
	public List<Computer> listComputer() {

		String computerSql = "select * from t_computer";
		String mainframeSql = "select * from t_mainframe where id = ?";
		String monitorSql = "select * from t_monitor where id = ?";
		String mouseSql = "select * from t_mouse where id = ?";
		String keyboardSql = "select * from t_keyboard where id = ?";
	    String softwareSql = "select s.* from t_software s join t_computer_software cs on s.id = cs.software_id and cs.computer_id = ?";


		List<Computer> computers = sqlExecutor.queryForBeanList(computerSql, Computer.class);

		for (Computer computer : computers) {

			computer.setMainframe(sqlExecutor.queryForBean(mainframeSql, Mainframe.class, computer.getMainframeId()));
			computer.setMonitor(sqlExecutor.queryForBean(monitorSql, Monitor.class, computer.getMonitorId()));
			computer.setMouse(sqlExecutor.queryForBean(mouseSql, Mouse.class, computer.getMouseId()));
			computer.setKeyboard(sqlExecutor.queryForBean(keyboardSql, Keyboard.class, computer.getKeyboardId()));
			computer.setSoftware(sqlExecutor.queryForBeanList(softwareSql, Software.class, computer.getId()));
		}

		return computers;
	}

	@Override
	public Monitor getMonitorById(int id) {

		return sqlExecutor.queryForBean("select * from t_monitor where id = ?", Monitor.class, id);
	}

	@Override
	public List<Monitor> listMonitor() {

		return sqlExecutor.queryForBeanList("select * from t_monitor", Monitor.class);
	}
	
	public String queryComputerJson(int id) {
		
		return sqlExecutor.queryForJson("select * from t_computer where id = ?", Computer.class, id);
	}
	
	public String queryComputerListJson() {
		
		return sqlExecutor.queryForJson("select * from t_computer", Computer.class);
	}

}
