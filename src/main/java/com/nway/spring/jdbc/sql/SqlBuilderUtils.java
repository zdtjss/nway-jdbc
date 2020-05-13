package com.nway.spring.jdbc.sql;

import com.esotericsoftware.reflectasm.MethodAccess;
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

import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SqlBuilderUtils {

    private static final Map<Class<?>, EntityInfo> ENTITY_INFO_MAP = new ConcurrentHashMap<>(256);
    private static final Map<Class<?>, SerializedLambda> SERIALIZED_LAMBDA_MAP = new ConcurrentHashMap<>(256);
	private static final Map<Class, MethodAccess> METHOD_ACCESS_MAP = new ConcurrentHashMap<>(256);

	private static void initEntityInfo(Class<?> claszz) {
		if (ENTITY_INFO_MAP.containsKey(claszz)) {
			return;
		}
		try {
			Field[] declaredFields = ReflectionUtils.getDeclaredFields(claszz);
			EntityInfo entityInfo = new EntityInfo();
			entityInfo.setTableName(getTableName(claszz));
			entityInfo.setColumnList(new HashMap<>(declaredFields.length));

			MethodAccess methodAccess = Optional.ofNullable(METHOD_ACCESS_MAP.get(claszz))
					.orElseGet(() -> {
						MethodAccess method = MethodAccess.get(claszz);
						METHOD_ACCESS_MAP.put(claszz, method);
						return method;
					});

			for (Field field : declaredFields) {
				PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), claszz);
				Column column = field.getAnnotation(Column.class);
				if(column != null && ColumnType.NONE.equals(column.type())) {
					continue;
				}
				ColumnInfo columnInfo = new ColumnInfo();
				columnInfo.setColumnName(getColumnName(field));
				columnInfo.setReadIndex(methodAccess.getIndex(descriptor.getReadMethod().getName()));
				columnInfo.setMethodHandle(methodAccess);
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
		return Optional.ofNullable(ENTITY_INFO_MAP.get(claszz))
				.orElseGet(() -> {
					initEntityInfo(claszz);
					return ENTITY_INFO_MAP.get(claszz);
				});
	}

	public static <T> String getColumn(Class<?> beanClass, SSupplier<T> lambda) {
		try {
			SerializedLambda serializedLambda = getSerializedLambda(lambda);
			return methodToColumn(beanClass, serializedLambda.getImplMethodName());
		} catch (Exception e) {
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

	public static <T> SerializedLambda getSerializedLambda(SSupplier<T> lambda) {
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

	public static <T, R> SerializedLambda getSerializedLambda(SFunction<T, R> lambda) {
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

	public static Object getColumnValue(ColumnInfo columnInfo, Object obj, SqlType sqlType) {
		if (columnInfo.getFillStrategy().isSupport(sqlType)) {
			return columnInfo.getFillStrategy().getValue(sqlType);
		}
		return columnInfo.getMethodHandle().invoke(obj, columnInfo.getReadIndex());
	}
	
	public static String getTableNameFromCache(Class<?> entityClass) {
		return getEntityInfo(entityClass).getTableName();
	}

	private static String getTableName(Class<?> entityClass) {
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
		return getEntityInfo(beanClass).getColumnList().get(fieldName).getColumnName();
	}

	public static String getIdName(Class<?> beanClass) {
		EntityInfo entityInfo = ENTITY_INFO_MAP.get(beanClass);
		if (entityInfo == null) {
			initEntityInfo(beanClass);
		}
		return ENTITY_INFO_MAP.get(beanClass).getId().getColumnName();
	}

	public static Object getIdValue(Class<?> beanClass, Object obj) {
		try {
			ColumnInfo columnInfo = getEntityInfo(beanClass).getId();
			return columnInfo.getMethodHandle().invoke(obj, columnInfo.getReadIndex());
		} catch (Exception e) {
			throw new SqlBuilderException(e);
		}
	}

}
