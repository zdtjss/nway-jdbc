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

		String computerSql = "select * from t_computer where id = ?";
		String mainframeSql = "select * from t_mainframe where id = ?";
		String monitorSql = "select * from t_monitor where id = ?";
		String mouseSql = "select * from t_mouse where id = ?";
		String keyboardSql = "select * from t_keyboard where id = ?";

		Computer computer = jdbcTemplate.query(computerSql, new BeanPropertyRowMapper<Computer>(Computer.class), id).get(0);

		computer.setMainframe(jdbcTemplate.query(mainframeSql, new BeanPropertyRowMapper<Mainframe>(Mainframe.class), computer.getMainframeId()).get(0));
		computer.setMonitor(jdbcTemplate.query(monitorSql, new BeanPropertyRowMapper<Monitor>(Monitor.class), computer.getMonitorId()).get(0));
		computer.setMouse(jdbcTemplate.query(mouseSql, new BeanPropertyRowMapper<Mouse>(Mouse.class), computer.getMouseId()).get(0));
		computer.setKeyboard(jdbcTemplate.query(keyboardSql, new BeanPropertyRowMapper<Keyboard>(Keyboard.class), computer.getKeyboardId()).get(0));

		return computer;
	}

	@Override
	public List<Computer> listComputer() {

		String computerSql = "select * from t_computer";
		String mainframeSql = "select * from t_mainframe where id = ?";
		String monitorSql = "select * from t_monitor where id = ?";
		String mouseSql = "select * from t_mouse where id = ?";
		String keyboardSql = "select * from t_keyboard where id = ?";

		List<Computer> computers = jdbcTemplate.query(computerSql, new BeanPropertyRowMapper<Computer>(Computer.class));

		for (Computer computer : computers) {

			computer.setMainframe(jdbcTemplate.query(mainframeSql, new BeanPropertyRowMapper<Mainframe>(Mainframe.class), computer.getMainframeId()).get(0));
			computer.setMonitor(jdbcTemplate.query(monitorSql, new BeanPropertyRowMapper<Monitor>(Monitor.class), computer.getMonitorId()).get(0));
			computer.setMouse(jdbcTemplate.query(mouseSql, new BeanPropertyRowMapper<Mouse>(Mouse.class), computer.getMouseId()).get(0));
			computer.setKeyboard(jdbcTemplate.query(keyboardSql, new BeanPropertyRowMapper<Keyboard>(Keyboard.class), computer.getKeyboardId()).get(0));
		}

		return computers;
	}

	@Override
	public Monitor getMonitor(int id) {

		return jdbcTemplate.query("select * from t_monitor where id=?", new BeanPropertyRowMapper<Monitor>(Monitor.class), id).get(0);
	}

	@Override
	public List<Monitor> listMonitor(int num) {

		return jdbcTemplate.query("select * from t_monitor where rownum < ?", new BeanPropertyRowMapper<Monitor>(Monitor.class), num);
	}
	
}
