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
package com.nway.spring.jdbc.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库表数据的动态bean创建
 *
 * @author zdtjss@163.com
 *
 * @since 2014-1-13
 */
public abstract class DbBeanFactory {
	
	protected abstract <T> T createBean(ResultSet rs, Class<T> type) throws SQLException;
	
	protected Integer integerValue(int value, boolean wasNull) throws SQLException {
		
		return wasNull ? null : value;
	}
	
	protected Long longValue(long value, boolean wasNull) throws SQLException {
		
		return wasNull ? null : value;
	}
	
	protected Float floatValue(float value, boolean wasNull) throws SQLException {
		
		return wasNull ? null : value;
	}
	
	protected Double doubleValue(double value, boolean wasNull) throws SQLException {
		
		return wasNull ? null : value;
	}
	
	protected Boolean booleanValue(boolean value, boolean wasNull) throws SQLException {
		
		return wasNull ? null : value;
	}
	
	protected Byte byteValue(byte value, boolean wasNull) throws SQLException {
		
		return wasNull ? null : value;
	}
	
	protected Short shortValue(short value, boolean wasNull) throws SQLException {
		
		return wasNull ? null : value;
	}
	
}
