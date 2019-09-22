package com.nway.spring.jdbc.sql;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nway.spring.jdbc.annotation.Column;

public class SqlBuilderUtils {

	/**
     * 字段映射
     */
    private static final Map<Class<?>, Map<String, String>> FIELD_COLUMN_MAP = new ConcurrentHashMap<>();

    public static Map<String, String> getColumnFieldMap(Class<?> classz) {
    	if(FIELD_COLUMN_MAP.containsKey(classz)) {
    		return FIELD_COLUMN_MAP.get(classz);
    	}
    	Map<String, String> columnFieldMap = new HashMap<String, String>();
    	for(Field field : classz.getDeclaredFields()) {
			Column column = field.getAnnotation(Column.class);
			if(column != null) {
				columnFieldMap.put(field.getName(), column.name());
			}
			else {
				columnFieldMap.put(field.getName(), fieldToColumn(field));
			}
    	}
    	FIELD_COLUMN_MAP.put(classz, columnFieldMap);
    	return columnFieldMap;
    }
	
	public static <T> String getColumn(Class<?> beanClass, SSupplier<T> lambda) {
		try {
			SerializedLambda serializedLambda = getSerializedLambda(lambda);
			return methodToColumn(beanClass, serializedLambda.getImplMethodName());
		} catch (Exception e) {
			return null;
		}
	}
    
	public static <T> SerializedLambda getSerializedLambda(SSupplier<T> lambda) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
		writeReplace.setAccessible(true);

		return (SerializedLambda) writeReplace.invoke(lambda);
	}
	
	public static String fieldToColumn(Field field) {
		Column column = field.getAnnotation(Column.class);
		if(column != null) {
			return column.name();
		}
		else {
			return fieldNameToColumn(field.getName());
		}
	}
	
	public static String fieldNameToColumn(String fieldName) {
		StringBuilder columnName = new StringBuilder();
		for(char c : fieldName.toCharArray()) {
			if(Character.isUpperCase(c)) {
				columnName.append('_').append(Character.toLowerCase(c));
			}
			else {
				columnName.append(c);
			}
		}
		return columnName.toString();
	}
	
	public static String methodToColumn(Class<?> beanClass, String methodName) {
		String fieldName = "";
		if (methodName.startsWith("is")) {
			fieldName = methodName.substring(2);
		}
		else if (methodName.startsWith("get") || methodName.startsWith("set")) {
			fieldName = methodName.substring(3);
		}
		else {
			throw new RuntimeException(
					"Error parsing property name '" + methodName + "'.  Didn't start with 'is', 'get' or 'set'.");
		}
		if (fieldName.length() == 1 || (fieldName.length() > 1 && !Character.isUpperCase(fieldName.charAt(1)))) {
			fieldName = fieldName.substring(0, 1).toLowerCase(Locale.ENGLISH) + fieldName.substring(1);
		}
		return getColumnFieldMap(beanClass).get(fieldName);
	}
}