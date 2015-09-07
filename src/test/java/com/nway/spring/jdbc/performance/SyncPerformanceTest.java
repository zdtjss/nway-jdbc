package com.nway.spring.jdbc.performance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nway.spring.jdbc.BaseTest;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;

public class SyncPerformanceTest extends BaseTest {

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
	public void testGetMonitor() throws InterruptedException {

		int times = 10;
		final int id = 1;

		ExecutorService nway = Executors.newFixedThreadPool(times);
		ExecutorService spring = Executors.newFixedThreadPool(times);
		ExecutorService hibernate = Executors.newFixedThreadPool(times);

		Collection<Callable<Monitor>> nwayTask = new ArrayList<Callable<Monitor>>(times);
		Collection<Callable<Monitor>> springTask = new ArrayList<Callable<Monitor>>(times);
		Collection<Callable<Monitor>> hibernateTask = new ArrayList<Callable<Monitor>>(times);

		for (int i = 0; i < times; i++) {

			nwayTask.add(new Callable<Monitor>() {

				@Override
				public Monitor call() throws Exception {

					return nwayPerformance.getMonitor(id);
				}
			});

			springTask.add(new Callable<Monitor>() {

				@Override
				public Monitor call() throws Exception {

					return springJdbcPerformance.getMonitor(id);
				}
			});

			hibernateTask.add(new Callable<Monitor>() {

				@Override
				public Monitor call() throws Exception {

					return hibernatePerformance.getMonitor(id);
				}
			});
		}

		long begin = System.currentTimeMillis();
		
		nwayPerformance.getMonitor(id);
		nway.invokeAll(nwayTask);
		
//		spring.invokeAll(springTask);
		
//		hibernate.invokeAll(hibernateTask);
		
		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void testListMonitor() throws InterruptedException {

		int times = 30;
		final int num = 10;

		ExecutorService nway = Executors.newFixedThreadPool(times);
		ExecutorService spring = Executors.newFixedThreadPool(times);
		ExecutorService hibernate = Executors.newFixedThreadPool(times);

		Collection<Callable<List<Monitor>>> nwayTask = new ArrayList<Callable<List<Monitor>>>(times);
		Collection<Callable<List<Monitor>>> springTask = new ArrayList<Callable<List<Monitor>>>(times);
		Collection<Callable<List<Monitor>>> hibernateTask = new ArrayList<Callable<List<Monitor>>>(times);

		for (int i = 0; i < times; i++) {

			nwayTask.add(new Callable<List<Monitor>>() {

				@Override
				public List<Monitor> call() throws Exception {

					return nwayPerformance.listMonitor(num);
				}
			});

			springTask.add(new Callable<List<Monitor>>() {

				@Override
				public List<Monitor> call() throws Exception {

					return springJdbcPerformance.listMonitor(num);
				}
			});

			hibernateTask.add(new Callable<List<Monitor>>() {

				@Override
				public List<Monitor> call() throws Exception {

					return hibernatePerformance.listMonitor(num);
				}
			});
		}

		long begin = System.currentTimeMillis();
		
		nway.invokeAll(nwayTask);
		
		spring.invokeAll(springTask);
		
		hibernate.invokeAll(hibernateTask);
		
		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void testGetComputer() throws InterruptedException {

		int times = 30;
		final int id = 1;

		ExecutorService nway = Executors.newFixedThreadPool(times);
		ExecutorService spring = Executors.newFixedThreadPool(times);
		ExecutorService hibernate = Executors.newFixedThreadPool(times);

		Collection<Callable<Computer>> nwayTask = new ArrayList<Callable<Computer>>(times);
		Collection<Callable<Computer>> springTask = new ArrayList<Callable<Computer>>(times);
		Collection<Callable<Computer>> hibernateTask = new ArrayList<Callable<Computer>>(times);

		for (int i = 0; i < times; i++) {

			nwayTask.add(new Callable<Computer>() {

				@Override
				public Computer call() throws Exception {

					return nwayPerformance.getComputer(id);
				}
			});

			springTask.add(new Callable<Computer>() {

				@Override
				public Computer call() throws Exception {

					return springJdbcPerformance.getComputer(id);
				}
			});

			hibernateTask.add(new Callable<Computer>() {

				@Override
				public Computer call() throws Exception {

					return hibernatePerformance.getComputer(id);
				}
			});
		}

		long begin = System.currentTimeMillis();
		
		nwayPerformance.getComputer(id);
		nway.invokeAll(nwayTask);
		
//		spring.invokeAll(springTask);
		
//		hibernate.invokeAll(hibernateTask);
		
		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void testListComputer() throws InterruptedException {

		int times = 50;
		int nThread = 2;

		ExecutorService nway = Executors.newFixedThreadPool(nThread);
		ExecutorService spring = Executors.newFixedThreadPool(nThread);
		ExecutorService hibernate = Executors.newFixedThreadPool(nThread);
		ExecutorService mybatis = Executors.newFixedThreadPool(nThread);

		Collection<Callable<List<Computer>>> nwayTask = new ArrayList<Callable<List<Computer>>>(times);
		Collection<Callable<List<Computer>>> springTask = new ArrayList<Callable<List<Computer>>>(times);
		Collection<Callable<List<Computer>>> hibernateTask = new ArrayList<Callable<List<Computer>>>(times);
		Collection<Callable<List<Computer>>> mybatisTask = new ArrayList<Callable<List<Computer>>>(times);

		for (int i = 0; i < times; i++) {

			nwayTask.add(new Callable<List<Computer>>() {

				@Override
				public List<Computer> call() throws Exception {

					return nwayPerformance.listComputer();
				}
			});

			springTask.add(new Callable<List<Computer>>() {

				@Override
				public List<Computer> call() throws Exception {

					return springJdbcPerformance.listComputer();
				}
			});

			hibernateTask.add(new Callable<List<Computer>>() {

				@Override
				public List<Computer> call() throws Exception {

					return hibernatePerformance.listComputer();
				}
			});
			
			mybatisTask.add(new Callable<List<Computer>>() {
				
				@Override
				public List<Computer> call() throws Exception {
					
					return myBatisPerformance.listComputer();
				}
			});
		}
		long begin = System.currentTimeMillis();
		
//		nwayPerformance.listComputer();
//		nway.invokeAll(nwayTask);
//		nwayPerformance.listComputer();
		
//		spring.invokeAll(springTask);
//		springJdbcPerformance.listComputer();
		
//		hibernate.invokeAll(hibernateTask);
//		hibernatePerformance.listComputer();
		
		mybatis.invokeAll(mybatisTask);
		
		System.out.println(System.currentTimeMillis() - begin);
	}
	
	@Test
	public void testQueryComputerJson() throws InterruptedException {
		
		int times = 50;
		final int id = 1;

		ExecutorService nway = Executors.newFixedThreadPool(times);
		
		Collection<Callable<String>> nwayTask = new ArrayList<Callable<String>>(times);
		
		for (int i = 0; i < times; i++) {

			nwayTask.add(new Callable<String>() {

				@Override
				public String call() throws Exception {

					return ((NwayPerformance) nwayPerformance).queryComputerJson(id);
				}
			});
		}
		
		nway.invokeAll(nwayTask);
	}
	
	@Test
	public void testQueryComputerListJson() throws InterruptedException {
		
		int times = 50;

		ExecutorService nway = Executors.newFixedThreadPool(times);
		
		Collection<Callable<String>> nwayTask = new ArrayList<Callable<String>>(times);
		
		for (int i = 0; i < times; i++) {

			nwayTask.add(new Callable<String>() {

				@Override
				public String call() throws Exception {

					return ((NwayPerformance) nwayPerformance).queryComputerListJson();
				}
			});
		}
		
		nway.invokeAll(nwayTask);
	}
}
