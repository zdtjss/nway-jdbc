package com.nway.spring.jdbc.sql;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nway.spring.jdbc.annotation.Column;

class ReflectUtils {

	/**
     * ×Ö¶ÎÓ³Éä
     */
    private static final Map<Class<?>, Map<String, String>> FIELD_COLUMN_MAP = new ConcurrentHashMap<>();

    public static Map<Class<?>, Map<String, String>> getColumnFieldMap(Class<?> classz) {
    	if(FIELD_COLUMN_MAP.containsKey(classz)) {
    		return FIELD_COLUMN_MAP;
    	}
    	Map<String, String> columnFieldMap = new HashMap<String, String>();
    	for(Field field : classz.getDeclaredFields()) {
			Column column = field.getAnnotation(Column.class);
			if(column != null) {
				columnFieldMap.put(field.getName(), column.name());
			}
			else {
				columnFieldMap.put(field.getName(), fieldToColumn(field.getName()));
			}
    	}
    	FIELD_COLUMN_MAP.put(classz, columnFieldMap);
    	return FIELD_COLUMN_MAP;
    }
	
	public static <T> String getColumn(SSupplier<T> lambda) {
		try {
			SerializedLambda serializedLambda = getSerializedLambda(lambda);
			return fieldToColumn(serializedLambda.getImplMethodName());
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
	
	public static String fieldToColumn(String fieldName) {
		StringBuilder column = new StringBuilder();
		for(char c :fieldName.toCharArray()) {
			if(Character.isUpperCase(c)) {
				column.append('_').append(Character.toLowerCase(c));
			}
			else {
				column.append(c);
			}
		}
		return column.toString();
	}
}
