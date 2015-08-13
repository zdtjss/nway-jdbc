package com.nway.spring.jdbc.performance;

import java.util.List;

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
	public Computer getComputer(int id);

	/**
	 * 有关联对象集
	 * 
	 * @return
	 */
	public List<Computer> listComputer();

	/**
	 * 无关联单对象
	 * 
	 * @param id 对象ID
	 * @return
	 */
	public Monitor getMonitor(int id);

	/**
	 * 无关联对象集
	 * 
	 * @param num
	 * @return
	 */
	public List<Monitor> listMonitor(int num);
}
