package com.nway.spring.jdbc.performance;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Service;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Keyboard;
import com.nway.spring.jdbc.performance.entity.Mainframe;
import com.nway.spring.jdbc.performance.entity.Monitor;
import com.nway.spring.jdbc.performance.entity.Mouse;

@Service("hibernatePerformance")
public class HibernatePerformance implements Performance {

	private SessionFactory sessionFactory = null;
	
	private EntityManagerFactory entityManagerFactory;

	@PostConstruct
	private void buildSessionFactory() {

		try {

			Configuration cfg = new Configuration().configure();
			
			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = cfg.buildSessionFactory(
					new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build());
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		
		entityManagerFactory = Persistence.createEntityManagerFactory("nway.perfomance.jpa");
	}

	@Override
	public Computer getComputer(int id) {

		Session session = sessionFactory.openSession();

		Computer computer = (Computer) session.get(Computer.class, id);

		session.close();

		return computer;
	}

	@Override
	public List<Computer> listComputer() {

//		Session session = sessionFactory.openSession();
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		@SuppressWarnings("unchecked")
//		List<Computer> computers = session.createQuery("from com.nway.spring.jdbc.performance.entity.Computer").list();
		List<Computer> computers = entityManager.createQuery("from Computer", Computer.class).getResultList();

//		session.close();
		entityManager.close();
		
		return computers;
	}

	@Override
	public Monitor getMonitor(int id) {

//		Session session = sessionFactory.openSession();
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();

//		Monitor mouse = (Monitor) session.get(Monitor.class, id);
		Monitor mouse = entityManager.find(Monitor.class, id);

//		session.close();
		
		entityManager.close();

		return mouse;
	}

	@Override
	public List<Monitor> listMonitor(int num) {

//		Session session = sessionFactory.openSession();
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();

//		Query query = session.createQuery("from Monitor");
		
		TypedQuery<Monitor> query = entityManager.createQuery("from Monitor", Monitor.class);

		query.setFirstResult(1);

		query.setMaxResults(10);

		@SuppressWarnings("unchecked")
		List<Monitor> monitor = query.getResultList();

//		session.close();
		
		entityManager.close();

		return monitor;
	}

	public void initDB() {

		Session session = sessionFactory.openSession();

		Transaction transaction = session.beginTransaction();

		for (int i = 0; i < 100; i++) {

			Computer computer = new Computer();

			Monitor monitor = new Monitor();

			monitor.setBrand("aoc");
			monitor.setModel("100-01");
			monitor.setType(0);
			monitor.setMaxResolution("1920*1080");
			monitor.setProductionDate(new Date());
			monitor.setPrice(1.1f);

			Mainframe mainframe = new Mainframe();

			mainframe.setBrand("GIGABYTE");
			mainframe.setModel("90-09");
			mainframe.setType(1);
			mainframe.setPrice(1.1f);
			mainframe.setProductionDate(new Date());

			Mouse mouse = new Mouse();

			mouse.setBrand("ausu");
			mouse.setColor("black");
			mouse.setModel("M-1");
			mouse.setType(1);
			mouse.setWireless(false);
			mouse.setProductionDate(new Date());

			Keyboard keyboard = new Keyboard();

			keyboard.setBrand("dell");
			keyboard.setModel("10-01");
			keyboard.setType(1);
			keyboard.setPrice(1.1f);
			keyboard.setInterfaceType(1);
			keyboard.setWireless(false);
			keyboard.setPhoto(null);
			keyboard.setProductionDate(new Date());

			computer.setBrand("hp");
			computer.setPrice(1.1f);
			computer.setProductionDate(new Date());

			computer.setMainframe(mainframe);
			computer.setMonitor(monitor);
			computer.setMouse(mouse);
			computer.setKeyboard(keyboard);

			session.save(computer);
		}

		transaction.commit();
	}

}
