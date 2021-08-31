package com.nway.spring.jdbc.performance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nway.spring.jdbc.SqlExecutor;
import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.ComputerSoftware;
import com.nway.spring.jdbc.performance.entity.Keyboard;
import com.nway.spring.jdbc.performance.entity.Mainframe;
import com.nway.spring.jdbc.performance.entity.Monitor;
import com.nway.spring.jdbc.performance.entity.Mouse;
import com.nway.spring.jdbc.performance.entity.Software;
import com.nway.spring.jdbc.sql.SQL;
import com.nway.spring.jdbc.sql.builder.SqlBuilder;
import com.nway.spring.jdbc.sql.builder.QueryBuilder;
import org.springframework.util.CollectionUtils;

@Service("nwayLambdaPerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class NwayLambdaPerformance implements Performance {

    @Autowired
    private SqlExecutor sqlExecutor;

    @Override
    public Computer getComputerById(int id) {

        SqlBuilder computerSoftwareSql = SQL.query(ComputerSoftware.class).where().eq(ComputerSoftware::getComputerId, id);

        Computer computer = sqlExecutor.queryById(id, Computer.class);

        computer.setMainframe(sqlExecutor.queryById(computer.getMainframeId(), Mainframe.class));
        computer.setMonitor(sqlExecutor.queryById(computer.getMonitorId(), Monitor.class));
        computer.setMouse(sqlExecutor.queryById(computer.getMouseId(), Mouse.class));
        computer.setKeyboard(sqlExecutor.queryById(computer.getKeyboardId(), Keyboard.class));
        List<ComputerSoftware> computerSoftwareList = sqlExecutor.queryList(computerSoftwareSql);
        List<Integer> softwareIdList = computerSoftwareList.stream().map(ComputerSoftware::getSoftwareId).collect(Collectors.toList());
        computer.setSoftware(sqlExecutor.queryList(softwareIdList, Software.class));

        return computer;
    }

    @Override
    public List<Computer> listComputer() {

        QueryBuilder<Computer> computerSql = SQL.query(Computer.class);

        List<Computer> computers = sqlExecutor.queryList(computerSql);

        List<Integer> mainframeIds = computers.stream().map(Computer::getMainframeId).collect(Collectors.toList());
        List<Integer> monitorIds = computers.stream().map(Computer::getMonitorId).collect(Collectors.toList());
        List<Integer> mouseIds = computers.stream().map(Computer::getMouseId).collect(Collectors.toList());
        List<Integer> keyboardIds = computers.stream().map(Computer::getKeyboardId).collect(Collectors.toList());

        List<Mainframe> mainframeList = sqlExecutor.queryList(mainframeIds, Mainframe.class);
        List<Monitor> monitorList = sqlExecutor.queryList(monitorIds, Monitor.class);
        List<Mouse> mouseList = sqlExecutor.queryList(mouseIds, Mouse.class);
        List<Keyboard> keyboardList = sqlExecutor.queryList(keyboardIds, Keyboard.class);

        Map<Integer, Mainframe> mainframeMap = mainframeList.stream().collect(Collectors.toMap(Mainframe::getId, Function.identity()));
        Map<Integer, Monitor> monitorMap = monitorList.stream().collect(Collectors.toMap(Monitor::getId, Function.identity()));
        Map<Integer, Mouse> mouseMap = mouseList.stream().collect(Collectors.toMap(Mouse::getId, Function.identity()));
        Map<Integer, Keyboard> keyboardMap = keyboardList.stream().collect(Collectors.toMap(Keyboard::getId, Function.identity()));

        SqlBuilder computerSoftwareSql = SQL.query(ComputerSoftware.class)
                .where().in(ComputerSoftware::getComputerId, computers.stream().map(Computer::getId).collect(Collectors.toList()));
        List<ComputerSoftware> computerSoftwareList = sqlExecutor.queryList(computerSoftwareSql);

        Map<Integer, List<Software>> computerSoftwareMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(computerSoftwareList)) {
            List<Integer> softIds = computerSoftwareList.stream().map(ComputerSoftware::getSoftwareId).collect(Collectors.toList());
            List<Software> softwareList = sqlExecutor.queryList(softIds, Software.class);
            Map<Integer, Software> softwareMap = softwareList.stream().collect(Collectors.toMap(Software::getId, Function.identity()));
            computerSoftwareList.stream().collect(Collectors.groupingBy(ComputerSoftware::getComputerId)).entrySet().forEach(e -> {
                computerSoftwareMap.put(e.getKey(), e.getValue().stream().map(cs -> softwareMap.get(cs.getSoftwareId())).collect(Collectors.toList()));
            });
        }

        for (Computer computer : computers) {
            computer.setMainframe(mainframeMap.get(computer.getMainframeId()));
            computer.setMonitor(monitorMap.get(computer.getMonitorId()));
            computer.setMouse(mouseMap.get(computer.getMouseId()));
            computer.setKeyboard(keyboardMap.get(computer.getKeyboardId()));
            computer.setSoftware(computerSoftwareMap.get(computer.getId()));
        }

        return computers;
    }

    @Override
    public Monitor getMonitorById(int id) {

        return sqlExecutor.queryById(id, Monitor.class);
    }

    @Override
    public List<Monitor> listMonitor() {

        return sqlExecutor.queryList(SQL.query(Monitor.class));
    }

}
