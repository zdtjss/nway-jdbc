/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.ClassUtils;

import com.nway.spring.classwork.DynamicObjectException;
import com.nway.spring.jdbc.annotation.Column;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;

/**
 * <p>
 * <code>BeanProcessor</code> matches column names to bean property names and converts
 * <code>ResultSet</code> columns into objects for those bean properties. Subclasses should override
 * the methods in the processing chain to customize behavior.
 * </p>
 *
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * 基于apache-dbutils，针对动态bean生成,修改了大部分方法的实现
 * <p>
 *
 * @since DbUtils 1.1
 */
class JavassistBeanProcessor implements BeanProcessor {

	private static final Map<String, DbBeanFactory> DBBEANFACTORY_CACHE = new HashMap<String, DbBeanFactory>();

    /**
     * Special array value used by <code>mapColumnsToProperties</code> that indicates there is no
     * bean property that matches a column from a <code>ResultSet</code>.
     */
    private static final int PROPERTY_NOT_FOUND = -1;
    
    /**
     * Convert a <code>ResultSet</code> into a <code>List</code> of JavaBeans. This implementation
     * uses reflection and <code>BeanInfo</code> classes to match column names to bean property
     * names. Properties are matched to columns based on several factors: <br/>
     * <ol>
     * <li>
     * The class has a writable property with the same name as a column. The name comparison is case
     * insensitive.</li>
     *
     * <li>
     * The column type can be converted to the property's set method parameter type with a
     * ResultSet.get* method. If the conversion fails (ie. the property was an int and the column
     * was a Timestamp) an SQLException is thrown.</li>
     * </ol>
     *
     * <p>
     * Primitive bean properties are set to their defaults when SQL NULL is returned from the
     * <code>ResultSet</code>. Numeric fields are set to 0 and booleans are set to false. Object
     * bean properties are set to <code>null</code> when SQL NULL is returned. This is the same
     * behavior as the <code>ResultSet</code> get* methods.
     * </p>
     *
     * @param <T> The type of bean to create
     * @param rs ResultSet that supplies the bean data
     * @param type Class from which to create the bean instance
     * @throws SQLException if a database access error occurs
     * @return the newly created List of beans
     */
    public <T> List<T> toBeanList(ResultSet rs, Class<T> type, String cacheKey) throws SQLException {

        if (!rs.next()) {
        	
            return Collections.emptyList();
        }
        
        final List<T> results = new ArrayList<T>();

        //String cacheKey = DynamicClassUtils.makeCacheKey(rs, type.getName());
		
        do {
        	
            results.add(toBean(rs, type, cacheKey));
        }
        while (rs.next());

        return results;
    }
    
    /**
     * 生成动态bean，并将数据和bean合并
     * <p>
     *
     * <b>默认优先使用ASM生成动态bean，如果需要以javassist方式生成动态bean，请将本实现切换为{@link
     * this#createBeanByJavassist(ResultSet, Class, String)}</b>
     *
     * @param <T>
     * @param rs {@link ResultSet}
     * @param type bean类型
     * @param querying sql查询信息
     * @return 包含数据的bean
     *
     * @throws SQLException
     */
    public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
    	
    	return toBean(rs, type, null);
    }

	public <T> T toBean(ResultSet rs, Class<T> type, String cacheKey) throws SQLException {

		if (cacheKey == null) {

			cacheKey = DynamicClassUtils.makeCacheKey(rs, type.getName());
		} 

		/*
		 * 同步可以提高单次响应效率，但会降低系统整体吞吐量。
		 * 如果不做线程同步，只有当存在某一查询一开始就大量并发访问时，才会在前几次查询中重复定义动态相同的DbBeanFactory
		 * 以type对象作为同步锁，降低线程同步对系统整体吞吐量的影响
		 */
//		synchronized (type) {

			return createBeanByJavassist(rs, type, cacheKey);

//		}

	}

    private <T> T createBeanByJavassist(ResultSet rs, Class<T> mappedClass, String key)
            throws SQLException {

        DbBeanFactory dynamicRse = DBBEANFACTORY_CACHE.get(key);

        // 如果缓存中有则直接返回
        if (dynamicRse != null) {

            return dynamicRse.createBean(rs, mappedClass);
        }

        T bean = this.newInstance(mappedClass);

        ResultSetMetaData rsmd = rs.getMetaData();

        PropertyDescriptor[] props = BeanUtils.getPropertyDescriptors(mappedClass);

        int[] columnToProperty = this.mapColumnsToProperties(rsmd, props);

        StringBuilder handlerScript = new StringBuilder();

        handlerScript.append("{").append(mappedClass.getName()).append(" bean = new ")
                .append(mappedClass.getName()).append("();\n");

        PropertyDescriptor desc = null;
        for (int i = 1; i < columnToProperty.length; i++) {

            if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
                continue;
            }

            desc = props[columnToProperty[i]];
            Class<?> propType = desc.getPropertyType();

            Object value = processColumn(rs, i, propType, desc.getWriteMethod().getName(),
                    handlerScript);

            this.callSetter(bean, desc, value);

        }

        handlerScript.append("return bean;");

        handlerScript.append("}");

        try {

        	ClassPool classPool = ClassPool.getDefault();

    		classPool.appendClassPath(new LoaderClassPath(ClassUtils.getDefaultClassLoader()));

            CtClass ctHandler = classPool.makeClass(DynamicClassUtils.getBeanProcessorName(mappedClass));
            ctHandler.setSuperclass(classPool.get("com.nway.spring.jdbc.bean.DbBeanFactory"));
            
            CtMethod mapRow = CtNewMethod
                    .make("public Object createBean(java.sql.ResultSet rs, Class type) throws java.sql.SQLException{return null;}",
                            ctHandler);

            mapRow.setGenericSignature("<T:Ljava/lang/Object;>(Ljava/sql/ResultSet;Ljava/lang/Class<TT;>;)TT;");
            
            mapRow.setBody(handlerScript.toString());

            ctHandler.addMethod(mapRow);
			
            DBBEANFACTORY_CACHE.put(key, (DbBeanFactory) ctHandler.toClass().newInstance());

        } catch (Exception e) {

            throw new DynamicObjectException("使用javassist创建 [ " + mappedClass.getName() + " ] 失败", e);
        }

        return bean;
    }


    /**
     * Calls the setter method on the target object for the given property. If no setter method
     * exists for the property, this method does nothing.
     *
     * @param target The object to set the property on.
     * @param prop The property to set.
     * @param value The value to pass into the setter.
     * @throws SQLException if an error occurs setting the property.
     */
    private void callSetter(Object target, PropertyDescriptor prop, Object value)
            throws SQLException {

        Method setter = prop.getWriteMethod();

        if (setter == null) {
        	
            return;
        }

        try {

            // Don't call setter if the value object isn't the right type
            // if (this.isCompatibleType(value, params[0])) {
            setter.invoke(target, new Object[]{value});
            
        } 
        catch (Exception e) {

            throw new SQLException("Cannot set " + prop.getName() + ": " + e.toString(), e);
        }
    }

    /**
     * Factory method that returns a new instance of the given Class. This is called at the start of
     * the bean creation process and may be overridden to provide custom behavior like returning a
     * cached bean instance.
     *
     * @param <T> The type of object to create
     * @param c The Class to create an object from.
     * @return A newly created object of the Class.
     * @throws SQLException if creation failed.
     */
    private <T> T newInstance(Class<T> c) throws SQLException {

        try {

            return c.newInstance();
        } 
        catch (InstantiationException e) {

            throw new SQLException("Cannot create " + c.getName() + ": " + e.toString(), e);
        } 
        catch (IllegalAccessException e) {

            throw new SQLException("Cannot create " + c.getName() + ": " + e.toString(), e);
        }
    }

    /**
     * The positions in the returned array represent column numbers. The values stored at each
     * position represent the index in the <code>PropertyDescriptor[]</code> for the bean property
     * that matches the column name. If no bean property was found for a column, the position is set
     * to <code>PROPERTY_NOT_FOUND</code>.
     *
     * @param rsmd The <code>ResultSetMetaData</code> containing column information.
     *
     * @param props The bean property descriptors.
     *
     * @throws SQLException if a database access error occurs
     *
     * @return An int[] with column index to property index mappings. The 0th element is meaningless
     * because JDBC column indexing starts at 1.
     */
    private int[] mapColumnsToProperties(ResultSetMetaData rsmd, PropertyDescriptor[] props)
            throws SQLException {

        int cols = rsmd.getColumnCount();
        int[] columnToProperty = new int[cols + 1];

        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        for (int col = 1; col <= cols; col++) {

            String columnName = rsmd.getColumnLabel(col);

            for (int i = 0; i < props.length; i++) {

                Column columnAnnotation = props[i].getReadMethod().getAnnotation(Column.class);

                if (columnAnnotation == null) {

                    //去除列名里的下划线'_'
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

	private Object processColumn(ResultSet rs, int index, Class<?> propType, String writer,
			StringBuilder handler) throws SQLException
	{
		if (propType.equals(String.class))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getString(").append(index).append("));");
			return rs.getString(index);
		}
		else if (propType.equals(Integer.TYPE))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getInt(").append(index).append("));");
			return rs.getInt(index);
		}
		else if (propType.equals(Integer.class))
		{
			handler.append("bean.").append(writer).append("(")
					.append("integerValue($1.getInt(").append(index)
					.append("),$1.wasNull()));");
			return JdbcUtils.getResultSetValue(rs, index, Integer.class);
		}
		else if (propType.equals(Long.TYPE))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getLong(").append(index).append("));");
			return rs.getLong(index);
		}
		else if (propType.equals(Long.class))
		{
			handler.append("bean.").append(writer).append("(")
					.append("longValue($1.getLong(").append(index)
					.append("),$1.wasNull()));");
			return JdbcUtils.getResultSetValue(rs, index, Long.class);
		}
		else if (propType.equals(java.sql.Date.class))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getDate(").append(index).append("));");
			return rs.getDate(index);
		}
		else if (propType.equals(java.util.Date.class) || propType.equals(Timestamp.class))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getTimestamp(").append(index).append("));");
			return rs.getTimestamp(index);
		}
		else if (propType.equals(Double.TYPE))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getDouble(").append(index).append("));");
			return rs.getDouble(index);
		}
		else if (propType.equals(Double.class))
		{
			handler.append("bean.").append(writer).append("(")
					.append("doubleValue($1.getDouble(").append(index)
					.append("),$1.wasNull()));");
			return JdbcUtils.getResultSetValue(rs, index, Double.class);
		}
		else if (propType.equals(Float.TYPE))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getFloat(").append(index).append("));");
			return rs.getFloat(index);
		}
		else if (propType.equals(Float.class))
		{
			handler.append("bean.").append(writer).append("(")
					.append("floatValue($1.getFloat(").append(index)
					.append("),$1.wasNull()));");
			return JdbcUtils.getResultSetValue(rs, index, Float.class);
		}
		else if (propType.equals(Time.class))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getTime(").append(index).append("));");
			return rs.getTime(index);
		}
		else if (propType.equals(Boolean.TYPE))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getBoolean(").append(index).append("));");
			return rs.getBoolean(index);
		}
		else if (propType.equals(Boolean.class))
		{
			handler.append("bean.").append(writer).append("(")
					.append("booleanValue($1.getBoolean(").append(index)
					.append("),$1.wasNull()));");
			return JdbcUtils.getResultSetValue(rs, index, Boolean.class);
		}
		else if (propType.equals(byte[].class))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getBytes(").append(index).append("));");
			return rs.getBytes(index);
		}
		else if (BigDecimal.class.equals(propType))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getBigDecimal(").append(index).append("));");
			return rs.getBigDecimal(index);
		}
		else if (Blob.class.equals(propType))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getBlob(").append(index).append("));");
			return rs.getBlob(index);
		}
		else if (Clob.class.equals(propType))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getClob(").append(index).append("));");
			return rs.getClob(index);
		}
		else if (propType.equals(Short.TYPE))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getShort(").append(index).append("));");
			return rs.getShort(index);
		}
		else if (propType.equals(Short.class))
		{
			handler.append("bean.").append(writer).append("(")
					.append("shortValue($1.getShort(").append(index)
					.append("),$1.wasNull()));");
			return JdbcUtils.getResultSetValue(rs, index, Short.class);
		}
		else if (propType.equals(Byte.TYPE))
		{
			handler.append("bean.").append(writer).append("(").append("$1.getByte(").append(index).append("));");
			return rs.getByte(index);
		}
		else if (propType.equals(Byte.class))
		{
			handler.append("bean.").append(writer).append("(")
					.append("byteValue($1.getByte(").append(index)
					.append("),$1.wasNull()));");
			return JdbcUtils.getResultSetValue(rs, index, Byte.class);
		}
		else
		{
			handler.append("bean.").append(writer).append("(").append("(")
					.append(propType.getName()).append(")").append("$1.getObject(").append(index)
					.append("));");
			return rs.getObject(index);
		}
	}
}
