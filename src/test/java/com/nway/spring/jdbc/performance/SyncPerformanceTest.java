package com.nway.spring.jdbc.performance;

import com.nway.spring.jdbc.BaseTest;
import com.nway.spring.jdbc.performance.dal.po.ComputerPoEntity;
import com.nway.spring.jdbc.performance.dal.po.MonitorPoEntity;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;
import com.nway.spring.jdbc.performance.repositories.SpringDataJpaPerformance;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class SyncPerformanceTest extends BaseTest {

    @Autowired
    @Qualifier("nwayPerformance")
    private NwayPerformance nwayPerformance;

    @Autowired
    @Qualifier("nwayLambdaPerformance")
    private NwayLambdaPerformance nwayLambdaPerformance;

    @Autowired
    @Qualifier("myBatisGePerformance")
    private MyBatisGePerformance myBatisGePerformance;

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

        ExecutorService executorService = Executors.newFixedThreadPool(times);

        Collection<Callable<Monitor>> nwayTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> nwayLambdaTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> springTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> hibernateTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> jpaTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> springDataJpaTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> mybatisTask = new ArrayList<>(times);
        Collection<Callable<Monitor>> mybatisPlusTask = new ArrayList<>(times);
        Collection<Callable<MonitorPoEntity>> mybatisGeTask = new ArrayList<>(times);

        Monitor monitor = nwayPerformance.listMonitor().stream().findFirst().get();
        int id = monitor.getId();

        for (int i = 0; i < times; i++) {

            nwayTask.add(() -> nwayPerformance.getMonitorById(id));

            nwayLambdaTask.add(() -> nwayLambdaPerformance.getMonitorById(id));

            mybatisGeTask.add(() -> myBatisGePerformance.getMonitorById(id));

            springTask.add(() -> springJdbcPerformance.getMonitorById(id));

            hibernateTask.add(() -> hibernatePerformance.getMonitorById(id));

            jpaTask.add(() -> jpaPerformance.getMonitorById(id));

            springDataJpaTask.add(() -> springDataJpaPerformance.getMonitorById(id));

            mybatisTask.add(() -> myBatisPerformance.getMonitorById(id));

            mybatisPlusTask.add(() -> myBatisPlusPerformance.getMonitorById(id));
        }

        long begin = System.currentTimeMillis();

        nwayPerformance.getMonitorById(id);
        myBatisPlusPerformance.getMonitorById(id);

        for (int i = 0; i < 10; i++) {
       /* nway.invokeAll(nwayTask);
        System.out.println("nway = " + (System.currentTimeMillis() - begin));*/

            begin = System.currentTimeMillis();
            executorService.invokeAll(nwayLambdaTask);
            System.out.println("nwayLambda = " + (System.currentTimeMillis() - begin));

        /*begin = System.currentTimeMillis();
        List<Future<MonitorPoEntity>> futureListGe = executorService.invokeAll(mybatisGeTask);
        System.out.println("mybatisGe = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        executorService.invokeAll(springTask);
        System.out.println("spring = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        executorService.invokeAll(hibernateTask);
        System.out.println("hibernate = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        executorService.invokeAll(hibernateTask);
        System.out.println("jpa = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        executorService.invokeAll(springDataJpaTask);
        System.out.println("springDataJpa = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        executorService.invokeAll(mybatisTask);
        System.out.println("mybatis = " + (System.currentTimeMillis() - begin));*/

            begin = System.currentTimeMillis();
            executorService.invokeAll(mybatisPlusTask);
            System.out.println("mybatisPlus = " + (System.currentTimeMillis() - begin));

        }
    }

    @Test
    public void testListMonitor() throws InterruptedException {

        int times = 30;
        int threadn = 50;

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
        Collection<Callable<List<MonitorPoEntity>>> mybatisGeTask = new ArrayList<>(times);

        for (int i = 0; i < times; i++) {

            nwayTask.add(() -> nwayPerformance.listMonitor());

            nwayLambdaTask.add(() -> nwayLambdaPerformance.listMonitor());

            mybatisGeTask.add(() -> myBatisGePerformance.listMonitor());

            springTask.add(() -> springJdbcPerformance.listMonitor());

            hibernateTask.add(() -> hibernatePerformance.listMonitor());

            jpaTask.add(() -> jpaPerformance.listMonitor());

            springDataJpaTask.add(() -> springDataJpaPerformance.listMonitor());

            mybatisTask.add(() -> myBatisPerformance.listMonitor());

            mybatisPlusTask.add(() -> myBatisPlusPerformance.listMonitor());

            jdbcTask.add(() -> jdbcPerformance.listMonitor());

            scriptTask.add(() -> scriptPerformance.listMonitor());
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
        myBatisGePerformance.listMonitor();

        for(int i = 0;i<10;i++) {
            long begin = System.currentTimeMillis();

            /*executorService.invokeAll(springTask);
            System.out.println("spring = " + (System.currentTimeMillis() - begin));
            springJdbcPerformance.listMonitor();

            begin = System.currentTimeMillis();
            executorService.invokeAll(hibernateTask);
            System.out.println("hibernate = " + (System.currentTimeMillis() - begin));
            hibernatePerformance.listMonitor();

            begin = System.currentTimeMillis();
            executorService.invokeAll(jpaTask);
            System.out.println("jpa = " + (System.currentTimeMillis() - begin));
            jpaPerformance.listMonitor();

            begin = System.currentTimeMillis();
            executorService.invokeAll(springDataJpaTask);
            System.out.println("springDataJpa = " + (System.currentTimeMillis() - begin));
            springDataJpaPerformance.listMonitor();*/

        /*begin = System.currentTimeMillis();
        executorService.invokeAll(nwayTask);
        System.out.println("nway = " + (System.currentTimeMillis() - begin));
        nwayPerformance.listMonitor();*/

            begin = System.currentTimeMillis();
            executorService.invokeAll(nwayLambdaTask);
            log.info("nwayLambda = {}", System.currentTimeMillis() - begin);
            nwayLambdaPerformance.listMonitor();

            /*begin = System.currentTimeMillis();
            executorService.invokeAll(mybatisGeTask);
            System.out.println("mybatisGe = " + (System.currentTimeMillis() - begin));
            myBatisGePerformance.listMonitor();

            begin = System.currentTimeMillis();
            executorService.invokeAll(mybatisTask);
            System.out.println("mybatis = " + (System.currentTimeMillis() - begin));
            myBatisPerformance.listMonitor();*/

            begin = System.currentTimeMillis();
            executorService.invokeAll(mybatisPlusTask);
            log.info("myBatisPlus = {}", System.currentTimeMillis() - begin);
            myBatisPlusPerformance.listMonitor();

            /*begin = System.currentTimeMillis();
            executorService.invokeAll(scriptTask);
            System.out.println("script = " + (System.currentTimeMillis() - begin));*/
        }
    }

    @Test
    public void testGetComputer() throws InterruptedException {

        int times = 30;
        int nThread = 10;
        final int id = 80007;

        ExecutorService executorService = Executors.newFixedThreadPool(nThread);

        Collection<Callable<Computer>> nwayTask = new ArrayList<>(times);
        Collection<Callable<Computer>> nwayLambdaTask = new ArrayList<>(times);
        Collection<Callable<Computer>> springTask = new ArrayList<>(times);
        Collection<Callable<Computer>> hibernateTask = new ArrayList<>(times);
        Collection<Callable<Computer>> jpaTask = new ArrayList<>(times);
        Collection<Callable<Computer>> springDataJpaTask = new ArrayList<>(times);
        Collection<Callable<Computer>> mybatisTask = new ArrayList<>(times);
        Collection<Callable<Computer>> mybatisPlusTask = new ArrayList<>(times);
        Collection<Callable<ComputerPoEntity>> mybatisGeTask = new ArrayList<>(times);

        for (int i = 0; i < times; i++) {

            nwayTask.add(() -> nwayPerformance.getComputerById(id));

            nwayLambdaTask.add(() -> nwayLambdaPerformance.getComputerById(id));

            mybatisGeTask.add(() -> myBatisGePerformance.getComputerById(id));

            springTask.add(() -> springJdbcPerformance.getComputerById(id));

            hibernateTask.add(() -> hibernatePerformance.getComputerById(id));

            jpaTask.add(() -> jpaPerformance.getComputerById(id));

            springDataJpaTask.add(() -> springDataJpaPerformance.getComputerById(id));

            mybatisTask.add(() -> myBatisPerformance.getComputerById(id));

            mybatisPlusTask.add(() -> myBatisPlusPerformance.getComputerById(id));
        }

		myBatisPerformance.getComputerById(id);
        hibernatePerformance.getComputerById(id);
        nwayLambdaPerformance.getComputerById(id);
        myBatisPlusPerformance.getComputerById(id);

        for (int i = 0; i < 10; i++) {
            long begin = System.currentTimeMillis();

       /* begin = System.currentTimeMillis();
        executorService.invokeAll(nwayTask);
        System.out.println("nway = " + (System.currentTimeMillis() - begin));*/

            begin = System.currentTimeMillis();
            executorService.invokeAll(nwayLambdaTask);
            log.info("lambdaNway = {}", System.currentTimeMillis() - begin);

		/*begin = System.currentTimeMillis();
        executorService.invokeAll(mybatisGeTask);
        System.out.println("mybatisGe = " + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
        executorService.invokeAll(springTask);
        System.out.println("spring = " + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
        executorService.invokeAll(hibernateTask);
        System.out.println("hibernate = " + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
        executorService.invokeAll(hibernateTask);
        System.out.println("jpa = " + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
        executorService.invokeAll(springDataJpaTask);
        System.out.println("springDataJpa = " + (System.currentTimeMillis() - begin));

            begin = System.currentTimeMillis();
            executorService.invokeAll(mybatisTask);
            System.out.println("mybatis = " + (System.currentTimeMillis() - begin));*/

            begin = System.currentTimeMillis();
            executorService.invokeAll(mybatisPlusTask);
            log.info("mybatisPlus = {}", System.currentTimeMillis() - begin);
        }

    }

    @Test
    public void testListComputer() throws InterruptedException {

        int times = 1500;
        int nThread = 1500;

        ExecutorService executorService = Executors.newFixedThreadPool(nThread);

        Collection<Callable<List<Computer>>> nwayTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> nwayLambdaTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> springTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> hibernateTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> jpaTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> springDataJpaTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> mybatisTask = new ArrayList<>(times);
        Collection<Callable<List<Computer>>> mybatisPlusTask = new ArrayList<>(times);
        Collection<Callable<List<ComputerPoEntity>>> mybatisGeTask = new ArrayList<>(times);

        for (int i = 0; i < times; i++) {

            nwayTask.add(() -> nwayPerformance.listComputer());

            nwayLambdaTask.add(() -> nwayLambdaPerformance.listComputer());

            mybatisGeTask.add(() -> myBatisGePerformance.listComputer());

            springTask.add(() -> springJdbcPerformance.listComputer());

            hibernateTask.add(() -> hibernatePerformance.listComputer());

            jpaTask.add(() -> jpaPerformance.listComputer());

            springDataJpaTask.add(() -> springDataJpaPerformance.listComputer());

            mybatisTask.add(() -> myBatisPerformance.listComputer());

            mybatisPlusTask.add(() -> myBatisPlusPerformance.listComputer());
        }

        IntStream.range(0, 30).forEach(e -> {
            nwayLambdaPerformance.listComputer();
            myBatisPerformance.listComputer();
            myBatisPlusPerformance.listComputer();
        });

        System.out.println();

        long begin = System.currentTimeMillis();

        for (int i = 0; i < 2; i++) {

        /*begin = System.currentTimeMillis();
        executorService.invokeAll(nwayTask);
        System.out.println("nway = " + (System.currentTimeMillis() - begin));*/

        /*begin = System.currentTimeMillis();
        nwayLambdaPerformance.listComputer();
        System.out.println("nwayLambdaPerformance = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        List<Future<List<ComputerPoEntity>>> futureListGe = executorService.invokeAll(mybatisGeTask);
        System.out.println("mybatisGe = " + (System.currentTimeMillis() - begin));*/

       /* begin = System.currentTimeMillis();
        executorService.invokeAll(springTask);
        System.out.println("spring = " + (System.currentTimeMillis() - begin));*/

        /*begin = System.currentTimeMillis();
        springJdbcPerformance.listComputer();
        System.out.println("springJdbcPerformance = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        executorService.invokeAll(hibernateTask);
        System.out.println("hibernate = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        hibernatePerformance.listComputer();
        System.out.println("hibernatePerformance = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        executorService.invokeAll(jpaTask);
        System.out.println("jpa = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        jpaPerformance.listComputer();
        System.out.println("jpaPerformance = " + (System.currentTimeMillis() - begin));

        begin = System.currentTimeMillis();
        executorService.invokeAll(springDataJpaTask);
        System.out.println("springDataJpa = " + (System.currentTimeMillis() - begin));*/

            begin = System.currentTimeMillis();
            executorService.invokeAll(mybatisPlusTask);
            System.out.println("mybatisPlus = " + (System.currentTimeMillis() - begin));

//            begin = System.currentTimeMillis();
//            executorService.invokeAll(mybatisTask);
//            System.out.println("mybatis = " + (System.currentTimeMillis() - begin));

//            begin = System.currentTimeMillis();
//            executorService.invokeAll(nwayLambdaTask);
//            System.out.println("nwayLambda = " + (System.currentTimeMillis() - begin));

        }
    }

}
