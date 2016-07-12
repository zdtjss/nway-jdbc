package com.nway.spring.jdbc.json;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * 
 * @author zdtjss@163.com
 * 
 * @since 2015-04-03
 *
 */
public abstract class JsonBuilder
{
	protected static final String JSON_NULL = "null";

	protected JsonBuilder(){
	}
	
	protected abstract String buildJson(ResultSet rs) throws SQLException;

	protected void integerValue(int value, boolean wasNull, StringBuilder json)
	{
		if (wasNull)
		{
			json.append(JSON_NULL);
		}
		else
		{
			json.append(value);
		}
	}

	protected void longValue(long value, boolean wasNull, StringBuilder json)
	{
		if (wasNull)
		{
			json.append(JSON_NULL);
		}
		else
		{
			json.append(value);
		}
	}

	protected void floatValue(float value, boolean wasNull, StringBuilder json)
	{
		if (wasNull)
		{
			json.append(JSON_NULL);
		}
		else
		{
			json.append(value);
		}
	}

	protected void doubleValue(double value, boolean wasNull, StringBuilder json)
	{
		if (wasNull)
		{
			json.append(JSON_NULL);
		}
		else
		{
			json.append(value);
		}
	}

	protected void stringValue(String value, StringBuilder json)
	{
		if (value == null)
		{
			json.append(JSON_NULL);
		}
		else
		{
			json.append('\"').append(value).append('\"');
		}
	}

	protected void dateValue(Date value, String pattern, StringBuilder json)
	{
		if (value == null)
		{
			json.append(JSON_NULL);
		}
		else
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

			json.append('\"').append(dateFormat.format(value)).append('\"');
		}
	}

	protected void booleanValue(boolean value, boolean wasNull, StringBuilder json)
	{
		if (wasNull)
		{
			json.append(JSON_NULL);
		}
		else
		{
			json.append(value);
		}
	}
	
}
