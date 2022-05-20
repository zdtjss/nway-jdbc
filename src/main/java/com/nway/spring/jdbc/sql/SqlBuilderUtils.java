package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.annotation.Table;
import com.nway.spring.jdbc.annotation.enums.ColumnType;
import com.nway.spring.jdbc.sql.builder.SqlBuilderException;
import com.nway.spring.jdbc.sql.fill.NoneFillStrategy;
import com.nway.spring.jdbc.sql.function.SFunction;
import com.nway.spring.jdbc.sql.function.SSupplier;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;
import com.nway.spring.jdbc.sql.permission.NonePermissionStrategy;
import com.nway.spring.jdbc.sql.permission.WhereCondition;
import com.nway.spring.jdbc.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SqlBuilderUtils {

    private static final Map<Class<?>, EntityInfo> ENTITY_INFO_MAP = new ConcurrentHashMap<>(256);
    private static final Map<Class<?>, SerializedLambda> SERIALIZED_LAMBDA_MAP = new ConcurrentHashMap<>(256);

	private static void initEntityInfo(Class<?> claszz) {
		if (ENTITY_INFO_MAP.containsKey(claszz)) {
			return;
		}
		try {
			Field[] declaredFields = ReflectionUtils.getAllFields(claszz);
			EntityInfo entityInfo = new EntityInfo();
			entityInfo.setTableName(getTableName(claszz));
			entityInfo.setColumnList(new ArrayList<>(declaredFields.length));
			entityInfo.setMultiValue(new ArrayList<>(8));
			entityInfo.setColumnMap(new HashMap<>(declaredFields.length));
			Map<String, ColumnInfo> columnMap = entityInfo.getColumnMap();
			for (Field field : declaredFields) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				Column column = field.getAnnotation(Column.class);
				if(column != null && ColumnType.IGNORE.equals(column.type())) {
					continue;
				}
				field.setAccessible(true);
				ColumnInfo columnInfo = new ColumnInfo();
				columnInfo.setColumnName(getColumnName(field));
				columnInfo.setReadMethod(field);
				if (column != null) {
					columnInfo.setFillStrategy(column.fillStrategy().getConstructor().newInstance());
					columnInfo.setPermissionStrategy(column.permissionStrategy().getConstructor().newInstance());
					if (ColumnType.ID.equals(column.type())) {
						entityInfo.setId(columnInfo);
					}
					else if(ColumnType.MULTI_VALUE.equals(column.type())) {
						entityInfo.getMultiValue().add(columnInfo);
					}
				}
				else {
					columnInfo.setFillStrategy(new NoneFillStrategy());
					columnInfo.setPermissionStrategy(new NonePermissionStrategy());
				}
				columnMap.put(field.getName(), columnInfo);
				// 多值字段在子表查
				if(column == null || !ColumnType.MULTI_VALUE.equals(column.type())) {
					entityInfo.getColumnList().add(columnInfo.getColumnName());
				}
			}
			ENTITY_INFO_MAP.put(claszz, entityInfo);
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
	}

	public static EntityInfo getEntityInfo(Class<?> claszz) {
		return Optional.ofNullable(ENTITY_INFO_MAP.get(claszz))
				.orElseGet(() -> {
					initEntityInfo(claszz);
					return ENTITY_INFO_MAP.get(claszz);
				});
	}

	public static <T, R> EntityInfo getEntityInfo(SFunction<T, R> lambda) {
		SerializedLambda serializedLambda = getSerializedLambda(lambda);
		try {
			final Class<?> claszz = Class.forName(serializedLambda.getImplClass().replace("/", "."));
			return Optional.ofNullable(ENTITY_INFO_MAP.get(claszz))
					.orElseGet(() -> {
						initEntityInfo(claszz);
						return ENTITY_INFO_MAP.get(claszz);
					});
		} catch (ClassNotFoundException e) {
			throw new SqlBuilderException(e);
		}
	}

	public static String getAllColumn(Class<?> beanClass) {
		EntityInfo entityInfo = getEntityInfo(beanClass);
		return String.join(",", entityInfo.getColumnList());
	}

	public static List<String> getColumnsWithoutId(Class<?> beanClass) {
		EntityInfo entityInfo = getEntityInfo(beanClass);
		List<String> columnList = new ArrayList<>(entityInfo.getColumnList());
		columnList.remove(entityInfo.getId().getColumnName());
		return columnList;
	}

	public static <T> String getColumn(SSupplier<T> lambda) {
		try {
			SerializedLambda serializedLambda = getSerializedLambda(lambda);
			return methodToColumn(Class.forName(serializedLambda.getImplClass().replace("/", ".")), serializedLambda.getImplMethodName());
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
	}

	public static <T> String getColumn(Class<?> beanClass, SSupplier<T> lambda) {
		try {
			SerializedLambda serializedLambda = getSerializedLambda(lambda);
			return methodToColumn(beanClass, serializedLambda.getImplMethodName());
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
	}

	public static <T, R> String getColumn(SFunction<T, R> lambda) {
		try {
			SerializedLambda serializedLambda = getSerializedLambda(lambda);
			return methodToColumn(Class.forName(serializedLambda.getImplClass().replace("/", ".")), serializedLambda.getImplMethodName());
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

	public static WhereCondition getWhereCondition(ColumnInfo columnInfo) {
		if (columnInfo.getPermissionStrategy().getClass() == NonePermissionStrategy.class) {
			return null;
		}
		return columnInfo.getPermissionStrategy().getSqlSegment(columnInfo.getColumnName());
	}

	public static <T extends Serializable> SerializedLambda getSerializedLambda(T lambda) {
		Class<?> funcClass = lambda.getClass();
		return Optional.ofNullable(SERIALIZED_LAMBDA_MAP.get(funcClass))
				.orElseGet(() -> {
					try {
						Method writeReplace = funcClass.getDeclaredMethod("writeReplace");
						writeReplace.setAccessible(true);
						SerializedLambda serializedLambda = (SerializedLambda) MethodHandles.lookup().unreflect(writeReplace).invoke(lambda);
						SERIALIZED_LAMBDA_MAP.put(funcClass, serializedLambda);
						return serializedLambda;
					} catch (Throwable e) {
						throw new SqlBuilderException(e);
					}
				});
	}

	private static String getColumnName(Field field) {
		Column column = field.getAnnotation(Column.class);
		if(column != null && (column.name().length() > 0 || column.value().length() > 0)) {
			return column.value().length() > 0 ? column.value() : column.name();
		}
		else {
			return fieldNameToColumn(field.getName());
		}
	}

	/**
	 * 先从obj中读取属性值，如果为null，则尝试通过FillStrategy读取，如果没有指定FillStrategy，则返回会null
	 *
	 * @param columnInfo
	 * @param obj
	 * @param sqlType
	 * @return
	 */
	public static Object getColumnValue(ColumnInfo columnInfo, Object obj, SqlType sqlType) {
		Object objVal;
		try {
			objVal = columnInfo.getReadMethod().get(obj);
		} catch (IllegalAccessException e) {
			throw new SqlBuilderException(e);
		}
		if (columnInfo.getFillStrategy().isSupport(sqlType)) {
			Object value = columnInfo.getFillStrategy().getValue(sqlType, objVal);
			try {
				columnInfo.getReadMethod().set(obj, value);
			} catch (IllegalAccessException e) {
				throw new SqlBuilderException(e);
			}
			return value;
		}
		return null;
	}
	
	public static String getTableNameFromCache(Class<?> entityClass) {
		return getEntityInfo(entityClass).getTableName();
	}

	private static String getTableName(Class<?> entityClass) {
		String tableName = "";
		Table table = entityClass.getAnnotation(Table.class);
		if(table != null) {
			tableName = table.value().length() > 0 ? table.value() : table.name();
		}
		if (tableName.length() != 0) {
			return tableName;
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
		return columnName.charAt(0) == '_' ? columnName.substring(1) : columnName.toString();
	}

	public static String columnToFieldName(String columnName) {
		StringBuilder str = new StringBuilder();
		char[] chars = columnName.toLowerCase().toCharArray();
		int underLineIdx = -1;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '_') {
				underLineIdx = i + 1;
			} else if (i == underLineIdx) {
				str.append(String.valueOf(chars[i]).toUpperCase());
			} else {
				str.append(chars[i]);
			}
		}
		return str.toString();
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
		return getEntityInfo(beanClass).getColumnMap().get(fieldName).getColumnName();
	}

	public static String getIdName(Class<?> beanClass) {
		EntityInfo entityInfo = ENTITY_INFO_MAP.get(beanClass);
		if (entityInfo == null) {
			initEntityInfo(beanClass);
		}
		return ENTITY_INFO_MAP.get(beanClass).getId().getColumnName();
	}

	public static Object getIdValue(Class<?> beanClass, Object obj) {
		ColumnInfo columnInfo = getEntityInfo(beanClass).getId();
		try {
			return columnInfo.getReadMethod().get(obj);
		} catch (IllegalAccessException e) {
			throw new SqlBuilderException(e);
		}
	}

	public static Object[] getIdValue(Class<?> beanClass, List<?> objs) {
		Object[] idValList = new Object[objs.size()];
		ColumnInfo columnInfo = getEntityInfo(beanClass).getId();
		try {
			for (int i = 0; i < objs.size(); i++) {
				idValList[i] = columnInfo.getReadMethod().get(objs.get(i));
			}
		} catch (IllegalAccessException e) {
			throw new SqlBuilderException(e);
		}
		return idValList;
	}

}
