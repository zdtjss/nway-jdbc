package com.nway.spring.jdbc.performance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nway.spring.jdbc.BaseTest;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;
import com.nway.spring.jdbc.performance.repositories.SpringDataJpaPerformance;

@Service
public class SyncPerformanceTest extends BaseTest {

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
    @Qualifier("jdbcPerformance")
    private JdbcPerformance jdbcPerformance;

    @Autowired
    @Qualifier("scriptSolutionPerformance")
    private ScriptSolutionPerformance scriptPerformance;

    @Test
    public void testGetMonitor() throws InterruptedException {

        int times = 10;
        final int id = 1;

        ExecutorService executorService = Executors.newFixedThreadPool(times);

        Collection<Callable<Monitor>> nwayTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> nwayLambdaTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> springTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> hibernateTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> jpaTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> springDataJpaTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> mybatisTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> mybatisPlusTask = new ArrayList<>(times);

        for (int i = 0; i < times; i++) {

            nwayTask.add(new Callable<Monitor>() {
                @Override
                public Monitor call() throws Exception {
                    return nwayPerformance.getMonitorById(id);
                }
            });

            nwayLambdaTask.add(new Callable<Monitor>() {
                @Override
                public Monitor call() throws Exception {
                    return nwayLambdaPerformance.getMonitorById(id);
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

            mybatisTask.add(new Callable<Monitor>() {
                @Override
                public Monitor call() throws Exception {
                    return myBatisPerformance.getMonitorById(id);
                }
            });

            mybatisPlusTask.add(new Callable<Monitor>() {
                @Override
                public Monitor call() throws Exception {
                    return myBatisPlusPerformance.getMonitorById(id);
                }
            });
        }

        long begin = System.currentTimeMillis();

        nwayPerformance.getMonitorById(id);

       /* List<Future<Monitor>> futureList = nway.invokeAll(nwayTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("nway = " + (System.currentTimeMillis() - begin));*/

        List<Future<Monitor>> futureList = executorService.invokeAll(nwayLambdaTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("nwayLambda = " + (System.currentTimeMillis() - begin));

        futureList = executorService.invokeAll(springTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("spring = " + (System.currentTimeMillis() - begin));

        futureList = executorService.invokeAll(hibernateTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("hibernate = " + (System.currentTimeMillis() - begin));

        futureList = executorService.invokeAll(hibernateTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("jpa = " + (System.currentTimeMillis() - begin));

        futureList = executorService.invokeAll(springDataJpaTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("springDataJpa = " + (System.currentTimeMillis() - begin));

        futureList = executorService.invokeAll(mybatisTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("mybatis = " + (System.currentTimeMillis() - begin));

        futureList = executorService.invokeAll(mybatisPlusTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("mybatisPlus = " + (System.currentTimeMillis() - begin));

    }

    @Test
    public void testListMonitor() throws InterruptedException {

        int times = 50;
        int threadn = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(threadn);

        Collection<Callable<List<Monitor>>> nwayTask = new ArrayList<>(times);
        Collection<Callable<List<Monitor>>> nwayLambdaTask = new ArrayList<>(times);
        Collection<Callable<List<Monitor>>> springTask = new ArrayList<>(times);
        Collection<Callable<List<Monitor>>> hibernateTask = new ArrayList<>(times);
        Collection<Callable<List<Monitor>>> jpaTask = new ArrayList<>(times);
        Collection<Callable<List<Monitor>>> springDataJpaTask = new ArrayList<>(times);
        Collection<Callable<List<Monitor>>> mybatisTask = new ArrayList<>(times);
        Collection<Callable<List<Monitor>>> mybatisPlusTask = new ArrayList<>(times);
        Collection<Callable<List<Monitor>>> jdbcTask = new ArrayList<>(times);
        Collection<Callable<List<Monitor>>> scriptTask = new ArrayList<>(times);

        for (int i = 0; i < times; i++) {

            nwayTask.add(new Callable<List<Monitor>>() {
                @Override
                public List<Monitor> call() throws Exception {
                    return nwayPerformance.listMonitor();
                }
            });

            nwayLambdaTask.add(new Callable<List<Monitor>>() {
                @Override
                public List<Monitor> call() throws Exception {
                    return nwayLambdaPerformance.listMonitor();
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

            mybatisPlusTask.add(new Callable<List<Monitor>>() {
                @Override
                public List<Monitor> call() throws Exception {
                    return myBatisPlusPerformance.listMonitor();
                }
            });

            jdbcTask.add(new Callable<List<Monitor>>() {
                @Override
                public List<Monitor> call() throws Exception {
                    return jdbcPerformance.listMonitor();
                }
            });

            scriptTask.add(new Callable<List<Monitor>>() {
                @Override
                public List<Monitor> call() throws Exception {
                    return scriptPerformance.listMonitor();
                }
            });
        }

        nwayPerformance.listMonitor();
        nwayLambdaPerformance.listMonitor();
        springJdbcPerformance.listMonitor();
        hibernatePerformance.listMonitor();
        jpaPerformance.listMonitor();
        springDataJpaPerformance.listMonitor();
		myBatisPerformance.listMonitor();
        myBatisPlusPerformance.listMonitor();
        scriptPerformance.listMonitor();

        long begin = System.currentTimeMillis();

        List<Future<List<Monitor>>> futureList = executorService.invokeAll(springTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("spring = " + (System.currentTimeMillis() - begin));
        springJdbcPerformance.listMonitor();

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(hibernateTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("hibernate = " + (System.currentTimeMillis() - begin));
        hibernatePerformance.listMonitor();

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(jpaTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("jpa = " + (System.currentTimeMillis() - begin));
        jpaPerformance.listMonitor();

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(springDataJpaTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("springDataJpa = " + (System.currentTimeMillis() - begin));
        springDataJpaPerformance.listMonitor();

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(jdbcTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("jdbc = " + (System.currentTimeMillis() - begin));
        jdbcPerformance.listMonitor();

        /*begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(nwayTask);
        System.out.println("nway = " + (System.currentTimeMillis() - begin));
        nwayPerformance.listMonitor();*/

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(nwayLambdaTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("nwayLambda = " + (System.currentTimeMillis() - begin));
        nwayLambdaPerformance.listMonitor();

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(mybatisTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("mybatis = "+(System.currentTimeMillis() - begin));
        myBatisPerformance.listMonitor();

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(mybatisPlusTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("myBatisPlus = " + (System.currentTimeMillis() - begin));
        myBatisPlusPerformance.listMonitor();

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(scriptTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("script = " + (System.currentTimeMillis() - begin));
    }

    @Test
    public void testGetComputer() throws InterruptedException {

        int times = 30;
        int nThread = 10;
        final int id = 1;

        ExecutorService executorService = Executors.newFixedThreadPool(nThread);

        Collection<Callable<Computer>> nwayTask = new ArrayList<>(times);
        Collection<Callable<Computer>> nwayLambdaTask = new ArrayList<>(times);
        Collection<Callable<Computer>> springTask = new ArrayList<>(times);
        Collection<Callable<Computer>> hibernateTask = new ArrayList<>(times);
        Collection<Callable<Computer>> jpaTask = new ArrayList<>(times);
        Collection<Callable<Computer>> springDataJpaTask = new ArrayList<>(times);
        Collection<Callable<Computer>> mybatisTask = new ArrayList<>(times);
        Collection<Callable<Computer>> mybatisPlusTask = new ArrayList<>(times);

        for (int i = 0; i < times; i++) {

            nwayTask.add(new Callable<Computer>() {
                @Override
                public Computer call() throws Exception {
                    return nwayPerformance.getComputerById(id);
                }
            });

            nwayLambdaTask.add(new Callable<Computer>() {
                @Override
                public Computer call() throws Exception {
                    return nwayLambdaPerformance.getComputerById(id);
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

            mybatisPlusTask.add(new Callable<Computer>() {
                @Override
                public Computer call() throws Exception {
                    return myBatisPlusPerformance.getComputerById(id);
                }
            });
        }

		myBatisPerformance.getComputerById(id);

        hibernatePerformance.getComputerById(id);

        long begin = System.currentTimeMillis();

        nwayPerformance.getComputerById(id);

        myBatisPlusPerformance.getMonitorById(id);

       /* begin = System.currentTimeMillis();
        List<Future<Computer>> futureList = executorService.invokeAll(nwayTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("nway = " + (System.currentTimeMillis() - begin));*/

		begin = System.currentTimeMillis();
        List<Future<Computer>> futureList = executorService.invokeAll(nwayLambdaTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("lambdaNway = " + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(springTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("spring = " + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(hibernateTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("hibernate = " + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(hibernateTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("jpa = " + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(springDataJpaTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("springDataJpa = " + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(mybatisTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
             break;
            }
        }
        System.out.println("mybatis = " + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(mybatisPlusTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("mybatisPlus = " + (System.currentTimeMillis() - begin));

    }

    @Test
    public void testListComputer() throws InterruptedException {

        int times = 10;
        int nThread = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(nThread);

        Collection<Callable<List<Computer>>> nwayTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> nwayLambdaTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> springTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> hibernateTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> jpaTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> springDataJpaTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> mybatisTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> mybatisPlusTask = new ArrayList<>(times);

        for (int i = 0; i < times; i++) {

            nwayTask.add(new Callable<List<Computer>>() {
                @Override
                public List<Computer> call() throws Exception {
                    return nwayPerformance.listComputer();
                }
            });

            nwayLambdaTask.add(new Callable<List<Computer>>() {
                @Override
                public List<Computer> call() throws Exception {
                    return nwayLambdaPerformance.listComputer();
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

            mybatisPlusTask.add(new Callable<List<Computer>>() {
                @Override
                public List<Computer> call() throws Exception {
                    return myBatisPlusPerformance.listComputer();
                }
            });
        }

        nwayPerformance.listComputer();

        nwayLambdaPerformance.listComputer();

		myBatisPerformance.listComputer();

        hibernatePerformance.listComputer();

        long begin = System.currentTimeMillis();

        nwayPerformance.listComputer();

        List<Future<List<Computer>>> futureList = null;
       /* begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(nwayTask);
        while (true) {
            if (futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("nway = " + (System.currentTimeMillis() - begin));*/

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(nwayLambdaTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("nwayLambda = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        nwayPerformance.listComputer();
        System.out.println("nwayPerformance = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(springTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("spring = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        springJdbcPerformance.listComputer();
        System.out.println("springJdbcPerformance = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(hibernateTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("hibernate = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        hibernatePerformance.listComputer();
        System.out.println("hibernatePerformance = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(jpaTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("jpa = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        jpaPerformance.listComputer();
        System.out.println("jpaPerformance = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(springDataJpaTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("springDataJpa = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(mybatisPlusTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("mybatisPlus = " + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
        futureList = executorService.invokeAll(mybatisTask);
        while (true) {
            if(futureList.stream().filter(e -> e.isDone()).count() == mybatisTask.size()) {
                break;
            }
        }
        System.out.println("mybatis = " + (System.currentTimeMillis() - begin));

    }

}
