package com.nway.spring.jdbc.json;

import java.sql.ResultSet;

import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * 
 * @author zdtjss@163.com
 * @since 2015/04/02
 */
public class JsonHandler implements ResultSetExtractor<String>
{
	private static final JsonProcessor JSON_PROCESSOR = new JsonProcessor();

	private final Class<?> type;
	private String cacheKey;
	
	public JsonHandler(Class<?> type, String cacheKey) {
		this.type = type;
		this.cacheKey = cacheKey;
	}

	@Override
	public String extractData(ResultSet rs) {
		
		try {

			return rs.next() ? JSON_PROCESSOR.buildJson(rs, type, cacheKey) : "{}";
		} 
		catch (Exception e) {
			
			throw new JsonBuildException("´´½¨JSONÊ§°Ü [ " + this.type + " ]", e);
		}
	}
}
