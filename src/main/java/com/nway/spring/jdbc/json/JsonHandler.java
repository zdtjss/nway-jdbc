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
	
	private String querying;

	public JsonHandler(Class<?> type, String querying) {
		this.type = type;
		this.querying = querying;
	}

	@Override
	public String extractData(ResultSet rs) {
		
		try {

			return rs.next() ? JSON_PROCESSOR.buildJson(rs, type, querying, true) : "{}";
		} 
		catch (Exception e) {
			
			throw new JsonBuildException("´´½¨JSONÊ§°Ü [ " + this.type + " ]", e);
		}
	}
}
