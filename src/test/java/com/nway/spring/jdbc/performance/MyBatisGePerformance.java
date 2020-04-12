package com.nway.spring.jdbc.performance;

import com.nway.spring.jdbc.performance.dal.mapper.*;
import com.nway.spring.jdbc.performance.dal.po.*;
import com.nway.spring.jdbc.performance.entity.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("myBatisGePerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class MyBatisGePerformance {

    @Autowired
    @Qualifier("ComputerMapperGe")
    private ComputerMapper computerMapper;
    @Autowired
    @Qualifier("ComputerSoftwareMapperGe")
    private ComputerSoftwareMapper computerSoftwareMapper;
    @Autowired
    @Qualifier("KeyboardMapperGe")
    private KeyboardMapper keyboardMapper;
    @Autowired
    @Qualifier("MainframeMapperGe")
    private MainframeMapper mainframeMapper;
    @Autowired
    @Qualifier("MonitorMapperGe")
    private MonitorMapper monitorMapper;
    @Autowired
    @Qualifier("MouseMapperGe")
    private MouseMapper mouseMapper;
    @Autowired
    @Qualifier("SoftwareMapperGe")
    private SoftwareMapper softwareMapper;

    public ComputerPoEntity getComputerById(int id) {
        ComputerPoEntity computerPoEntity = computerMapper.selectByPrimaryKey(id);
        MainframePoEntity mainframePoEntity = mainframeMapper.selectByPrimaryKey(computerPoEntity.getMainframeId());
        MonitorPoEntity monitorPoEntity = monitorMapper.selectByPrimaryKey(computerPoEntity.getMonitorId());
        MousePoEntity mousePoEntity = mouseMapper.selectByPrimaryKey(computerPoEntity.getMouseId());
        KeyboardPoEntity keyboardPoEntity = keyboardMapper.selectByPrimaryKey(computerPoEntity.getKeyboardId());

        ComputerSoftwarePoQuery computerSoftwarePoQuery = new ComputerSoftwarePoQuery();
        List<ComputerSoftwarePoEntity> computerSoftwarePoEntityList = computerSoftwareMapper.selectByExample(computerSoftwarePoQuery);

        Map<Integer, List<SoftwarePoEntity>> computerSoftwareMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(computerSoftwarePoEntityList)) {
            List<Integer> softwareIdList = computerSoftwarePoEntityList.stream().map(e -> e.getSoftwareId()).collect(Collectors.toList());
            SoftwarePoQuery softwarePoQuery = new SoftwarePoQuery();
            softwarePoQuery.createCriteria().andIdIn(softwareIdList);
            List<SoftwarePoEntity> softwarePoEntityList = softwareMapper.selectByExample(softwarePoQuery);
            Map<Integer, SoftwarePoEntity> softwareMap = softwarePoEntityList.stream().collect(Collectors.toMap(SoftwarePoEntity::getId, Function.identity()));
            computerSoftwarePoEntityList.stream().collect(Collectors.groupingBy(ComputerSoftwarePoEntity::getComputerId)).entrySet().stream().forEach(e -> {
                computerSoftwareMap.put(e.getKey(), e.getValue().stream().map(s -> softwareMap.get(s.getSoftwareId())).collect(Collectors.toList()));
            });
        }

        computerPoEntity.setMainframe(mainframePoEntity);
        computerPoEntity.setMonitor(monitorPoEntity);
        computerPoEntity.setMouse(mousePoEntity);
        computerPoEntity.setKeyboard(keyboardPoEntity);
        computerPoEntity.setSoftware(computerSoftwareMap.get(computerPoEntity.getId()));

        return computerPoEntity;
    }

    public List<ComputerPoEntity> listComputer() {

        List<ComputerPoEntity> computerPoEntityList = computerMapper.selectByExample(new ComputerPoQuery());

        List<Integer> mainframeIds = computerPoEntityList.stream().map(e -> e.getMainframeId()).collect(Collectors.toList());
        List<Integer> monitorIds = computerPoEntityList.stream().map(e -> e.getMonitorId()).collect(Collectors.toList());
        List<Integer> mouseIds = computerPoEntityList.stream().map(e -> e.getMouseId()).collect(Collectors.toList());
        List<Integer> keyboardIds = computerPoEntityList.stream().map(e -> e.getKeyboardId()).collect(Collectors.toList());

        MainframePoQuery mainframePoQuery = new MainframePoQuery();
        mainframePoQuery.createCriteria().andIdIn(mainframeIds);

        MonitorPoQuery monitorPoQuery = new MonitorPoQuery();
        monitorPoQuery.createCriteria().andIdIn(monitorIds);

        MousePoQuery mousePoQuery = new MousePoQuery();
        mousePoQuery.createCriteria().andIdIn(mouseIds);

        KeyboardPoQuery keyboardPoQuery = new KeyboardPoQuery();
        keyboardPoQuery.createCriteria().andIdIn(keyboardIds);

        List<MainframePoEntity> mainframePoEntityList = mainframeMapper.selectByExample(mainframePoQuery);
        List<MonitorPoEntity> monitorPoEntityList = monitorMapper.selectByExample(monitorPoQuery);
        List<MousePoEntity> mousePoEntityList = mouseMapper.selectByExample(mousePoQuery);
        List<KeyboardPoEntity> keyboardPoEntityList = keyboardMapper.selectByExample(keyboardPoQuery);

        Map<Integer, MainframePoEntity> mainframeMap = mainframePoEntityList.stream().collect(Collectors.toMap(MainframePoEntity::getId, Function.identity()));

        Map<Integer, MonitorPoEntity> monitorMap = monitorPoEntityList.stream().collect(Collectors.toMap(MonitorPoEntity::getId, Function.identity()));

        Map<Integer, MousePoEntity> mouseMap = mousePoEntityList.stream().collect(Collectors.toMap(MousePoEntity::getId, Function.identity()));

        Map<Integer, KeyboardPoEntity> keyboardMap = keyboardPoEntityList.stream().collect(Collectors.toMap(KeyboardPoEntity::getId, Function.identity()));

        ComputerSoftwarePoQuery computerSoftwarePoQuery = new ComputerSoftwarePoQuery();
        List<ComputerSoftwarePoEntity> computerSoftwarePoEntityList = computerSoftwareMapper.selectByExample(computerSoftwarePoQuery);

        Map<Integer, List<SoftwarePoEntity>> computerSoftwareMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(computerSoftwarePoEntityList)) {
            List<Integer> softwareIdList = computerSoftwarePoEntityList.stream().map(e -> e.getSoftwareId()).collect(Collectors.toList());
            SoftwarePoQuery softwarePoQuery = new SoftwarePoQuery();
            softwarePoQuery.createCriteria().andIdIn(softwareIdList);
            List<SoftwarePoEntity> softwarePoEntityList = softwareMapper.selectByExample(softwarePoQuery);
            Map<Integer, SoftwarePoEntity> softwareMap = softwarePoEntityList.stream().collect(Collectors.toMap(SoftwarePoEntity::getId, Function.identity()));
            computerSoftwarePoEntityList.stream().collect(Collectors.groupingBy(ComputerSoftwarePoEntity::getComputerId)).entrySet().stream().forEach(e -> {
                computerSoftwareMap.put(e.getKey(), e.getValue().stream().map(s -> softwareMap.get(s.getSoftwareId())).collect(Collectors.toList()));
            });
        }

        for (ComputerPoEntity computer : computerPoEntityList) {
            computer.setMainframe(mainframeMap.get(computer.getMainframeId()));
            computer.setMonitor(monitorMap.get(computer.getMonitorId()));
            computer.setMouse(mouseMap.get(computer.getMouseId()));
            computer.setKeyboard(keyboardMap.get(computer.getKeyboardId()));
            computer.setSoftware(computerSoftwareMap.get(computer.getId()));
        }

        return computerPoEntityList;
    }

    public MonitorPoEntity getMonitorById(int id) {

        return monitorMapper.selectByPrimaryKey(id);
    }

    public List<MonitorPoEntity> listMonitor() {

        MonitorPoQuery query = new MonitorPoQuery();
        return monitorMapper.selectByExample(query);
    }

}
