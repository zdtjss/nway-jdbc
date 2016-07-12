/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nway.spring.jdbc.json;

import java.sql.ResultSet;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 *
 * @author zdtjss@163.com
 * @param <T>
 *            List元素类型
 *
 * @since 2014-03-28
 */
public final class JsonListHandlerNRC implements ResultSetExtractor<String>
{
	private static final JsonProcessor JSON_PROCESSOR = new JsonProcessor();

	private String cacheKey;
	
	public JsonListHandlerNRC(String cacheKey) {
		
		this.cacheKey = cacheKey;
	}

	@Override
	public String extractData(ResultSet rs) throws DataAccessException
	{
		try
		{
			return rs.next() ? JSON_PROCESSOR.toJsonList(rs, cacheKey) : "[]";
		}
		catch (Exception e)
		{
			throw new JsonBuildException("创建JSON失败 ", e);
		}
	}

}
