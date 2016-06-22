package com.nway.spring.jdbc.performance;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nway.spring.jdbc.BaseTest;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.repositories.MonitorRepository;

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
    @Qualifier("hibernateJpaPerformance")
	private Performance jpaPerformance;
	
	@Autowired
	private MonitorRepository monitorRepository;
	
	@Autowired
	@Qualifier("myBatisPerformance")
	private Performance myBatisPerformance;

	@Test
	public void testGetMonitor() {

		int id = 32768;

		for (int i = 0; i < 1; i++) {

			/*nwayPerformance.getMonitor(id);

			hibernatePerformance.getMonitor(id);
			
			jpaPerformance.getMonitor(id);

			springJdbcPerformance.getMonitor(id);*/
			
			myBatisPerformance.getMonitorById(id);

			System.out.println();
		}
	}

	@Test
	public void testListMonitor() {

		int num = 10;

		for (int i = 0; i < 30; i++) {

			nwayPerformance.listMonitor();

			hibernatePerformance.listMonitor();
			
			jpaPerformance.listMonitor();
			
			monitorRepository.listMonitor();

			springJdbcPerformance.listMonitor();
			
			myBatisPerformance.listMonitor();

			System.out.println();
		}
	}

	@Test
	public void testGetComputer() {

		final int id = 1;

		for (int i = 0; i < 10; i++) {

			nwayPerformance.getComputerById(id);

			hibernatePerformance.getComputerById(id);
			
			jpaPerformance.getComputerById(id);

			springJdbcPerformance.getComputerById(id);
			
		    Computer computer = myBatisPerformance.getComputerById(id);
		    
		    // System.out.println(computer.getSoftware());

			System.out.println();
		}
	}

	@Test
	public void testListComputer() {

		List<Computer> cs = null;
		
		for (int i = 0; i < 10; i++) {

//			nwayPerformance.listComputer();

			cs = hibernatePerformance.listComputer();
			
			cs = jpaPerformance.listComputer();

//			System.out.println(cs.get(0));
			
			springJdbcPerformance.listComputer();
			
			cs = myBatisPerformance.listComputer();
			
//			System.out.println(cs.get(0));
			
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
