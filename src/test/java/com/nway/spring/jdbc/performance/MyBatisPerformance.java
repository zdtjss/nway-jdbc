package com.nway.spring.jdbc.performance;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;

@Service("myBatisPerformance")
@Transactional(transactionManager = "jdbcTxManager", readOnly = true)
public class MyBatisPerformance implements Performance {

	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public Computer getComputerById(int id) {
		
		return sqlSession.selectOne("selectComputer", id);
	}

	@Override
	public List<Computer> listComputer() {
		
		return sqlSession.selectList("listComputer");
	}

	@Override
	public Monitor getMonitorById(int id) {
		sqlSession.getConfiguration().getDatabaseId();
		return sqlSession.selectOne("selectMonitor", id);
	}

	@Override
	public List<Monitor> listMonitor() {
		
		return sqlSession.selectList("listMonitor");
	}
	
	public int queryAllNum(String[] itemTypes) {
	    
	    return sqlSession.selectOne("queryAllNum", itemTypes);
	}

}
