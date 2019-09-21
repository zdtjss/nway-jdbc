package com.nway.spring.jdbc.json;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.nway.spring.jdbc.annotation.Column;

/**
 * 
 * @author zdtjss@163.com
 * @since 2014-03-28
 */
class JsonProcessor {
	
	private static final int PROPERTY_NOT_FOUND = -1;
	private static final String JSON_NULL = "null";

	public String buildJson(ResultSet rs, Class<?> type) throws SQLException, IntrospectionException {

		return buildJsonByJdk(rs, type);
	}

	public String toJsonList(ResultSet rs, Class<?> type) throws SQLException, IntrospectionException {

		StringBuilder json = new StringBuilder(5000);

		json.append("[");

		do {
			json.append(buildJson(rs, type)).append(',');
		}
		while (rs.next());

		if (json.length() > 1) {
			
			json = json.deleteCharAt(json.length() - 1);
		}

		return json.append(']').toString();
	}

	private String buildJsonByJdk(ResultSet rs, Class<?> type) throws SQLException, IntrospectionException {
		
		ResultSetMetaData rsmd = rs.getMetaData();

		PropertyDescriptor[] props = Introspector.getBeanInfo(type).getPropertyDescriptors();

		int[] columnToProperty = this.mapColumnsToProperties(rsmd, props);

		StringBuilder json = new StringBuilder(512);
		json.append("{");

		for (int index = 1; index < columnToProperty.length; index++) {
			if (columnToProperty[index] == PROPERTY_NOT_FOUND) {
				continue;
			}

			PropertyDescriptor prop = props[columnToProperty[index]];
			Class<?> propType = prop.getPropertyType();

			json.append('\"').append(prop.getName()).append("\":");

			if (String.class.equals(propType) || Clob.class.equals(propType)) {
				stringValue(rs.getString(index), json);
			}
			else if (boolean.class.equals(propType) || Boolean.class.equals(propType)) {
				booleanValue(rs.getBoolean(index), rs.wasNull(), json);
			}
			else if (int.class.equals(propType) || Integer.class.equals(propType)) {
				integerValue(rs.getInt(index), rs.wasNull(), json);
			}
			else if (long.class.equals(propType) || Long.class.equals(propType)) {
				longValue(rs.getLong(index), rs.wasNull(), json);
			}
			else if (float.class.equals(propType) || Float.class.equals(propType)) {
				floatValue(rs.getFloat(index), rs.wasNull(), json);
			}
			else if (double.class.equals(propType) || Double.class.equals(propType)) {
				doubleValue(rs.getDouble(index), rs.wasNull(), json);
			}
			else if (java.util.Date.class.equals(propType)) {
				dateValue(rs.getDate(index), "yyyy-MM-dd HH:mm:ss", json);
			}
			else if (java.sql.Timestamp.class.equals(propType)) {
				dateValue(rs.getTimestamp(index), "yyyy-MM-dd HH:mm:ss.SSS", json);
			}
			else if (java.sql.Date.class.equals(propType)) {
				dateValue(rs.getDate(index), "yyyy-MM-dd", json);
			}
			else if (java.sql.Time.class.equals(propType)) {
				dateValue(rs.getTime(index), "HH:mm:ss", json);
			}
			else {
				json.append('\"').append(rs.getObject(index)).append('\"');
			}

			json.append(',');
		}

		if (json.length() > 1) {
			json = json.deleteCharAt(json.length() - 1);
		}

		return json.append('}').toString();
	}

	/**
	 * The positions in the returned array represent column numbers. The values
	 * stored at each position represent the index in the
	 * <code>PropertyDescriptor[]</code> for the bean property that matches the
	 * column name. If no bean property was found for a column, the position is set
	 * to <code>PROPERTY_NOT_FOUND</code>.
	 *
	 * @param rsmd  The <code>ResultSetMetaData</code> containing column
	 *              information.
	 *
	 * @param props The bean property descriptors.
	 *
	 * @throws SQLException if a database access error occurs
	 *
	 * @return An int[] with column index to property index mappings. The 0th
	 *         element is meaningless because JDBC column indexing starts at 1.
	 */
	private int[] mapColumnsToProperties(ResultSetMetaData rsmd, PropertyDescriptor[] props) throws SQLException {

		int cols = rsmd.getColumnCount();
		int[] columnToProperty = new int[cols + 1];

		Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

		for (int col = 1; col <= cols; col++) {
			String columnName = rsmd.getColumnLabel(col);

			for (int i = 0; i < props.length; i++) {
				Column columnAnnotation = props[i].getReadMethod().getAnnotation(Column.class);

				if (columnAnnotation == null) {
					// 去除列名里的下划线'_'
					if (columnName.replace("_", "").equalsIgnoreCase(props[i].getName())) {
						columnToProperty[col] = i;
						break;
					}
				}
				else if (columnName.equalsIgnoreCase(columnAnnotation.value())
						|| columnName.equalsIgnoreCase(columnAnnotation.name())) {
					columnToProperty[col] = i;
					break;
				}
			}
		}

		return columnToProperty;
	}
	
	private void integerValue(int value, boolean wasNull, StringBuilder json) {
		if (wasNull) {
			json.append(JSON_NULL);
		}
		else {
			json.append(value);
		}
	}

	private void longValue(long value, boolean wasNull, StringBuilder json) {
		if (wasNull) {
			json.append(JSON_NULL);
		} else {
			json.append(value);
		}
	}

	private void floatValue(float value, boolean wasNull, StringBuilder json) {
		if (wasNull) {
			json.append(JSON_NULL);
		}
		else {
			json.append(value);
		}
	}

	private void doubleValue(double value, boolean wasNull, StringBuilder json) {
		if (wasNull) {
			json.append(JSON_NULL);
		}
		else {
			json.append(value);
		}
	}

	private void stringValue(String value, StringBuilder json) {
		if (value == null) {
			json.append(JSON_NULL);
		}
		else {
			json.append('\"').append(value).append('\"');
		}
	}

	private void dateValue(Date value, String pattern, StringBuilder json) {
		if (value == null) {
			json.append(JSON_NULL);
		}
		else {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

			json.append('\"').append(dateFormat.format(value)).append('\"');
		}
	}

	private void booleanValue(boolean value, boolean wasNull, StringBuilder json) {
		if (wasNull) {
			json.append(JSON_NULL);
		}
		else {
			json.append(value);
		}
	}
}
