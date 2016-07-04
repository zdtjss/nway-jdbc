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
import com.nway.spring.jdbc.performance.repositories.SpringDataJpaPerformance;

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
	@Qualifier("hibernateJpaPerformance")
	private Performance jpaPerformance;
	
	@Autowired
	private SpringDataJpaPerformance springDataJpaPerformance;
	
	@Autowired
	@Qualifier("myBatisPerformance")
	private Performance myBatisPerformance;
	
	@Autowired
    @Qualifier("jdbcPerformance")
    private Performance jdbcPerformance;

	@Test
	public void testGetMonitor() throws InterruptedException {

		int times = 10;
		final int id = 1;

		ExecutorService nway = Executors.newFixedThreadPool(times);
		ExecutorService spring = Executors.newFixedThreadPool(times);
		ExecutorService hibernate = Executors.newFixedThreadPool(times);
		ExecutorService jpa = Executors.newFixedThreadPool(times);
		ExecutorService springDataJpa = Executors.newFixedThreadPool(times);

		Collection<Callable<Monitor>> nwayTask = new ArrayList<Callable<Monitor>>(times);
		Collection<Callable<Monitor>> springTask = new ArrayList<Callable<Monitor>>(times);
		Collection<Callable<Monitor>> hibernateTask = new ArrayList<Callable<Monitor>>(times);
		Collection<Callable<Monitor>> jpaTask = new ArrayList<Callable<Monitor>>(times);
		Collection<Callable<Monitor>> springDataJpaTask = new ArrayList<Callable<Monitor>>(times);

		for (int i = 0; i < times; i++) {

			nwayTask.add(new Callable<Monitor>() {

				@Override
				public Monitor call() throws Exception {

					return nwayPerformance.getMonitorById(id);
				}
			});

			springTask.add(new Callable<Monitor>() {

				@Override
				public Monitor call() throws Exception {

					return springJdbcPerformance.getMonitorById(id);
				}
			});

			hibernateTask.add(new Callable<Monitor>() {

				@Override
				public Monitor call() throws Exception {

					return hibernatePerformance.getMonitorById(id);
				}
			});
			
			jpaTask.add(new Callable<Monitor>() {
			    
			    @Override
			    public Monitor call() throws Exception {
			        
			        return jpaPerformance.getMonitorById(id);
			    }
			});
			
			springDataJpaTask.add(new Callable<Monitor>() {
			    
			    @Override
			    public Monitor call() throws Exception {
			        
			        return springDataJpaPerformance.getMonitorById(id);
			    }
			});
		}

		long begin = System.currentTimeMillis();
		
		nwayPerformance.getMonitorById(id);
		nway.invokeAll(nwayTask);
		
		spring.invokeAll(springTask);
		
		hibernate.invokeAll(hibernateTask);
		
		jpa.invokeAll(hibernateTask);
		
		springDataJpa.invokeAll(springDataJpaTask);
		
		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void testListMonitor() throws InterruptedException {

		int times = 30;

		ExecutorService nway = Executors.newFixedThreadPool(times);
		ExecutorService spring = Executors.newFixedThreadPool(times);
		ExecutorService hibernate = Executors.newFixedThreadPool(times);
		ExecutorService jpa = Executors.newFixedThreadPool(times);
		ExecutorService springDataJpa = Executors.newFixedThreadPool(times);
		ExecutorService mybatis = Executors.newFixedThreadPool(times);
		ExecutorService jdbc = Executors.newFixedThreadPool(times);

		Collection<Callable<List<Monitor>>> nwayTask = new ArrayList<Callable<List<Monitor>>>(times);
		Collection<Callable<List<Monitor>>> springTask = new ArrayList<Callable<List<Monitor>>>(times);
		Collection<Callable<List<Monitor>>> hibernateTask = new ArrayList<Callable<List<Monitor>>>(times);
		Collection<Callable<List<Monitor>>> jpaTask = new ArrayList<Callable<List<Monitor>>>(times);
		Collection<Callable<List<Monitor>>> springDataJpaTask = new ArrayList<Callable<List<Monitor>>>(times);
		Collection<Callable<List<Monitor>>> mybatisTask = new ArrayList<Callable<List<Monitor>>>(times);
		Collection<Callable<List<Monitor>>> jdbcTask = new ArrayList<Callable<List<Monitor>>>(times);

		for (int i = 0; i < times; i++) {

			nwayTask.add(new Callable<List<Monitor>>() {

				@Override
				public List<Monitor> call() throws Exception {

					return nwayPerformance.listMonitor();
				}
			});

			springTask.add(new Callable<List<Monitor>>() {

				@Override
				public List<Monitor> call() throws Exception {

					return springJdbcPerformance.listMonitor();
				}
			});

			hibernateTask.add(new Callable<List<Monitor>>() {

				@Override
				public List<Monitor> call() throws Exception {

					return hibernatePerformance.listMonitor();
				}
			});
			
			jpaTask.add(new Callable<List<Monitor>>() {
			    
			    @Override
			    public List<Monitor> call() throws Exception {
			        
			        return jpaPerformance.listMonitor();
			    }
			});
			
			springDataJpaTask.add(new Callable<List<Monitor>>() {
			    
			    @Override
			    public List<Monitor> call() throws Exception {
			        
			        return springDataJpaPerformance.listMonitor();
			    }
			});
			
			mybatisTask.add(new Callable<List<Monitor>>() {
			    
			    @Override
			    public List<Monitor> call() throws Exception {
			        
			        return myBatisPerformance.listMonitor();
			    }
			});
			
			jdbcTask.add(new Callable<List<Monitor>>() {
			    
			    @Override
			    public List<Monitor> call() throws Exception {
			        
			        return jdbcPerformance.listMonitor();
			    }
			});
		}

		nwayPerformance.listMonitor();
		springJdbcPerformance.listMonitor();
		hibernatePerformance.listMonitor();
		jpaPerformance.listMonitor();
		springDataJpaPerformance.listMonitor();
		myBatisPerformance.listMonitor();
		
		long begin = System.currentTimeMillis();
		
		spring.invokeAll(springTask);
		System.out.println("spring = "+(System.currentTimeMillis() - begin));
		springJdbcPerformance.listMonitor();
		
		begin = System.currentTimeMillis();
		hibernate.invokeAll(hibernateTask);
		System.out.println("hibernate = "+(System.currentTimeMillis() - begin));
		hibernatePerformance.listMonitor();
		
		begin = System.currentTimeMillis();
		jpa.invokeAll(jpaTask);
		System.out.println("jpa = "+(System.currentTimeMillis() - begin));
		jpaPerformance.listMonitor();
		
		begin = System.currentTimeMillis();
		springDataJpa.invokeAll(springDataJpaTask);
		System.out.println("springDataJpa = "+(System.currentTimeMillis() - begin));
		springDataJpaPerformance.listMonitor();
		
		begin = System.currentTimeMillis();
		jdbc.invokeAll(jdbcTask);
		System.out.println("jdbc = "+(System.currentTimeMillis() - begin));
		jdbcPerformance.listMonitor();
		
		begin = System.currentTimeMillis();
		nway.invokeAll(nwayTask);
        System.out.println("nway = "+(System.currentTimeMillis() - begin));
        nwayPerformance.listMonitor();
        
        begin = System.currentTimeMillis();
        mybatis.invokeAll(mybatisTask);
        System.out.println("mybatis = "+(System.currentTimeMillis() - begin));
        myBatisPerformance.listMonitor();
	}

	@Test
	public void testGetComputer() throws InterruptedException {

		int times = 30;
		int nThread = 2;
		final int id = 1;

		ExecutorService nway = Executors.newFixedThreadPool(nThread);
		ExecutorService spring = Executors.newFixedThreadPool(nThread);
		ExecutorService hibernate = Executors.newFixedThreadPool(nThread);
		ExecutorService jpa = Executors.newFixedThreadPool(nThread);
		ExecutorService springDataJpa = Executors.newFixedThreadPool(nThread);
		ExecutorService mybatis = Executors.newFixedThreadPool(nThread);

		Collection<Callable<Computer>> nwayTask = new ArrayList<Callable<Computer>>(times);
		Collection<Callable<Computer>> springTask = new ArrayList<Callable<Computer>>(times);
		Collection<Callable<Computer>> hibernateTask = new ArrayList<Callable<Computer>>(times);
		Collection<Callable<Computer>> jpaTask = new ArrayList<Callable<Computer>>(times);
		Collection<Callable<Computer>> springDataJpaTask = new ArrayList<Callable<Computer>>(times);
		Collection<Callable<Computer>> mybatisTask = new ArrayList<Callable<Computer>>(times);

		for (int i = 0; i < times; i++) {

			nwayTask.add(new Callable<Computer>() {

				@Override
				public Computer call() throws Exception {

					return nwayPerformance.getComputerById(id);
				}
			});

			springTask.add(new Callable<Computer>() {

				@Override
				public Computer call() throws Exception {

					return springJdbcPerformance.getComputerById(id);
				}
			});

			hibernateTask.add(new Callable<Computer>() {

				@Override
				public Computer call() throws Exception {

					return hibernatePerformance.getComputerById(id);
				}
			});
			
			jpaTask.add(new Callable<Computer>() {
			    
			    @Override
			    public Computer call() throws Exception {
			        
			        return jpaPerformance.getComputerById(id);
			    }
			});
			
			springDataJpaTask.add(new Callable<Computer>() {
			    
			    @Override
			    public Computer call() throws Exception {
			        
			        return springDataJpaPerformance.getComputerById(id);
			    }
			});
			
			mybatisTask.add(new Callable<Computer>() {
				
				@Override
				public Computer call() throws Exception {
					
					return myBatisPerformance.getComputerById(id);
				}
			});
		}
		
//		myBatisPerformance.getComputer(id);
		
		hibernatePerformance.getComputerById(id);

		long begin = System.currentTimeMillis();
		
		nwayPerformance.getComputerById(id);
		nway.invokeAll(nwayTask);
		
		spring.invokeAll(springTask);
		
		hibernate.invokeAll(hibernateTask);
		
		jpa.invokeAll(hibernateTask);
		
		springDataJpa.invokeAll(springDataJpaTask);
		
		mybatis.invokeAll(mybatisTask);
		
		System.out.println(System.currentTimeMillis() - begin);
	}

	@Test
	public void testListComputer() throws InterruptedException {

		int times = 10;
		int nThread = 10;

		ExecutorService nway = Executors.newFixedThreadPool(nThread);
		ExecutorService spring = Executors.newFixedThreadPool(nThread);
		ExecutorService hibernate = Executors.newFixedThreadPool(nThread);
		ExecutorService jpa = Executors.newFixedThreadPool(nThread);
		ExecutorService springDataJpa = Executors.newFixedThreadPool(nThread);
		ExecutorService mybatis = Executors.newFixedThreadPool(nThread);

		Collection<Callable<List<Computer>>> nwayTask = new ArrayList<Callable<List<Computer>>>(times);
		Collection<Callable<List<Computer>>> springTask = new ArrayList<Callable<List<Computer>>>(times);
		Collection<Callable<List<Computer>>> hibernateTask = new ArrayList<Callable<List<Computer>>>(times);
		Collection<Callable<List<Computer>>> jpaTask = new ArrayList<Callable<List<Computer>>>(times);
		Collection<Callable<List<Computer>>> springDataJpaTask = new ArrayList<Callable<List<Computer>>>(times);
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
			
			jpaTask.add(new Callable<List<Computer>>() {
			    
			    @Override
			    public List<Computer> call() throws Exception {
			        
			        return jpaPerformance.listComputer();
			    }
			});
			
			springDataJpaTask.add(new Callable<List<Computer>>() {
			    
			    @Override
			    public List<Computer> call() throws Exception {
			        
			        return springDataJpaPerformance.listComputer();
			    }
			});
			
			mybatisTask.add(new Callable<List<Computer>>() {
				
				@Override
				public List<Computer> call() throws Exception {
					
					return myBatisPerformance.listComputer();
				}
			});
		}
		
		nwayPerformance.listComputer();
		
		myBatisPerformance.listComputer();
		
		hibernatePerformance.listComputer();
		
		long begin = System.currentTimeMillis();
		
		nwayPerformance.listComputer();
		nway.invokeAll(nwayTask);
		nwayPerformance.listComputer();
		
		spring.invokeAll(springTask);
		springJdbcPerformance.listComputer();
		
		hibernate.invokeAll(hibernateTask);
		hibernatePerformance.listComputer();
		
		jpa.invokeAll(jpaTask);
		jpaPerformance.listComputer();
		
		springDataJpa.invokeAll(springDataJpaTask);
		jpaPerformance.listComputer();
		
		mybatis.invokeAll(mybatisTask);
		myBatisPerformance.listComputer();
		
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
