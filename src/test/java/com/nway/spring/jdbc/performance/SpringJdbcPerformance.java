package com.nway.spring.jdbc.performance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
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

		List<Computer> computers = jdbcTemplate.query(computerSql, new ComputerRowMapper());

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

		return jdbcTemplate.query("select * from t_monitor where id=?", new MonitorRowMapper(), id).get(0);
	}

	@Override
	public List<Monitor> listMonitor(int num) {

		return jdbcTemplate.query("select * from t_monitor limit ?", new MonitorRowMapper(), num);
	}

	class ComputerRowMapper implements RowMapper<Computer> {

		@Override
		public Computer mapRow(ResultSet rs, int rowNum) throws SQLException {

			Computer computer = new Computer();

			computer.setId(rs.getInt("id"));
			computer.setBrand(rs.getString("brand"));
			computer.setModel(rs.getString("model"));
			computer.setPrice(rs.getFloat("price"));
			computer.setMainframeId(rs.getInt("mainframe_id"));
			computer.setMonitorId(rs.getInt("monitor_id"));
			computer.setMouseId(rs.getInt("mouse_id"));
			computer.setKeyboardId(rs.getInt("keyboard_id"));
			computer.setProductionDate(rs.getDate("production_date"));
			computer.setPhoto(rs.getBytes("photo"));

			return computer;
		}
	}

	/**
	 * 单对象
	 */
	class ComputerResultSetExtractor implements ResultSetExtractor<Computer> {

		@Override
		public Computer extractData(ResultSet rs) throws SQLException, DataAccessException {

			if (!rs.next()) {

				return null;
			}

			Computer computer = new Computer();

			computer.setId(rs.getInt("id"));
			computer.setBrand(rs.getString("brand"));
			computer.setModel(rs.getString("model"));
			computer.setPrice(rs.getFloat("price"));
			computer.setMainframeId(rs.getInt("mainframe_id"));
			computer.setMonitorId(rs.getInt("monitor_id"));
			computer.setMouseId(rs.getInt("mouse_id"));
			computer.setKeyboardId(rs.getInt("keyboard_id"));
			computer.setProductionDate(rs.getDate("production_date"));
			computer.setPhoto(rs.getBytes("photo"));

			return computer;
		}
	}

	/**
	 * 单对象
	 */
	class MainframeResultSetExtractor implements ResultSetExtractor<Mainframe> {

		@Override
		public Mainframe extractData(ResultSet rs) throws SQLException, DataAccessException {

			if (!rs.next()) {

				return null;
			}

			Mainframe mainframe = new Mainframe();

			mainframe.setId(rs.getInt("id"));
			mainframe.setBrand(rs.getString("brand"));
			mainframe.setModel(rs.getString("model"));
			mainframe.setPrice(rs.getFloat("price"));
			mainframe.setType(rs.getInt("type"));
			mainframe.setProductionDate(rs.getDate("production_date"));
			mainframe.setPhoto(rs.getBytes("photo"));

			return mainframe;
		}
	}

	/**
	 * 单对象
	 */
	class MonitorRowMapper implements RowMapper<Monitor> {

		@Override
		public Monitor mapRow(ResultSet rs, int rowNum) throws SQLException {

			if (!rs.next()) {

				return null;
			}

			Monitor monitor = new Monitor();

			monitor.setId(rs.getInt("id"));
			monitor.setBrand(rs.getString("brand"));
			monitor.setModel(rs.getString("model"));
			monitor.setPrice(rs.getFloat("price"));
			monitor.setType(rs.getInt("type"));
			monitor.setMaxResolution(rs.getString("max_resolution"));
			monitor.setProductionDate(rs.getDate("production_date"));
			monitor.setPhoto(rs.getBytes("photo"));

			return monitor;
		}
	}

	/**
	 * 单对象
	 */
	class MonitorResultSetExtractor implements ResultSetExtractor<Monitor> {

		@Override
		public Monitor extractData(ResultSet rs) throws SQLException {

			if (!rs.next()) {

				return null;
			}

			Monitor monitor = new Monitor();

			monitor.setId(rs.getInt("id"));
			monitor.setBrand(rs.getString("brand"));
			monitor.setModel(rs.getString("model"));
			monitor.setPrice(rs.getFloat("price"));
			monitor.setType(rs.getInt("type"));
			monitor.setMaxResolution(rs.getString("max_resolution"));
			monitor.setProductionDate(rs.getDate("production_date"));
			monitor.setPhoto(rs.getBytes("photo"));

			return monitor;
		}
	}

	/**
	 * 单对象
	 */
	class MouseResultSetExtractor implements ResultSetExtractor<Mouse> {

		@Override
		public Mouse extractData(ResultSet rs) throws SQLException, DataAccessException {

			if (!rs.next()) {

				return null;
			}

			Mouse mouse = new Mouse();

			mouse.setId(rs.getInt("id"));
			mouse.setBrand(rs.getString("brand"));
			mouse.setModel(rs.getString("model"));
			mouse.setPrice(rs.getFloat("price"));
			mouse.setType(rs.getInt("type"));
			mouse.setWireless(rs.getBoolean("wireless"));
			mouse.setColor(rs.getString("color"));
			mouse.setProductionDate(rs.getDate("production_date"));
			mouse.setPhoto(rs.getBytes("photo"));

			return mouse;
		}
	}

	/**
	 * 单对象
	 */
	class KeyboardResultSetExtractor implements ResultSetExtractor<Keyboard> {

		@Override
		public Keyboard extractData(ResultSet rs) throws SQLException, DataAccessException {

			if (!rs.next()) {

				return null;
			}

			Keyboard keyboard = new Keyboard();

			keyboard.setId(rs.getInt("id"));
			keyboard.setBrand(rs.getString("brand"));
			keyboard.setModel(rs.getString("model"));
			keyboard.setPrice(rs.getFloat("price"));
			keyboard.setType(rs.getInt("type"));
			keyboard.setInterfaceType(rs.getInt("interface_type"));
			keyboard.setWireless(rs.getBoolean("wireless"));
			keyboard.setColor(rs.getString("color"));
			keyboard.setProductionDate(rs.getDate("production_date"));
			keyboard.setPhoto(rs.getBytes("photo"));

			return keyboard;
		}
	}
}
