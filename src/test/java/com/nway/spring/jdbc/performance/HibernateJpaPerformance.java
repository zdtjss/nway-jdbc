package com.nway.spring.jdbc.performance;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;

@Service("hibernateJpaPerformance")
@Transactional(transactionManager = "jpaTxManager", readOnly = true)
public class HibernateJpaPerformance implements Performance {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

	@Override
	public Computer getComputerById(int id) {

	    EntityManager entityManager = entityManagerFactory.createEntityManager();

		Computer computer = (Computer) entityManager.find(Computer.class, id);

		entityManager.close();

		return computer;
	}

	@Override
	public List<Computer> listComputer() {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		List<Computer> computers = entityManager.createQuery("from Computer", Computer.class).getResultList();

		entityManager.close();
		
		return computers;
	}

	@Override
	public Monitor getMonitorById(int id) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Monitor mouse = entityManager.find(Monitor.class, id);

		entityManager.close();

		return mouse;
	}

	@Override
	public List<Monitor> listMonitor() {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		TypedQuery<Monitor> query = entityManager.createQuery("from Monitor", Monitor.class);

		List<Monitor> monitor = query.getResultList();

		entityManager.close();

		return monitor;
	}

}
