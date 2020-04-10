package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.annotation.enums.ColumnType;
import com.nway.spring.jdbc.sql.builder.SqlBuilderException;
import com.nway.spring.jdbc.sql.fill.FillStrategy;
import com.nway.spring.jdbc.sql.fill.NoneFillStrategy;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.function.SSupplier;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;
import com.nway.spring.jdbc.sql.permission.NonePermissionStrategy;
import com.nway.spring.jdbc.sql.permission.PermissionStrategy;
import com.nway.spring.jdbc.sql.permission.WhereCondition;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlBuilderUtils {

	/**
     * 字段映射
     */
    private static final Map<Class<?>, Map<String, String>> FIELD_COLUMN_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, EntityInfo> ENTITY_INFO_MAP = new ConcurrentHashMap<>();

    private static final Map<Class<?>, FillStrategy> FILL_STRATEGY = new ConcurrentHashMap<>();
    private static final Map<Class<?>, PermissionStrategy> PERMISSION_STRATEGY = new ConcurrentHashMap<>();
	private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
	private static void initEntityInfo(Class<?> claszz) {
		if (ENTITY_INFO_MAP.containsKey(claszz)) {
			return;
		}
		try {
			Field[] declaredFields = claszz.getDeclaredFields();
			EntityInfo entityInfo = new EntityInfo();
			entityInfo.setTableName(getTableName(claszz));
			entityInfo.setColumnList(new HashMap<>(declaredFields.length));
			for (Field field : declaredFields) {
				PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), claszz);
				MethodHandle methodHandle = lookup.findVirtual(claszz, descriptor.getReadMethod().getName(), MethodType.methodType(field.getType()));
				Column column = field.getAnnotation(Column.class);
				ColumnInfo columnInfo = new ColumnInfo();
				columnInfo.setColumnName(getColumnName(field));
				columnInfo.setMethodHandle(methodHandle);
				if (column != null) {
					columnInfo.setFillStrategy(column.fillStrategy().getConstructor().newInstance());
					columnInfo.setPermissionStrategy(column.permissionStrategy().getConstructor().newInstance());
					if (ColumnType.ID.equals(column.type())) {
						entityInfo.setId(columnInfo);
					}
				}
				else {
					columnInfo.setFillStrategy(new NoneFillStrategy());
					columnInfo.setPermissionStrategy(new NonePermissionStrategy());
				}
				entityInfo.getColumnList().put(field.getName(), columnInfo);
			}
			ENTITY_INFO_MAP.put(claszz, entityInfo);
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
	}

	public static EntityInfo getEntityInfo(Class<?> claszz) {
		if (!ENTITY_INFO_MAP.containsKey(claszz)) {
			initEntityInfo(claszz);
		}
		return ENTITY_INFO_MAP.get(claszz);
	}

	public static <T> String getColumn(Class<?> beanClass, SSupplier<T> lambda) {
		try {
			SerializedLambda serializedLambda = getSerializedLambda(lambda);
			return methodToColumn(beanClass, serializedLambda.getImplMethodName());
		} catch (Throwable e) {
			throw new SqlBuilderException(e);
		}
	}
	
	public static <T, R> String getColumn(Class<?> beanClass, SFunction<T, R> lambda) {
		try {
			SerializedLambda serializedLambda = getSerializedLambda(lambda);
			return methodToColumn(beanClass, serializedLambda.getImplMethodName());
		} catch (Throwable e) {
			throw new SqlBuilderException(e);
		}
	}
    
	public static <T> SerializedLambda getSerializedLambda(SSupplier<T> lambda) throws Throwable {

		Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
		writeReplace.setAccessible(true);

		return (SerializedLambda) MethodHandles.lookup().unreflect(writeReplace).invoke(lambda);
	}

	public static <T, R> SerializedLambda getSerializedLambda(SFunction<T, R> lambda) throws Throwable {

		Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
		writeReplace.setAccessible(true);

		return (SerializedLambda) MethodHandles.lookup().unreflect(writeReplace).invoke(lambda);
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
	
	public static Object getColumnValue(Class<? extends FillStrategy> fillStrategyClass, SqlType sqlType) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		FillStrategy fillStrategy = FILL_STRATEGY.get(fillStrategyClass);
		if (fillStrategy == null) {
			fillStrategy = fillStrategyClass.getDeclaredConstructor().newInstance();
			FILL_STRATEGY.put(fillStrategyClass, fillStrategy);
		}
		return fillStrategy.getValue(sqlType);
	}
	
	public static Object getColumnValue(ColumnInfo columnInfo, Object obj, SqlType sqlType) throws Throwable {
		if(NoneFillStrategy.class.equals(columnInfo.getFillStrategy().getClass())) {
			return columnInfo.getMethodHandle().invoke(obj);
		}
		return columnInfo.getFillStrategy().getValue(sqlType);
	}
	
	public static WhereCondition getWhereCondition(ColumnInfo columnInfo) {
		if (NonePermissionStrategy.class.equals(columnInfo.getPermissionStrategy().getClass())) {
			return null;
		}
		return columnInfo.getPermissionStrategy().getSqlSegment(columnInfo.getColumnName());
	}
	
	public static String getTableName(Class<?> entityClass) {
		Table table = entityClass.getAnnotation(Table.class);
		if(table != null) {
			return table.value().length() > 0 ? table.value() : table.name();
		}
		return fieldNameToColumn(entityClass.getSimpleName());
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
		EntityInfo entityInfo = ENTITY_INFO_MAP.get(beanClass);
		if(entityInfo == null) {
			initEntityInfo(beanClass);
		}
		return ENTITY_INFO_MAP.get(beanClass).getColumnList().get(fieldName).getColumnName();
	}

	public static String getIdName(Class<?> beanClass) {
		EntityInfo entityInfo = ENTITY_INFO_MAP.get(beanClass);
		if (entityInfo == null) {
			initEntityInfo(beanClass);
		}
		return ENTITY_INFO_MAP.get(beanClass).getId().getColumnName();
	}
	
	public static Object getIdValue(Class<?> beanClass) {
		Object value = null;
		try {
			EntityInfo entityInfo = ENTITY_INFO_MAP.get(beanClass);
			if (entityInfo == null) {
				initEntityInfo(beanClass);
			}
			ColumnInfo columnInfo = ENTITY_INFO_MAP.get(beanClass).getId();
			if(!NoneFillStrategy.class.equals(columnInfo.getFillStrategy())) {
				return columnInfo.getFillStrategy().getValue(SqlType.UPDATE);
			}
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
		return value;
	}

	public static Object getIdValue(Class<?> beanClass, Object obj) {
		Object value = null;
		try {
			EntityInfo entityInfo = ENTITY_INFO_MAP.get(beanClass);
			if (entityInfo == null) {
				initEntityInfo(beanClass);
			}
			ColumnInfo columnInfo = ENTITY_INFO_MAP.get(beanClass).getId();
			return columnInfo.getMethodHandle().invoke(obj);
		} catch (Throwable e) {
			throw new SqlBuilderException(e);
		}
	}

}
