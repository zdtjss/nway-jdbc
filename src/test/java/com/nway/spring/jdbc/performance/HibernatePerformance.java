package com.nway.spring.jdbc.performance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
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

        Computer computer = session.get(Computer.class, id);

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

        Monitor mouse = session.get(Monitor.class, id);

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

        for (int i = 0; i < 30; i++) {

            Computer computer = new Computer();

            computer.setBrand("HP" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            computer.setPrice(Double.valueOf(Math.random() * Integer.MAX_VALUE).floatValue());
            computer.setProductionDate(new Date());

            Monitor monitor = new Monitor();

            monitor.setBrand("aoc" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            monitor.setModel("100-01" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            monitor.setType((int) (Math.random() * 100) % 2);
            monitor.setMaxResolution("1920*1080" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            monitor.setProductionDate(new Date());
            monitor.setPrice(1.1f);

            Mainframe mainframe = new Mainframe();

            mainframe.setBrand("GIGABYTE" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            mainframe.setModel("90-09" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            mainframe.setType(Double.valueOf(Math.random() * 10).intValue());
            mainframe.setPrice(Double.valueOf(Math.random() * Integer.MAX_VALUE).floatValue());
            mainframe.setProductionDate(new Date());

            Mouse mouse = new Mouse();

            mouse.setBrand("AUSU" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            mouse.setColor("black" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            mouse.setModel("M-1" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            mouse.setType(Double.valueOf(Math.random() * 10).intValue());
            mouse.setIsWireless(Double.valueOf(Math.random() * Integer.MAX_VALUE).longValue() % 2 == 1);
            mouse.setProductionDate(new Date());

            Keyboard keyboard = new Keyboard();

            keyboard.setBrand("DELL" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            keyboard.setModel("10-01" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            keyboard.setType(Double.valueOf(Math.random() * 10).intValue());
            keyboard.setPrice(Double.valueOf(Math.random() * Integer.MAX_VALUE).floatValue());
            keyboard.setInterfaceType(Double.valueOf(Math.random() * 10).intValue());
            keyboard.setIsWireless(Double.valueOf(Math.random() * Integer.MAX_VALUE).longValue() % 2 == 1);
            keyboard.setPhoto(null);
            keyboard.setProductionDate(new Date());

            computer.setMainframe(mainframe);
            computer.setMonitor(monitor);
            computer.setMouse(mouse);
            computer.setKeyboard(keyboard);

            List<Software> software = new ArrayList<>();

            Software s1 = new Software();

            s1.setName("计算器" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            s1.setSize(Double.valueOf(Math.random() * Integer.MAX_VALUE).longValue());
            s1.setVender("微软" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            s1.setVersion("1.0");

            software.add(s1);

            Software s2 = new Software();

            s2.setName("计算器" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            s2.setSize(Double.valueOf(Math.random() * Integer.MAX_VALUE).longValue());
            s2.setVender("微软" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            s2.setVersion("1.0");

            software.add(s2);

            Software s3 = new Software();

            s3.setName("计算器" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            s3.setSize(Double.valueOf(Math.random() * Integer.MAX_VALUE).longValue());
            s3.setVender("微软" + UUID.randomUUID().toString().replace('-', ' ').toUpperCase());
            s3.setVersion("1.0");

            software.add(s3);

            computer.setSoftware(software);

            session.save(computer);
        }

        transaction.commit();

        session.close();
    }

}
