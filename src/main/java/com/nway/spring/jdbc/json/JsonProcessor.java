package com.nway.spring.jdbc.json;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import com.nway.spring.classwork.DynamicBeanClassLoader;
import com.nway.spring.classwork.DynamicObjectException;
import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.bean.DynamicClassUtils;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;

/**
 * 
 * @author zdtjss@163.com
 * @since 2014-03-28
 */
class JsonProcessor extends JsonBuilder
{
	private static final int PROPERTY_NOT_FOUND = -1;
	
    private static final int PROPERTY_TYPE_BOOLEAN = 1;
    private static final int PROPERTY_TYPE_BYTE = 2;
    private static final int PROPERTY_TYPE_SHORT = 3;
    private static final int PROPERTY_TYPE_INTEGER = 4;
    private static final int PROPERTY_TYPE_LONG = 5;
    private static final int PROPERTY_TYPE_FLOAT = 6;
    private static final int PROPERTY_TYPE_DOUBLE = 7;
    private static final int PROPERTY_TYPE_SQLDATE = 8;
    private static final int PROPERTY_TYPE_TIME = 9;
    private static final int PROPERTY_TYPE_DATE = 10;
    private static final int PROPERTY_TYPE_TIMESTAMP = 11;
    
    private static final int  DYNAMIC_CLASS_ORIGIN_LINE_NUMBER = 7;
    private static final int  DYNAMIC_CLASS_LINE_NUMBER_STEP  = 3;
    
    private static final boolean HAS_ASM = ClassUtils.isPresent("org.objectweb.asm.ClassWriter", ClassUtils.getDefaultClassLoader());
	
	private static final boolean HAS_JAVASSIST = ClassUtils.isPresent("javassist.ClassPool", ClassUtils.getDefaultClassLoader());
	
	/**
     * 缓存动态生成的 DbBeanFactory，这里的cache是静态的，意味着无论BeanProcessor被new多少次，cache都是同一个
     */
    private static final Map<String, JsonBuilder> JSON_BUILDER_CACHE = new ConcurrentHashMap<String, JsonBuilder>();

	public String buildJson(ResultSet rs, Class<?> type) throws SQLException, IntrospectionException {

		return buildJson(rs, type);
	}
    
	private String buildJson(ResultSet rs, Class<?> type, String cacheKey) throws SQLException, IntrospectionException {
	
		if (cacheKey == null) {

			cacheKey = DynamicClassUtils.makeCacheKey(rs,  type.getName());
		} 

		/*
		 * 同步可以提高单次响应效率，但会降低系统整体吞吐量。
		 * 如果不做线程同步，只有当存在某一查询一开始就大量并发访问时，才会在前几次查询中重复定义动态相同的DbBeanFactory
		 * 以type对象作为同步锁，降低线程同步对系统整体吞吐量的影响
		 */
//		synchronized (type) {

			if (HAS_ASM) {

				return buildJsonByAsm(rs, type, cacheKey);
			} 
			else if (HAS_JAVASSIST) {

				return buildJsonByJavassist(rs, type, cacheKey);
			} 
			else {

				return buildJsonWithJdk(rs, type);
			}
//		}
	}
	
	public String toJsonList(ResultSet rs, Class<?> type) throws SQLException, IntrospectionException {
	
		StringBuilder json = new StringBuilder("[");

		String cacheKey = DynamicClassUtils.makeCacheKey(rs, type.getName());
		
		do {
			
			json.append(buildJson(rs, type, cacheKey)).append(',');
		}
		while (rs.next());

		if (json.length() > 1) {
			
			json = json.deleteCharAt(json.length() - 1);
		}

		return json.append(']').toString();
	}
	
	private String buildJsonWithJdk(ResultSet rs, Class<?> type) throws SQLException, IntrospectionException
	{
		ResultSetMetaData rsmd = rs.getMetaData();

		PropertyDescriptor[] props = Introspector.getBeanInfo(type).getPropertyDescriptors();

		int[] columnToProperty = this.mapColumnsToProperties(rsmd, props);

		StringBuilder json = new StringBuilder("{");

		for (int index = 1; index < columnToProperty.length; index++)
		{
			if (columnToProperty[index] == PROPERTY_NOT_FOUND)
			{
				continue;
			}

			PropertyDescriptor prop = props[columnToProperty[index]];
			Class<?> propType = prop.getPropertyType();

			json.append('\"').append(prop.getName()).append("\":");

			if (String.class.equals(propType) || Clob.class.equals(propType))
			{
				stringValue(rs.getString(index), json);
			}
			else if (boolean.class.equals(propType) || Boolean.class.equals(propType))
			{
				booleanValue(rs.getBoolean(index), rs.wasNull(), json);
			}
			else if (int.class.equals(propType) || Integer.class.equals(propType))
			{
				integerValue(rs.getInt(index), rs.wasNull(), json);
			}
			else if (long.class.equals(propType) || Long.class.equals(propType))
			{
				longValue(rs.getLong(index), rs.wasNull(), json);
			}
			else if (float.class.equals(propType) || Float.class.equals(propType))
			{
				floatValue(rs.getFloat(index), rs.wasNull(), json);
			}
			else if (double.class.equals(propType) || Double.class.equals(propType))
			{
				doubleValue(rs.getDouble(index), rs.wasNull(), json);
			}
			else if (java.util.Date.class.equals(propType))
			{
				dateValue(rs.getDate(index), "yyyy-MM-dd HH:mm:ss", json);
			}
			else if (java.sql.Timestamp.class.equals(propType))
			{
				dateValue(rs.getTimestamp(index), "yyyy-MM-dd HH:mm:ss.SSS", json);
			}
			else if (java.sql.Date.class.equals(propType))
			{
				dateValue(rs.getDate(index), "yyyy-MM-dd", json);
			}
			else if (java.sql.Time.class.equals(propType))
			{
				dateValue(rs.getTime(index), "HH:mm:ss", json);
			} 
			else 
			{
				json.append('\"').append(rs.getObject(index)).append('\"');
			}
			
			json.append(',');
		}

		if (json.length() > 1)
		{
			json = json.deleteCharAt(json.length() - 1);
		}

		return json.append('}').toString();
	}
	
	private String buildJsonByJavassist(ResultSet rs, Class<?> type, String key) throws SQLException, IntrospectionException {
		
		JsonBuilder jsonBuilder = JSON_BUILDER_CACHE.get(key);
		
		if (jsonBuilder != null) {

			return jsonBuilder.buildJson(rs);
		}
		
		StringBuilder[] sb = processByJavasist(rs, type);
		
		try {

            ClassPool classPool = ClassPoolCreator.getClassPool();
            
            CtClass ctHandler = classPool.makeClass(DynamicClassUtils.getProcessorName(type));
            CtClass superClass = classPool.get("com.nway.spring.jdbc.json.JsonBuilder");
            
            ctHandler.setSuperclass(superClass);
            
            ctHandler.addMethod(CtNewMethod.make(sb[1].toString(), ctHandler));
			
			JSON_BUILDER_CACHE.put(key, (JsonBuilder) ctHandler.toClass().newInstance());

        } catch (Exception e) {

            throw new DynamicObjectException("使用javassist创建 [ " + type.getName() + " ] 失败", e);
        }
		
		return sb[0].toString();
	}

	private StringBuilder[] processByJavasist(ResultSet rs,Class<?> type) throws SQLException, IntrospectionException{
		
		ResultSetMetaData rsmd = rs.getMetaData();
		
		PropertyDescriptor[] props = Introspector.getBeanInfo(type).getPropertyDescriptors();
		
		int[] columnToProperty = this.mapColumnsToProperties(rsmd, props);
		
		StringBuilder json = new StringBuilder("{");

		StringBuilder handlerScript = new StringBuilder("String buildJson(java.sql.ResultSet rs) throws java.sql.SQLException{");

		handlerScript.append("StringBuilder json = new StringBuilder(\"{\");");

		for (int index = 1; index < columnToProperty.length; index++)
		{
			if (columnToProperty[index] == PROPERTY_NOT_FOUND) {
				
				continue;
			}
			
			PropertyDescriptor prop = props[columnToProperty[index]];
			String propName = prop.getName();
			Class<?> propType = prop.getPropertyType();
			
			json.append('\"').append(propName).append("\":");
			
			handlerScript.append("json.append(\"\\\"").append(propName).append("\\\":\")");

			if (String.class.equals(propType) || Clob.class.equals(propType))
			{
				stringValue(rs.getString(index), json);
				
				handlerScript.append(";stringValue($1.getString(").append(index).append("),json);json.append(',');");
			}
			else if (boolean.class.equals(propType))
			{
				json.append(rs.getBoolean(index));
				
				handlerScript.append(".append($1.getBoolean(").append(index).append(")).append(',');");
			}
			else if (Boolean.class.equals(propType))
			{
				booleanValue(rs.getBoolean(index), rs.wasNull(), json);
				
				handlerScript.append(";booleanValue($1.getBoolean(").append(index).append("),$1.wasNull(),json);json.append(',');");
			}
			else if (int.class.equals(propType))
			{
				json.append(rs.getInt(index));
				
				handlerScript.append(".append($1.getInt(").append(index).append(")).append(',');");
			}
			else if (Integer.class.equals(propType))
			{
				integerValue(rs.getInt(index), rs.wasNull(), json);
				
				handlerScript.append(";integerValue($1.getInt(").append(index).append("),$1.wasNull(),json);json.append(',');");
			}
			else if (long.class.equals(propType))
			{
				json.append(rs.getLong(index));

				handlerScript.append(".append($1.getLong(").append(index).append(")).append(',');");
			}
			else if (Long.class.equals(propType))
			{
				longValue(rs.getLong(index), rs.wasNull(), json);
				
				handlerScript.append(";longValue($1.getLong(").append(index).append("),$1.wasNull(),json);json.append(',');");
			}
			else if (float.class.equals(propType))
			{
				json.append(rs.getFloat(index));
				
				handlerScript.append(".append($1.getFloat(").append(index).append(")).append(',');");
			}
			else if (Float.class.equals(propType))
			{
				floatValue(rs.getFloat(index), rs.wasNull(), json);
				
				handlerScript.append(";floatValue($1.getFloat(").append(index).append("),$1.wasNull(),json);json.append(',');");
			}
			else if (double.class.equals(propType))
			{
				json.append(rs.getDouble(index));
				
				handlerScript.append(".append($1.getDouble(").append(index).append(")).append(',');");
			}
			else if (Double.class.equals(propType))
			{
				doubleValue(rs.getDouble(index), rs.wasNull(), json);
				
				handlerScript.append(";doubleValue($1.getDouble(").append(index).append("),$1.wasNull(),json);json.append(',');");
			}
			else if (java.util.Date.class.equals(propType))
			{
				dateValue(rs.getDate(index), "yyyy-MM-dd HH:mm:ss", json);
				
				handlerScript.append(";dateValue($1.getDate(").append(index).append("),\"yyyy-MM-dd HH:mm:ss\",json);json.append(',');");
			}
			else if (java.sql.Timestamp.class.equals(propType))
			{
				dateValue(rs.getTimestamp(index), "yyyy-MM-dd HH:mm:ss.SSS", json);
				
				handlerScript.append(";dateValue($1.getTimestamp(").append(index).append("),\"yyyy-MM-dd HH:mm:ss.SSS\",json);json.append(',');");
			}
			else if (java.sql.Date.class.equals(propType))
			{
				dateValue(rs.getDate(index), "yyyy-MM-dd", json);
				
				handlerScript.append(";dateValue($1.getDate(").append(index).append("),\"yyyy-MM-dd\",json);json.append(',');");
			}
			else if (java.sql.Time.class.equals(propType))
			{
				dateValue(rs.getTime(index), "HH:mm:ss", json);
				
				handlerScript.append(";dateValue($1.getTime(").append(index).append("),\"HH:mm:ss\",json);json.append(',');");
			}
			else 
			{
				json.append('\"').append(rs.getObject(index).toString()).append('\"');
				handlerScript.append(".append($1.getObject(").append(index).append(").toString()).append('\"').append(',');");
			}
			
			json.append(',');
		}
		
		if (json.length() > 1)
		{
			json = json.deleteCharAt(json.length() - 1);
			
			handlerScript = handlerScript.append("if(json.length()>1){json = json.deleteCharAt(json.length() - 1);}json.append('}');return json.toString();}");
		}
		
		json.append('}');
		
		return new StringBuilder[] { json, handlerScript };
	}
	
	private String buildJsonByAsm(ResultSet rs, Class<?> type, String key) throws SQLException, IntrospectionException {
		
		JsonBuilder jsonBuilder = JSON_BUILDER_CACHE.get(key);
		
		if (jsonBuilder != null) {

			return jsonBuilder.buildJson(rs);
		}
		
		ClassWriter cw = new ClassWriter(0);
		
		String processorName = DynamicClassUtils.getProcessorName(type);
		
		String json = processByAsm( cw, processorName, rs, type);
		
		try {

			DynamicBeanClassLoader beanClassLoader = new DynamicBeanClassLoader(ClassUtils.getDefaultClassLoader());

			Class<?> processor = beanClassLoader.defineClass(processorName, cw.toByteArray());

			JSON_BUILDER_CACHE.put(key, (JsonBuilder) processor.newInstance());

        } catch (Exception e) {

            throw new DynamicObjectException("使用ASM创建 [ " + type.getName() + " ] 失败", e);
        }
		
		return json;
	}

	private String processByAsm(ClassWriter cw, String processorName, ResultSet rs,Class<?> type) throws SQLException, IntrospectionException{
		
		ResultSetMetaData rsmd = rs.getMetaData();
		
		PropertyDescriptor[] props = BeanUtils.getPropertyDescriptors(type);
		
		int[] columnToProperty = this.mapColumnsToProperties(rsmd, props);
		
        String internalProcessorName = processorName.replace('.', '/');
		
		StringBuilder json = new StringBuilder("{");
		
		Object[] initParam = initBuilder(cw, internalProcessorName);
		
		MethodVisitor mv = (MethodVisitor) initParam[0];
		
		for (int index = 1; index < columnToProperty.length; index++)
		{
			if (columnToProperty[index] == PROPERTY_NOT_FOUND) {
				
				continue;
			}
			
			PropertyDescriptor prop = props[columnToProperty[index]];
			String propName = prop.getName();
			Class<?> propType = prop.getPropertyType();
			int baseLineNumber = DYNAMIC_CLASS_ORIGIN_LINE_NUMBER + index * DYNAMIC_CLASS_LINE_NUMBER_STEP;
			
			json.append('\"').append(propName).append("\":");
			
			if (String.class.equals(propType) || Clob.class.equals(propType))
			{
				stringValue(rs.getString(index), json);
				stringValue(mv, internalProcessorName, propName, index, baseLineNumber);
			}
			else if (boolean.class.equals(propType))
			{
				json.append(rs.getBoolean(index));
				
				processPrimitive(mv, propName, index, PROPERTY_TYPE_BOOLEAN, baseLineNumber);
			}
			else if (Boolean.class.equals(propType))
			{
				booleanValue(rs.getBoolean(index), rs.wasNull(), json);
				
				booleanValue(mv,internalProcessorName, propName, index, baseLineNumber);
			}
			else if (int.class.equals(propType) )
			{
				json.append(rs.getInt(index));
				
				processPrimitive(mv, propName, index, PROPERTY_TYPE_INTEGER, baseLineNumber);
			}
			else if (Integer.class.equals(propType))
			{
				integerValue(rs.getInt(index), rs.wasNull(), json);
				
				integerValue(mv, internalProcessorName, propName, index, baseLineNumber);
			}
			else if (long.class.equals(propType))
			{
				json.append(rs.getLong(index));
				
				processPrimitive(mv, propName, index, PROPERTY_TYPE_LONG, baseLineNumber);
			}
			else if (Long.class.equals(propType))
			{
				longValue(rs.getLong(index), rs.wasNull(), json);
				
				longValue(mv, internalProcessorName, propName, index, baseLineNumber);
			}
			else if (float.class.equals(propType))
			{
				json.append(rs.getFloat(index));
				
				processPrimitive(mv, propName, index, PROPERTY_TYPE_FLOAT, baseLineNumber);
			}
			else if (Float.class.equals(propType))
			{
				floatValue(rs.getFloat(index), rs.wasNull(), json);
				
				floatValue(mv, internalProcessorName, propName, index, baseLineNumber);
			}
			else if (double.class.equals(propType))
			{
				json.append(rs.getDouble(index));
				
				processPrimitive(mv, propName, index, PROPERTY_TYPE_DOUBLE, baseLineNumber);
			}
			else if (Double.class.equals(propType))
			{
				doubleValue(rs.getDouble(index), rs.wasNull(), json);
				
				doubleValue(mv, internalProcessorName, propName, index, baseLineNumber);
			}
			else if (java.util.Date.class.equals(propType))
			{
				dateValue(rs.getDate(index), "yyyy-MM-dd HH:mm:ss", json);
				dateValue( mv, internalProcessorName, propName, index, PROPERTY_TYPE_DATE, baseLineNumber);
			}
			else if (java.sql.Timestamp.class.equals(propType))
			{
				dateValue(rs.getTimestamp(index), "yyyy-MM-dd HH:mm:ss.SSS", json);
				dateValue( mv, internalProcessorName, propName, index, PROPERTY_TYPE_TIMESTAMP, baseLineNumber);
			}
			else if (java.sql.Date.class.equals(propType))
			{
				dateValue(rs.getDate(index), "yyyy-MM-dd", json);
				dateValue( mv, internalProcessorName, propName, index, PROPERTY_TYPE_SQLDATE, baseLineNumber);
			}
			else if (java.sql.Time.class.equals(propType))
			{
				dateValue(rs.getTime(index), "HH:mm:ss", json);
				dateValue( mv, internalProcessorName, propName, index, PROPERTY_TYPE_TIME, baseLineNumber);
			}
			else 
			{
				json.append('\"').append(rs.getObject(index).toString()).append('\"');
				ojbectValue(mv, internalProcessorName, propName, index, baseLineNumber);
			}
			
			json.append(',');
		}
		
		if (json.length() > 1)
		{
			json = json.deleteCharAt(json.length() - 1);
			
		}
		
		json.append('}');
		
		finishBuilder(mv, internalProcessorName, (Label)initParam[1], (Label)initParam[2], DYNAMIC_CLASS_ORIGIN_LINE_NUMBER + columnToProperty.length * DYNAMIC_CLASS_LINE_NUMBER_STEP );
		
		cw.visitEnd();
		
		return json.toString();
	}

	/**
	 * returnVal[0] : MethodVisitor;<br>
	 * returnVal[1] : thisLabel;<br>
	 * returnVal[2] : jsonLabel;<br>
	 * 
	 * @param cw
	 * @param builderName
	 * @return
	 */
	private Object[] initBuilder(ClassWriter cw, String builderName) {
		
		MethodVisitor mv;
		Object[] returnVal = new Object[3];
		
		cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, builderName, null, "com/nway/spring/jdbc/json/JsonBuilder", null);
		
		{
			mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(6, l0);
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/nway/spring/jdbc/json/JsonBuilder", "<init>", "()V", false);
			mv.visitInsn(Opcodes.RETURN);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLocalVariable("this", "L"+builderName+";", null, l0, l1, 0);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "buildJson", "(Ljava/sql/ResultSet;)Ljava/lang/String;", null, new String[] { "java/sql/SQLException" });
			mv.visitCode();
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitLineNumber(10, l0);
			mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
			mv.visitInsn(Opcodes.DUP);
			mv.visitLdcInsn("{");
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitVarInsn(Opcodes.ASTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(12, l1);
			
			returnVal[1] = l0;
			returnVal[2] = l1;
		}
		
		returnVal[0] = mv;
			
		return returnVal;
	}
	
	private void finishBuilder(MethodVisitor mv, String builderName, Label thisLabel, Label jsonLabel, int currentLine) {
		
		Label l13 = new Label();
		mv.visitLabel(l13);
		mv.visitLineNumber(currentLine + 1, l13);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "length", "()I", false);
		mv.visitInsn(Opcodes.ICONST_1);
		Label l14 = new Label();
		mv.visitJumpInsn(Opcodes.IF_ICMPLE, l14);
		Label l15 = new Label();
		mv.visitLabel(l15);
		mv.visitLineNumber(currentLine + 2, l15);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "length", "()I", false);
		mv.visitInsn(Opcodes.ICONST_1);
		mv.visitInsn(Opcodes.ISUB);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "deleteCharAt", "(I)Ljava/lang/StringBuilder;", false);
		mv.visitVarInsn(Opcodes.ASTORE, 2);
		mv.visitLabel(l14);
		mv.visitLineNumber(currentLine + 3, l14);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitIntInsn(Opcodes.BIPUSH, 125);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
		mv.visitInsn(Opcodes.POP);
		Label l16 = new Label();
		mv.visitLabel(l16);
		mv.visitLineNumber(currentLine + 4, l16);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
		mv.visitInsn(Opcodes.ARETURN);
		Label l17 = new Label();
		mv.visitLabel(l17);
		mv.visitLocalVariable("this", "L"+builderName+";", null, thisLabel, l17, 0);
		mv.visitLocalVariable("rs", "Ljava/sql/ResultSet;", null, thisLabel, l17, 1);
		mv.visitLocalVariable("json", "Ljava/lang/StringBuilder;", null, jsonLabel, l17, 2);
		mv.visitMaxs(5, 3);
		mv.visitEnd();
	}
	
	private void processPrimitive(MethodVisitor mv,String fieldName, int index, int primitiveType, int lineNumber){

		if (index != 1) {

			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(lineNumber, l1);
		}
		
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitLdcInsn("\"" + fieldName + "\":");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		visitIndex(mv, index);
		visitPrimitiveValue(mv, primitiveType);
		
		mv.visitIntInsn(Opcodes.BIPUSH, 44);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
		mv.visitInsn(Opcodes.POP);
	}
	
	private void visitIndex(MethodVisitor mv, int index) {

		switch (index) {
		case 1:
			mv.visitInsn(Opcodes.ICONST_1);
			break;
		case 2:
			mv.visitInsn(Opcodes.ICONST_2);
			break;
		case 3:
			mv.visitInsn(Opcodes.ICONST_3);
			break;
		case 4:
			mv.visitInsn(Opcodes.ICONST_4);
			break;
		case 5:
			mv.visitInsn(Opcodes.ICONST_5);
			break;
		default:
			mv.visitIntInsn(Opcodes.BIPUSH, index);
		}
	}
	
	/**
	 * 
	 * 
	 * @param mv
	 * @param type
	 */
	private void visitPrimitiveValue(MethodVisitor mv, int type) {

		switch (type) {
		case PROPERTY_TYPE_INTEGER:
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getInt", "(I)I", true);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
			break;
		case PROPERTY_TYPE_LONG:
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getLong", "(I)J", true);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
			break;
		case PROPERTY_TYPE_BOOLEAN:
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getBoolean", "(I)Z", true);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;", false);
			break;
		case PROPERTY_TYPE_FLOAT:
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getFloat", "(I)F", true);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(F)Ljava/lang/StringBuilder;", false);
			break;
		case PROPERTY_TYPE_DOUBLE:
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getDouble", "(I)D", true);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;", false);
			break;
		case PROPERTY_TYPE_BYTE:
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getByte", "(I)B", true);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(B)Ljava/lang/StringBuilder;", false);
			break;
		case PROPERTY_TYPE_SHORT:
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getShort", "(I)S", true);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(S)Ljava/lang/StringBuilder;", false);
			break;
		}
	}
	
	private void integerValue(MethodVisitor mv, String builderName,String fieldName, int index, int lineNumber) {
		
		visistJsonField(mv, fieldName, index, lineNumber);

		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLineNumber(lineNumber + 1, l2);
		
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		visitIndex(mv, index);
		
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getInt", "(I)I", true);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "wasNull", "()Z", true);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, builderName, "integerValue", "(IZLjava/lang/StringBuilder;)V", false);
		
		visitComma(mv, lineNumber + 2);
	}
	
	private void longValue(MethodVisitor mv, String builderName, String fieldName, int index, int lineNumber) {
		
		visistJsonField(mv, fieldName, index, lineNumber);
		
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLineNumber(lineNumber + 1, l2);
		
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		visitIndex(mv, index);
		
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getLong", "(I)J", true);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "wasNull", "()Z", true);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, builderName, "longValue", "(JZLjava/lang/StringBuilder;)V", false);
		
		visitComma(mv, lineNumber + 2);
	}
	
	private void floatValue(MethodVisitor mv, String builderName, String fieldName, int index, int lineNumber) {
		
		visistJsonField(mv, fieldName, index, lineNumber);
		
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLineNumber(lineNumber + 1, l2);
		
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		visitIndex(mv, index);
		
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getFloat", "(I)F", true);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "wasNull", "()Z", true);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, builderName, "floatValue", "(FZLjava/lang/StringBuilder;)V", false);
		
		visitComma(mv, lineNumber + 2);
	}
	
	private void doubleValue(MethodVisitor mv, String builderName, String fieldName, int index, int lineNumber) {
		
		visistJsonField(mv, fieldName, index, lineNumber);
		
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLineNumber(lineNumber + 1, l2);
		
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		visitIndex(mv, index);
		
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getDouble", "(I)D", true);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "wasNull", "()Z", true);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, builderName, "doubleValue", "(DZLjava/lang/StringBuilder;)V", false);
		
		visitComma(mv, lineNumber + 2);
	}
	
	private void booleanValue(MethodVisitor mv, String builderName, String fieldName, int index, int lineNumber) {
		
		visistJsonField(mv, fieldName, index, lineNumber);
		
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLineNumber(lineNumber + 1, l2);
		
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		visitIndex(mv, index);
		
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getBoolean", "(I)Z", true);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "wasNull", "()Z", true);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, builderName, "booleanValue", "(ZZLjava/lang/StringBuilder;)V", false);
		
		visitComma(mv, lineNumber + 2);
	}
	
	private void dateValue(MethodVisitor mv, String builderName, String fieldName, int index, int dateType, int lineNumber) {
		
		visistJsonField(mv, fieldName, index, lineNumber);
		
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLineNumber(lineNumber + 1, l2);
		
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		visitIndex(mv, index);
		
		visitMethodInsnDate(mv, dateType);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, builderName, "dateValue", "(Ljava/util/Date;Ljava/lang/String;Ljava/lang/StringBuilder;)V", false);
		
		visitComma(mv, lineNumber + 2);
	}
	
	/**
	 * 
	 * 
	 * @param mv
	 * @param builderName com/nway/spring/jdbc/json/User14299279142700
	 * @param fieldName
	 * @param index
	 * @param lineNumber
	 */
	private void stringValue(MethodVisitor mv, String builderName, String fieldName, int index, int lineNumber) {
		
		visistJsonField(mv, fieldName, index, lineNumber);
		
		Label l5 = new Label();
		mv.visitLabel(l5);
		mv.visitLineNumber(lineNumber + 1, l5);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		visitIndex(mv, index);
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getString", "(I)Ljava/lang/String;", true);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, builderName, "stringValue", "(Ljava/lang/String;Ljava/lang/StringBuilder;)V", false);

		visitComma(mv, lineNumber + 2);
	}
	
	private void ojbectValue(MethodVisitor mv, String builderName, String fieldName, int index, int lineNumber) {
		
		visistJsonField(mv, fieldName, index, lineNumber);
		
		Label l12 = new Label();
		mv.visitLabel(l12);
		mv.visitLineNumber(lineNumber + 1, l12);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitIntInsn(Opcodes.BIPUSH, 34);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		visitIndex(mv, index);
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getObject", "(I)Ljava/lang/Object;", true);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
		mv.visitIntInsn(Opcodes.BIPUSH, 34);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
		mv.visitInsn(Opcodes.POP);

		visitComma(mv, lineNumber + 2);
	}
	
	/**
	 * 从ResultSet在取得不同Date类型值,并给出相应字符串格式
	 * 
	 * @param mv
	 * @param dateType
	 */
	private void visitMethodInsnDate(MethodVisitor mv, int dateType) {
		
		switch (dateType) {
		
		case PROPERTY_TYPE_DATE:
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getTimestamp", "(I)Ljava/sql/Timestamp;", true);
			mv.visitLdcInsn("yyyy-MM-dd HH:mm:ss");
			break;
		case PROPERTY_TYPE_TIMESTAMP:
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getTimestamp", "(I)Ljava/sql/Timestamp;", true);
			mv.visitLdcInsn("yyyy-MM-dd HH:mm:ss.SSS");
			break;
		case PROPERTY_TYPE_SQLDATE:
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getDate", "(I)Ljava/sql/Date;", true);
			mv.visitLdcInsn("yyyy-MM-dd");
			break;
		case PROPERTY_TYPE_TIME:
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/sql/ResultSet", "getTime", "(I)Ljava/sql/Time;", true);
			mv.visitLdcInsn("HH:mm:ss");
			break;
		}
	}
	
	/**
	 * 写json字段名, 如："name":
	 * 
	 * @param mv
	 * @param fieldName
	 * @param index
	 * @param lineNumber
	 */
	private void visistJsonField(MethodVisitor mv, String fieldName, int index, int lineNumber) {

		if (index != 1) {
			
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLineNumber(lineNumber, l1);
		}
		
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitLdcInsn("\"" + fieldName + "\":");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		mv.visitInsn(Opcodes.POP);
	}
	
	/**
	 * 追加分隔符（逗号:,）
	 * 
	 * @param mv
	 * @param currentLine
	 */
	private void visitComma(MethodVisitor mv, int lineNumber) {

		Label l3 = new Label();
		
		mv.visitLabel(l3);
		mv.visitLineNumber(lineNumber, l3);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitIntInsn(Opcodes.BIPUSH, 44);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
		mv.visitInsn(Opcodes.POP);
	}
	
	/**
	 * The positions in the returned array represent column numbers. The values
	 * stored at each position represent the index in the
	 * <code>PropertyDescriptor[]</code> for the bean property that matches the
	 * column name. If no bean property was found for a column, the position is
	 * set to <code>PROPERTY_NOT_FOUND</code>.
	 *
	 * @param rsmd
	 *            The <code>ResultSetMetaData</code> containing column
	 *            information.
	 *
	 * @param props
	 *            The bean property descriptors.
	 *
	 * @throws SQLException
	 *             if a database access error occurs
	 *
	 * @return An int[] with column index to property index mappings. The 0th
	 *         element is meaningless because JDBC column indexing starts at 1.
	 */
	private int[] mapColumnsToProperties(ResultSetMetaData rsmd, PropertyDescriptor[] props) throws SQLException {

		int cols = rsmd.getColumnCount();
		int[] columnToProperty = new int[cols + 1];

		Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

		for (int col = 1; col <= cols; col++)
		{
			String columnName = rsmd.getColumnLabel(col);

			for (int i = 0; i < props.length; i++)
			{
				Column columnAnnotation = props[i].getReadMethod().getAnnotation(Column.class);

				if (columnAnnotation == null)
				{
					// 去除列名里的下划线'_'
					if (columnName.replace("_", "").equalsIgnoreCase(props[i].getName()))
					{
						columnToProperty[col] = i;
						break;
					}
				}
				else if (columnName.equalsIgnoreCase(columnAnnotation.value())
						|| columnName.equalsIgnoreCase(columnAnnotation.name()))
				{
					columnToProperty[col] = i;
					break;
				}
			}
		}

		return columnToProperty;
	}

	@Override
	public String buildJson(ResultSet rs) throws SQLException {
		
		throw new UnsupportedClassVersionError("本实现不支持此方法");
	}
}
