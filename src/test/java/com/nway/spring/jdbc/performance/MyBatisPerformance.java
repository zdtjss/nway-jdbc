package com.nway.spring.jdbc.performance;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nway.spring.jdbc.performance.entity.Computer;
import com.nway.spring.jdbc.performance.entity.Monitor;

@Service("myBatisPerformance")
public class MyBatisPerformance implements Performance {

	@Autowired
	private SqlSessionTemplate sqlSession;
	
	@Override
	public Computer getComputer(int id) {
		
		return sqlSession.selectOne("selectComputer", id);
	}

	@Override
	public List<Computer> listComputer() {
		
		return sqlSession.selectList("listComputer");
	}

	@Override
	public Monitor getMonitor(int id) {
		sqlSession.getConfiguration().getDatabaseId();
		return sqlSession.selectOne("selectMonitor", id);
	}

	@Override
	public List<Monitor> listMonitor(int num) {
		
		return sqlSession.selectList("listMonitor", num);
	}

}
