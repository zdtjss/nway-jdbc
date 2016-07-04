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
import com.nway.spring.jdbc.performance.entity.Software;

@Service("springJdbcPerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class SpringJdbcPerformance implements Performance {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Computer getComputerById(int id) {

		String computerSql = "select * from t_computer where id = ?";
		String mainframeSql = "select * from t_mainframe where id = ?";
		String monitorSql = "select * from t_monitor where id = ?";
		String mouseSql = "select * from t_mouse where id = ?";
		String keyboardSql = "select * from t_keyboard where id = ?";
	    String softwareSql = "select s.* from t_software s join t_computer_software cs on s.id = cs.software_id and cs.computer_id = ?";

		Computer computer = jdbcTemplate.query(computerSql, new BeanPropertyRowMapper<Computer>(Computer.class), id).get(0);

		computer.setMainframe(jdbcTemplate.query(mainframeSql, BeanPropertyRowMapper.newInstance(Mainframe.class), computer.getMainframeId()).get(0));
		computer.setMonitor(jdbcTemplate.query(monitorSql, BeanPropertyRowMapper.newInstance(Monitor.class), computer.getMonitorId()).get(0));
		computer.setMouse(jdbcTemplate.query(mouseSql, BeanPropertyRowMapper.newInstance(Mouse.class), computer.getMouseId()).get(0));
		computer.setKeyboard(jdbcTemplate.query(keyboardSql, BeanPropertyRowMapper.newInstance(Keyboard.class), computer.getKeyboardId()).get(0));
		computer.setSoftware(jdbcTemplate.query(softwareSql, BeanPropertyRowMapper.newInstance(Software.class), computer.getId()));
		
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

		List<Computer> computers = jdbcTemplate.query(computerSql, BeanPropertyRowMapper.newInstance(Computer.class));

		for (Computer computer : computers) {

			computer.setMainframe(jdbcTemplate.query(mainframeSql, BeanPropertyRowMapper.newInstance(Mainframe.class), computer.getMainframeId()).get(0));
			computer.setMonitor(jdbcTemplate.query(monitorSql, BeanPropertyRowMapper.newInstance(Monitor.class), computer.getMonitorId()).get(0));
			computer.setMouse(jdbcTemplate.query(mouseSql, BeanPropertyRowMapper.newInstance(Mouse.class), computer.getMouseId()).get(0));
			computer.setKeyboard(jdbcTemplate.query(keyboardSql, BeanPropertyRowMapper.newInstance(Keyboard.class), computer.getKeyboardId()).get(0));
			computer.setSoftware(jdbcTemplate.query(softwareSql, BeanPropertyRowMapper.newInstance(Software.class), computer.getId()));
		}
		
		/*List<Computer> computers = new ArrayList<>();
		
		try {
			
			DataSource ds = jdbcTemplate.getDataSource();
    		Connection conn = DataSourceUtils.getConnection(ds);
    		String sql = sqlSession.getConfiguration().getMappedStatement("listComputer").getBoundSql(null).getSql();
		
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				
				Computer computer = new Computer();
				
				computer.setId(rs.getInt("computer_id"));
				computer.setBrand(rs.getString("computer_brand"));
				computer.setModel(rs.getString("computer_model"));
				computer.setPhoto(rs.getBytes("computer_photo"));
				computer.setPrice(rs.getFloat("computer_price"));
				computer.setProductionDate(rs.getDate("computer_production_date"));
				
				Mainframe mainframe = new Mainframe();
				
				mainframe.setId(rs.getInt("mainframe_id"));
				mainframe.setBrand(rs.getString("mainframe_brand"));
				mainframe.setModel(rs.getString("mainframe_model"));
				mainframe.setPhoto(rs.getBytes("mainframe_photo"));
				mainframe.setPrice(rs.getFloat("mainframe_price"));
				mainframe.setType(rs.getInt("mainframe_type"));
				mainframe.setProductionDate(rs.getDate("mainframe_production_date"));
				
				computer.setMainframe(mainframe);
				
				Monitor monitor = new Monitor();
				
				monitor.setId(rs.getInt("monitor_id"));
				monitor.setBrand(rs.getString("monitor_brand"));
				monitor.setModel(rs.getString("monitor_model"));
				monitor.setPhoto(rs.getBytes("monitor_photo"));
				monitor.setPrice(rs.getFloat("monitor_price"));
				monitor.setMaxResolution(rs.getString("monitor_max_resolution"));
				monitor.setType(rs.getInt("monitor_type"));
				monitor.setProductionDate(rs.getDate("monitor_production_date"));
				
				computer.setMonitor(monitor);

				Keyboard keyboard = new Keyboard();
				
				keyboard.setId(rs.getInt("keyboard_id"));
				keyboard.setBrand(rs.getString("keyboard_brand"));
				keyboard.setModel(rs.getString("keyboard_model"));
				keyboard.setPhoto(rs.getBytes("keyboard_photo"));
				keyboard.setPrice(rs.getFloat("keyboard_price"));
				keyboard.setColor(rs.getString("keyboard_color"));
				keyboard.setInterfaceType(rs.getInt("keyboard_interface_type"));
				keyboard.setType(rs.getInt("keyboard_type"));
				keyboard.setWireless(rs.getBoolean("keyboard_wireless"));
				keyboard.setProductionDate(rs.getDate("keyboard_production_date"));
				
				computer.setKeyboard(keyboard);
				
				Mouse mouse = new Mouse();
				
				mouse.setId(rs.getInt("mouse_id"));
				mouse.setBrand(rs.getString("mouse_brand"));
				mouse.setModel(rs.getString("mouse_model"));
				mouse.setPhoto(rs.getBytes("mouse_photo"));
				mouse.setPrice(rs.getFloat("mouse_price"));
				mouse.setColor(rs.getString("mouse_color"));
				mouse.setWireless(rs.getBoolean("mouse_wireless"));
				mouse.setProductionDate(rs.getDate("mouse_production_date"));
				
				computer.setMouse(mouse);
				
				computers.add(computer);
				
			}
			rs.close();
			pstmt.close();
			DataSourceUtils.releaseConnection(conn, ds);
		} catch (SQLException e) {
			e.printStackTrace();
		}*/

		return computers;
	}

	@Override
	public Monitor getMonitorById(int id) {

		return jdbcTemplate.query("select * from t_monitor where id=?", BeanPropertyRowMapper.newInstance(Monitor.class), id).get(0);
	}

	@Override
	public List<Monitor> listMonitor() {

		return jdbcTemplate.query("select * from t_monitor", BeanPropertyRowMapper.newInstance(Monitor.class));
	}
	
}
