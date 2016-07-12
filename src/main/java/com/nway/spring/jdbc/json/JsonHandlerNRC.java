package com.nway.spring.jdbc.json;

import java.sql.ResultSet;

import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * 
 * @author zdtjss@163.com
 * @since 2016/07/12
 */
public class JsonHandlerNRC implements ResultSetExtractor<String>
{
	private static final JsonProcessor JSON_PROCESSOR = new JsonProcessor();

	private String cacheKey;
	
	public JsonHandlerNRC(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	@Override
	public String extractData(ResultSet rs) {
		
		try {

			return rs.next() ? JSON_PROCESSOR.buildJson(rs, cacheKey) : "{}";
		} 
		catch (Exception e) {
			
			throw new JsonBuildException("´´½¨JSONÊ§°Ü ", e);
		}
	}
}
