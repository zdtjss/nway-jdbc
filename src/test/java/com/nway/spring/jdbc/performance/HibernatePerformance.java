package com.nway.spring.jdbc.performance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Keyboard;
import com.nway.spring.jdbc.performance.entity.Mainframe;
import com.nway.spring.jdbc.performance.entity.Monitor;
import com.nway.spring.jdbc.performance.entity.Mouse;
import com.nway.spring.jdbc.performance.entity.Software;

@Service("hibernatePerformance")
@Transactional(transactionManager = "hibernateTxManager", readOnly = true)
public class HibernatePerformance implements Performance {

    @Autowired
    private SessionFactory sessionFactory;
	
	@Override
	public Computer getComputerById(int id) {

		Session session = sessionFactory.openSession();

		Computer computer = (Computer) session.get(Computer.class, id);

		session.close();

		return computer;
	}

	@Override
	public List<Computer> listComputer() {

		Session session = sessionFactory.openSession();
		
		@SuppressWarnings("unchecked")
		List<Computer> computers = session.createQuery("from com.nway.spring.jdbc.performance.entity.Computer").list();

		session.close();
		
		return computers;
	}

	@Override
	public Monitor getMonitorById(int id) {

		Session session = sessionFactory.openSession();
		
		Monitor mouse = (Monitor) session.get(Monitor.class, id);
		
		session.close();
		
		return mouse;
	}

	@Override
	public List<Monitor> listMonitor() {

		Session session = sessionFactory.openSession();
		
		Query query = session.createQuery("from Monitor");
		
		@SuppressWarnings("unchecked")
		List<Monitor> monitor = query.list();

		session.close();
		
		return monitor;
	}

    @Transactional(transactionManager = "hibernateTxManager", readOnly = false)
	public void initDB() {

		Session session = sessionFactory.openSession();

		Transaction transaction = session.beginTransaction();

		for (int i = 0; i < 30000; i++) {

			Computer computer = new Computer();

			computer.setBrand("hp");
			computer.setPrice(1.1f);
			computer.setProductionDate(new Date());
			
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

			computer.setMainframe(mainframe);
			computer.setMonitor(monitor);
			computer.setMouse(mouse);
			computer.setKeyboard(keyboard);
			
			List<Software> software = new ArrayList<Software>();
			
			Software s1 = new Software();
			
			s1.setName("º∆À„∆˜");
			s1.setSize(100);
			s1.setVender("Œ¢»Ì");
			s1.setVender("1.0");
			
			software.add(s1);
			
			Software s2 = new Software();
			
			s2.setName("º∆À„∆˜");
			s2.setSize(100);
			s2.setVender("Œ¢»Ì");
			s2.setVender("1.0");
			
			software.add(s2);
			
			Software s3 = new Software();
			
			s3.setName("º∆À„∆˜");
			s3.setSize(100);
			s3.setVender("Œ¢»Ì");
			s3.setVender("1.0");
			
			software.add(s3);
			
			computer.setSoftware(software);

			session.save(computer);
		}

		transaction.commit();
		
		session.close();
	}

}
