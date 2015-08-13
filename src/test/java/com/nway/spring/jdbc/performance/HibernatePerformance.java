package com.nway.spring.jdbc.performance;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.hibernate.Query;
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

		Session session = sessionFactory.openSession();

		@SuppressWarnings("unchecked")
		List<Computer> computers = session.createQuery("from Computer").list();

		session.close();

		return computers;
	}

	@Override
	public Monitor getMonitor(int id) {

		Session session = sessionFactory.openSession();

		Monitor mouse = (Monitor) session.get(Monitor.class, id);

		session.close();

		return mouse;
	}

	@Override
	public List<Monitor> listMonitor(int num) {

		Session session = sessionFactory.openSession();

		Query query = session.createQuery("from Monitor");

		query.setFirstResult(1);

		query.setMaxResults(10);

		@SuppressWarnings("unchecked")
		List<Monitor> monitor = query.list();

		session.close();

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
