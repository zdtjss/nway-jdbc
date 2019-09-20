package com.nway.spring.jdbc.performance;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nway.spring.jdbc.BaseTest;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.repositories.SpringDataJpaPerformance;

@Service
public class PerformanceTest extends BaseTest {

	@Autowired
	@Qualifier("nwayPerformance")
	private NwayPerformance nwayPerformance;

	@Autowired
	@Qualifier("springJdbcPerformance")
	private SpringJdbcPerformance springJdbcPerformance;

	@Autowired
	@Qualifier("hibernatePerformance")
	private HibernatePerformance hibernatePerformance;
	
	@Autowired
        @Qualifier("hibernateJpaPerformance")
	private HibernateJpaPerformance jpaPerformance;
	
	@Autowired
	private SpringDataJpaPerformance springDataJpaPerformance;
	
	@Autowired
	@Qualifier("myBatisPerformance")
	private MyBatisPerformance myBatisPerformance;
	
	@Autowired
	@Qualifier("jdbcPerformance")
	private JdbcPerformance jdbcPerformance;
	
	@Autowired
	@Qualifier("scriptSolutionPerformance")
	private ScriptSolutionPerformance scriptPerformance;

	@Test
	public void testGetMonitor() {

		int id = 70;

		for (int i = 0; i < 10; i++) {

			nwayPerformance.getMonitorById(id);

			hibernatePerformance.getMonitorById(id);
			
			jpaPerformance.getMonitorById(id);
			
			springDataJpaPerformance.getMonitorById(id);

			springJdbcPerformance.getMonitorById(id);
			
			myBatisPerformance.getMonitorById(id);

			System.out.println();
		}
	}

	@Test
	public void testListMonitor() {

		for (int i = 0; i < 30; i++) {

			nwayPerformance.listMonitor();
			
			jdbcPerformance.listMonitor();

			hibernatePerformance.listMonitor();
			
			jpaPerformance.listMonitor();
			
			springDataJpaPerformance.listMonitor();

			springJdbcPerformance.listMonitor();
			
			myBatisPerformance.listMonitor();
			
			scriptPerformance.listMonitor();

			System.out.println();
		}
	}
	
	@Test
	public void testQueryMonitorJsonList() {
	    
	    for (int i = 0; i < 10; i++) {
	        
	        jdbcPerformance.queryMonitorJsonList();

	        /*hibernatePerformance.listMonitor();
			
			jpaPerformance.listMonitor();
			
			springDataJpaPerformance.listMonitor();*/

	        springJdbcPerformance.queryMonitorJsonList();
	        
			myBatisPerformance.queryMonitorJsonList();
	        
	        scriptPerformance.queryMonitorJsonList();
	        
	        nwayPerformance.queryMonitorJsonList();
	        
	        System.out.println();
	    }
	}

	@Test
	public void testGetComputer() {

		final int id = 11;

		for (int i = 0; i < 10; i++) {

//			nwayPerformance.getComputerById(id);

//			hibernatePerformance.getComputerById(id);
			
//			jpaPerformance.getComputerById(id);
			
//			springDataJpaPerformance.getComputerById(id);

//			springJdbcPerformance.getComputerById(id);
			
		    Computer computer = myBatisPerformance.getComputerById(id);
		    
		    // System.out.println(computer.getSoftware());

			System.out.println();
		}
	}

	@Test
	public void testListComputer() {

		List<Computer> cs = null;
		
		for (int i = 0; i < 10; i++) {

			nwayPerformance.listComputer();

			cs = hibernatePerformance.listComputer();
			
			cs = jpaPerformance.listComputer();
			
			cs = springDataJpaPerformance.listComputer();

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
	public void testQueryMonitorListJson() {
	    
	    for (int i = 0; i < 30; i++) {
	        
	        ((NwayPerformance) nwayPerformance).queryMonitorListJson();
	    }
	}

	@Test
	public void initDB() {

		((HibernatePerformance) this.hibernatePerformance).initDB();
	}
	
}
