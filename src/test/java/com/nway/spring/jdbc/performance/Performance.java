package com.nway.spring.jdbc.performance;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;

public interface Performance {

	/**
	 * 有关联单个对象
	 * 
	 * @param id
	 *            对象ID
	 * @return 
	 */
	public Computer getComputerById(int id);
	
	/**
	 * 有关联对象集
	 * 
	 * @return
	 */
	@Query("select c from Computer c")
	public List<Computer> listComputer();

	/**
	 * 无关联单对象
	 * 
	 * @param id 对象ID
	 * @return
	 */
	public Monitor getMonitorById(int id);

	/**
	 * 无关联对象集
	 * 
	 * @param num
	 * @return
	 */
	@Query("select m from Monitor m")
	public List<Monitor> listMonitor();
}
