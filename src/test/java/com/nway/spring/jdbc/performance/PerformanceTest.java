package com.nway.spring.jdbc.performance;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nway.spring.jdbc.BaseTest;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;
import com.nway.spring.jdbc.performance.repositories.SpringDataJpaPerformance;

@Service
public class PerformanceTest extends BaseTest {

    @Autowired
    @Qualifier("nwayPerformance")
    private NwayPerformance nwayPerformance;

    @Autowired
    @Qualifier("nwayLambdaPerformance")
    private NwayLambdaPerformance nwayLambdaPerformance;

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
    @Qualifier("myBatisPlusPerformance")
    private MyBatisPlusPerformance myBatisPlusPerformance;

    @Autowired
    @Qualifier("myBatisGePerformance")
    private MyBatisGePerformance myBatisGePerformance;

    @Autowired
    @Qualifier("jdbcPerformance")
    private JdbcPerformance jdbcPerformance;

    @Autowired
    @Qualifier("scriptSolutionPerformance")
    private ScriptSolutionPerformance scriptPerformance;

    @Test
    public void testGetMonitor() {

        List<Monitor> monitorList = nwayPerformance.listMonitor();

        for (Monitor monitor : monitorList) {

            nwayPerformance.getMonitorById(monitor.getId());

            nwayLambdaPerformance.getMonitorById(monitor.getId());

            hibernatePerformance.getMonitorById(monitor.getId());

            jpaPerformance.getMonitorById(monitor.getId());

            springDataJpaPerformance.getMonitorById(monitor.getId());

            springJdbcPerformance.getMonitorById(monitor.getId());

//			myBatisPerformance.getMonitorById(monitor.getId());

            myBatisPlusPerformance.getMonitorById(monitor.getId());

            myBatisGePerformance.getMonitorById(monitor.getId());

            System.out.println();
        }
    }

    @Test
    public void testListMonitor() {

        for (int i = 0; i < 30; i++) {

            nwayPerformance.listMonitor();

            nwayLambdaPerformance.listMonitor();

            jdbcPerformance.listMonitor();

            hibernatePerformance.listMonitor();

            jpaPerformance.listMonitor();

            springDataJpaPerformance.listMonitor();

            springJdbcPerformance.listMonitor();

			myBatisPerformance.listMonitor();

            myBatisPlusPerformance.listMonitor();

            myBatisGePerformance.listMonitor();

            scriptPerformance.listMonitor();

            System.out.println();
        }
    }

    @Test
    public void testGetComputer() {

        final int id = 11;

        for (int i = 0; i < 10; i++) {

			nwayPerformance.getComputerById(id);

//			hibernatePerformance.getComputerById(id);

//			jpaPerformance.getComputerById(id);

//			springDataJpaPerformance.getComputerById(id);

//			springJdbcPerformance.getComputerById(id);

            myBatisPerformance.getComputerById(id);

            myBatisGePerformance.getComputerById(id);

            // System.out.println(computer.getSoftware());

            System.out.println();
        }
    }

    @Test
    public void testListComputer() {

        List<Computer> cs = null;

        for (int i = 0; i < 10; i++) {

            nwayPerformance.listComputer();

            nwayLambdaPerformance.listComputer();

            cs = hibernatePerformance.listComputer();

            cs = jpaPerformance.listComputer();

            cs = springDataJpaPerformance.listComputer();

//			System.out.println(cs.get(0));

            springJdbcPerformance.listComputer();

//			cs = myBatisPerformance.listComputer();

            cs = myBatisPlusPerformance.listComputer();

            myBatisGePerformance.listComputer();

//			System.out.println(cs.get(0));

            System.out.println();
        }
    }

	@Test
	public void listComputerVsTest() {

		for (int i = 0; i < 50; i++) {
			myBatisPerformance.listComputer();
		}
		System.out.println();
		for (int i = 0; i < 50; i++) {
			nwayLambdaPerformance.listComputer();
		}
		System.out.println();
		for (int i = 0; i < 50; i++) {
			myBatisPlusPerformance.listComputer();
		}
		System.out.println();
		for (int i = 0; i < 50; i++) {
			myBatisGePerformance.listComputer();
		}
	}

	@Test
	public void listMonitorVsTest() {

		for (int i = 0; i < 50; i++) {
			myBatisPerformance.listMonitor();
		}
		System.out.println();
		for (int i = 0; i < 50; i++) {
			nwayLambdaPerformance.listMonitor();
		}
		System.out.println();
		for (int i = 0; i < 50; i++) {
			myBatisPlusPerformance.listMonitor();
		}
		System.out.println();
		for (int i = 0; i < 50; i++) {
			myBatisGePerformance.listMonitor();
		}
	}

    @Test
    public void initDB() {

       hibernatePerformance.initDB();
    }

}
