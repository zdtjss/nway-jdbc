package com.nway.spring.jdbc.performance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Keyboard;
import com.nway.spring.jdbc.performance.entity.Mainframe;
import com.nway.spring.jdbc.performance.entity.Monitor;
import com.nway.spring.jdbc.performance.entity.Mouse;

@Transactional
@Service("springJdbcPerformance")
public class SpringJdbcPerformance implements Performance {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Computer getComputer(int id) {

		String computerSql = "select * from t_computer where id = ? and 1 = 2";
		String mainframeSql = "select * from t_mainframe where id = ?";
		String monitorSql = "select * from t_monitor where id = ?";
		String mouseSql = "select * from t_mouse where id = ?";
		String keyboardSql = "select * from t_keyboard where id = ?";

		Computer computer = jdbcTemplate.query(computerSql, new BeanPropertyRowMapper<Computer>(Computer.class), id).get(0);

		computer.setMainframe(jdbcTemplate.query(mainframeSql, BeanPropertyRowMapper.newInstance(Mainframe.class), computer.getMainframeId()).get(0));
		computer.setMonitor(jdbcTemplate.query(monitorSql, BeanPropertyRowMapper.newInstance(Monitor.class), computer.getMonitorId()).get(0));
		computer.setMouse(jdbcTemplate.query(mouseSql, BeanPropertyRowMapper.newInstance(Mouse.class), computer.getMouseId()).get(0));
		computer.setKeyboard(jdbcTemplate.query(keyboardSql, BeanPropertyRowMapper.newInstance(Keyboard.class), computer.getKeyboardId()).get(0));

		return computer;
	}

	@Override
	public List<Computer> listComputer() {

		String computerSql = "select * from t_computer";
		String mainframeSql = "select * from t_mainframe where id = ?";
		String monitorSql = "select * from t_monitor where id = ?";
		String mouseSql = "select * from t_mouse where id = ?";
		String keyboardSql = "select * from t_keyboard where id = ?";

		List<Computer> computers = jdbcTemplate.query(computerSql, BeanPropertyRowMapper.newInstance(Computer.class));

		for (Computer computer : computers) {

			computer.setMainframe(jdbcTemplate.query(mainframeSql, BeanPropertyRowMapper.newInstance(Mainframe.class), computer.getMainframeId()).get(0));
			computer.setMonitor(jdbcTemplate.query(monitorSql, BeanPropertyRowMapper.newInstance(Monitor.class), computer.getMonitorId()).get(0));
			computer.setMouse(jdbcTemplate.query(mouseSql, BeanPropertyRowMapper.newInstance(Mouse.class), computer.getMouseId()).get(0));
			computer.setKeyboard(jdbcTemplate.query(keyboardSql, BeanPropertyRowMapper.newInstance(Keyboard.class), computer.getKeyboardId()).get(0));
		}

		return computers;
	}

	@Override
	public Monitor getMonitor(int id) {

		return jdbcTemplate.query("select * from t_monitor where id=?", BeanPropertyRowMapper.newInstance(Monitor.class), id).get(0);
	}

	@Override
	public List<Monitor> listMonitor(int num) {

		return jdbcTemplate.query("select * from t_monitor where rownum < ?", BeanPropertyRowMapper.newInstance(Monitor.class), num);
	}
	
}
