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
import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.annotation.enums.ColumnType;
import com.nway.spring.jdbc.sql.builder.SqlBuilderException;
import com.nway.spring.jdbc.sql.fill.FillStrategy;
import com.nway.spring.jdbc.sql.fill.NoneFillStrategy;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.function.SSupplier;
import com.nway.spring.jdbc.sql.permission.NonePermissionStrategy;
import com.nway.spring.jdbc.sql.permission.PermissionStrategy;

public class SqlBuilderUtils {

	/**
     * 字段映射
     */
    private static final Map<Class<?>, Map<String, String>> FIELD_COLUMN_MAP = new ConcurrentHashMap<>();
    
    private static final Map<Class<?>, FillStrategy> FILL_STRATEGY = new ConcurrentHashMap<>();
    private static final Map<Class<?>, PermissionStrategy> PERMISSION_STRATEGY = new ConcurrentHashMap<>();

    public static Map<String, String> getColumnFieldMap(Class<?> classz) {
    	if(FIELD_COLUMN_MAP.containsKey(classz)) {
    		return FIELD_COLUMN_MAP.get(classz);
    	}
    	Map<String, String> columnFieldMap = new HashMap<String, String>();
    	for(Field field : classz.getDeclaredFields()) {
			Column column = field.getAnnotation(Column.class);
			if(column != null) {
				columnFieldMap.put(field.getName(), column.value().length() > 0 ? column.value() : column.name());
			}
			else {
				columnFieldMap.put(field.getName(), getColumnName(field));
			}
    	}
    	FIELD_COLUMN_MAP.put(classz, columnFieldMap);
    	return columnFieldMap;
    }
	
	public static <T> String getColumn(Class<?> beanClass, SSupplier<T> lambda) {
		try {
			SerializedLambda serializedLambda = getSerializedLambda(lambda);
			return methodToColumn(beanClass, serializedLambda.getImplMethodName());
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new SqlBuilderException(e);
		}
	}
	
	public static <T, R> String getColumn(Class<?> beanClass, SFunction<T, R> lambda) {
		try {
			SerializedLambda serializedLambda = getSerializedLambda(lambda);
			return methodToColumn(beanClass, serializedLambda.getImplMethodName());
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new SqlBuilderException(e);
		}
	}
    
	public static <T> SerializedLambda getSerializedLambda(SSupplier<T> lambda) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
		writeReplace.setAccessible(true);

		return (SerializedLambda) writeReplace.invoke(lambda);
	}
	
	public static <T, R> SerializedLambda getSerializedLambda(SFunction<T, R> lambda) throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
		writeReplace.setAccessible(true);
		
		return (SerializedLambda) writeReplace.invoke(lambda);
	}
	
	public static String getColumnName(Field field) {
		Column column = field.getAnnotation(Column.class);
		if(column != null && (column.name().length() > 0 || column.value().length() > 0)) {
			return column.value().length() > 0 ? column.value() : column.name();
		}
		else {
			return fieldNameToColumn(field.getName());
		}
	}
	
	public static Object getColumnValue(Class<? extends FillStrategy> fillStrategyClass, SqlType sqlType) throws InstantiationException, IllegalAccessException {
		FillStrategy fillStrategy = FILL_STRATEGY.get(fillStrategyClass);
		if (fillStrategy == null) {
			fillStrategy = fillStrategyClass.newInstance();
			FILL_STRATEGY.put(fillStrategyClass, fillStrategy);
		}
		return fillStrategy.getValue(sqlType);
	}
	
	public static Object getColumnValue(Field field, Object obj, SqlType sqlType) throws InstantiationException, IllegalAccessException {
		Column column = field.getAnnotation(Column.class);
		if(column == null || NoneFillStrategy.class.equals(column.fillStrategy())) {
			field.setAccessible(true);
			return field.get(obj);
		}
		return getColumnValue(column.fillStrategy(), sqlType);
	}
	
	public static String getWhereCondition(Field field) {
		Column column = field.getAnnotation(Column.class);
		if(column == null ||NonePermissionStrategy.class.equals(column.permissionStrategy())) {
			return "";
		}
		Class<? extends PermissionStrategy> permissionStrategyClass = column.permissionStrategy();
		PermissionStrategy permissionStrategy = PERMISSION_STRATEGY.get(permissionStrategyClass);
		if(permissionStrategy == null) {
			try {
				permissionStrategy = permissionStrategyClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new SqlBuilderException(e);
			}
			PERMISSION_STRATEGY.put(permissionStrategyClass, permissionStrategy);
		}
		return permissionStrategy.getSqlSegment(getColumnName(field));
	}
	
	public static String getTableName(Table table) {
		return table.value().length() > 0 ? table.value() : table.name();
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
	
	public static String getIdName(Class<?> beanClass) {
		String id = "";
		for (Field field : beanClass.getDeclaredFields()) {
			Column column = field.getAnnotation(Column.class);
			if(column != null && column.type().equals(ColumnType.ID)) {
				id = getColumnName(field);
				break;
			}
		}
		return id;
	}
	
	public static Object getIdValue(Class<?> beanClass, Object obj) {
		Object value = null;
		try {
			for (Field field : beanClass.getDeclaredFields()) {
				Column column = field.getAnnotation(Column.class);
				if (column != null && column.type().equals(ColumnType.ID)) {
					value = getColumnValue(field, obj, SqlType.UPDATE);
					break;
				}
			}
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
		return value;
	}
	
}
