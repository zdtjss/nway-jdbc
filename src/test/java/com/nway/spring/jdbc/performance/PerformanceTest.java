package com.nway.spring.jdbc.performance;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nway.spring.jdbc.BaseTest;
import com.nway.spring.jdbc.performance.entity.Computer;

public class PerformanceTest extends BaseTest {

	@Autowired
	@Qualifier("nwayPerformance")
	private Performance nwayPerformance;

	@Autowired
	@Qualifier("springJdbcPerformance")
	private Performance springJdbcPerformance;

	@Autowired
	@Qualifier("hibernatePerformance")
	private Performance hibernatePerformance;
	
	@Autowired
	@Qualifier("myBatisPerformance")
	private Performance myBatisPerformance;

	@Test
	public void testGetMonitor() {

		int id = 32768;

		for (int i = 0; i < 30; i++) {

			nwayPerformance.getMonitor(id);

			hibernatePerformance.getMonitor(id);

			springJdbcPerformance.getMonitor(id);
			
			myBatisPerformance.getMonitor(id);

			System.out.println();
		}
	}

	@Test
	public void testListMonitor() {

		int num = 10;

		for (int i = 0; i < 30; i++) {

			nwayPerformance.listMonitor(num);

			hibernatePerformance.listMonitor(num);

			springJdbcPerformance.listMonitor(num);
			
			myBatisPerformance.listMonitor(num);

			System.out.println();
		}
	}

	@Test
	public void testGetComputer() {

		final int id = 1;

		for (int i = 0; i < 30; i++) {

			nwayPerformance.getComputer(id);

			hibernatePerformance.getComputer(id);

			springJdbcPerformance.getComputer(id);
			
			myBatisPerformance.getComputer(id);

			System.out.println();
		}
	}

	@Test
	public void testListComputer() {

		List<Computer> cs = null;
		
		for (int i = 0; i < 100; i++) {

//			nwayPerformance.listComputer();

//			cs = hibernatePerformance.listComputer();

//			System.out.println(cs.get(0));
			
//			springJdbcPerformance.listComputer();
			
			cs = myBatisPerformance.listComputer();
			
			System.out.println(cs.get(0));
			
			System.out.println();
		}
	}
	
	@Test
	public void testQueryComputerJson() {
		
		int id = 1;
		
		for (int i = 0; i < 30; i++) {

			((NwayPerformance) nwayPerformance).queryComputerJson(id);
		}
	}
	
	@Test
	public void testQueryComputerListJson() {
		
		for (int i = 0; i < 30; i++) {
			
			((NwayPerformance) nwayPerformance).queryComputerListJson();
		}
	}

	@Test
	public void initDB() {

		((HibernatePerformance) this.hibernatePerformance).initDB();
	}
}
