package com.nway.spring.jdbc.performance.mapper;

import java.util.Map;

public interface GenericMapper {

	Object list(Map<String, String> param);
	
	Object getById(long id);
	
	int update(Map<String, String> param);
	
	int removeById(String ids);
}
