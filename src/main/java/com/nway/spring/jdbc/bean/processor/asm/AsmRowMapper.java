package com.nway.spring.jdbc.bean.processor.asm;

import com.nway.spring.jdbc.bean.processor.RowMapper;
import com.nway.spring.jdbc.bean.processor.DefaultRowMapper;
import com.nway.spring.jdbc.sql.SqlBuilderUtils;
import com.nway.spring.jdbc.sql.meta.ColumnInfo;
import com.nway.spring.jdbc.sql.meta.EntityInfo;
import com.nway.spring.jdbc.util.ReflectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.*;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;


/**
 * {@link org.springframework.jdbc.core.RowMapper} implementation that converts a row into a new instance
 * of the specified mapped target class. The mapped target class must be a
 * top-level class and it must have a default or no-arg constructor.
 *
 * <p>Column values are mapped based on matching the column name as obtained from result set
 * meta-data to public setters for the corresponding properties. The names are matched either
 * directly or by transforming a name separating the parts with underscores to the same name
 * using "camel" case.
 *
 * <p>Mapping is provided for fields in the target class for many common types, e.g.:
 * String, boolean, Boolean, byte, Byte, short, Short, int, Integer, long, Long,
 * float, Float, double, Double, BigDecimal, {@code java.util.Date}, etc.
 *
 * <p>To facilitate mapping between columns and fields that don't have matching names,
 * try using column aliases in the SQL statement like "select fname as first_name from customer".
 *
 * <p>For 'null' values read from the database, we will attempt to call the setter, but in the case of
 * Java primitives, this causes a TypeMismatchException. This class can be configured (using the
 * primitivesDefaultedForNullValue property) to trap this exception and use the primitives default value.
 * Be aware that if you use the values from the generated bean to update the database the primitive value
 * will have been set to the primitive's default value instead of null.
 *
 * <p>Please note that this class is designed to provide convenience rather than high performance.
 * For best performance, consider using a custom {@link org.springframework.jdbc.core.RowMapper} implementation.
 *
 * <p>
 * 注：本类使用了{@link org.springframework.jdbc.core.BeanPropertyRowMapper}
 * 源码，新增了通过 {@link com.nway.spring.jdbc.annotation.Column}自定义表字段与bean属性的映射
 *
 * @param <T> the result type
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @author zdtjss@163.com
 * @see DataClassRowMapper
 * @since 2.5
 */
public class AsmRowMapper<T> extends DefaultRowMapper<T> {

    private static final Log log = LogFactory.getLog(AsmRowMapper.class);

    private RowMapper<T> beanAccess;

    private Map<String, Integer> columnIndexMap;

    /**
     * Create a new {@code BeanPropertyRowMapper}, accepting unpopulated
     * properties in the target bean.
     *
     * @param mappedClass the class that each row should be mapped to
     */
    public AsmRowMapper(Class<T> mappedClass, LinkedHashMap<String, Integer> columnIndexMap) {
        super(mappedClass);
        setColumnIndexMap(columnIndexMap);
        this.columnIndexMap = columnIndexMap;
        this.beanAccess = createBeanAccess(mappedClass, columnIndexMap);
    }

    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        return beanAccess.mapRow(rs);
    }

    private RowMapper<T> createBeanAccess(Class<T> type, LinkedHashMap<String, Integer> columnIndexMap) {

        ClassWriter classWriter = new ClassWriter(0);
        MethodVisitor methodVisitor;

        // "com/nway/spring/jdbc/performance/entity/Monitor"
        String beanClassName = type.getCanonicalName().replace('.', '/');
        String randomName = UUID.randomUUID().toString().replace("-", "");
        String className = beanClassName + columnIndexMap.hashCode() + "Mapper";

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, "Lcom/nway/spring/jdbc/bean/processor/RowMapper<L" + beanClassName + ";>;", "com/nway/spring/jdbc/bean/processor/RowMapper", null);

        classWriter.visitSource(type.getSimpleName() + columnIndexMap.hashCode() + "Mapper.java", null);

        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(9, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/nway/spring/jdbc/bean/processor/RowMapper", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "L" + className + ";", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "mapRow", "(Ljava/sql/ResultSet;)L" + beanClassName + ";", null, new String[]{"java/sql/SQLException"});
            methodVisitor.visitCode();


            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(14, label0);
            methodVisitor.visitTypeInsn(NEW, beanClassName);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, beanClassName, "<init>", "()V", false);
            methodVisitor.visitVarInsn(ASTORE, 2);

            int rowNum = 16;
            Label labelFirst = new Label();
            Map<String, ColumnInfo> fieldMap = SqlBuilderUtils.getEntityInfo(type).getColumnMap();
            Map<String, Field> columnMap = fieldMap.values().stream().collect(Collectors.toMap(ColumnInfo::getColumnName, ColumnInfo::getReadMethod));

            for (Map.Entry<String, Field> column : columnMap.entrySet()) {

                Integer colIdx = columnIndexMap.get(column.getKey());
                if (colIdx == null) {
                    continue;
                }

                Field field = column.getValue();
                Class<?> fieldType = field.getType();

                String localGetter = getLocalGetter(fieldType);

                Label label1 = colIdx == 1 ? labelFirst : new Label();
                methodVisitor.visitLabel(label1);
                methodVisitor.visitLineNumber(rowNum++, label1);
                methodVisitor.visitVarInsn(ALOAD, 2);
                if (localGetter != null) {
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    methodVisitor.visitVarInsn(ALOAD, 1);
                }
                else {
                    methodVisitor.visitVarInsn(ALOAD, 1);
                }

                switch (colIdx) {
                    case 1:
                        methodVisitor.visitInsn(ICONST_1);
                        break;
                    case 2:
                        methodVisitor.visitInsn(ICONST_2);
                        break;
                    case 3:
                        methodVisitor.visitInsn(ICONST_3);
                        break;
                    case 4:
                        methodVisitor.visitInsn(ICONST_4);
                        break;
                    case 5:
                        methodVisitor.visitInsn(ICONST_5);
                        break;
                    default:
                        methodVisitor.visitIntInsn(BIPUSH, colIdx);
                }
                if (localGetter != null) {
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, className, localGetter, "(Ljava/sql/ResultSet;I)" + getDescriptor(fieldType, true), false);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanClassName, getSetter(field), "(" + getDescriptor(fieldType, false) + ")V", false);
                }
                else if(fieldType == LocalDate.class) {
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getDate", "(I)Ljava/sql/Date;", true);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "com/nway/spring/jdbc/util/DateUtils", "toLocalDate", "(Ljava/sql/Date;)Ljava/time/LocalDate;", false);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanClassName, getSetter(field), "(Ljava/time/LocalDate;)V", false);
                }
                else if(fieldType == LocalDateTime.class) {
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getTimestamp", "(I)Ljava/sql/Timestamp;", true);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "com/nway/spring/jdbc/util/DateUtils", "toLocalDateTime", "(Ljava/sql/Timestamp;)Ljava/time/LocalDateTime;", false);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanClassName, getSetter(field), "(Ljava/time/LocalDateTime;)V", false);
                }
                else if(fieldType == LocalTime.class) {
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getTime", "(I)Ljava/sql/Time;", true);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "com/nway/spring/jdbc/util/DateUtils", "toLocalTime", "(Ljava/sql/Time;)Ljava/time/LocalTime;", false);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanClassName, getSetter(field), "(Ljava/time/LocalTime;)V", false);
                }
                else {
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "get" + fieldType.getSimpleName(), "(I)" + getDescriptor(fieldType, true), true);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanClassName, getSetter(field), "(" + getDescriptor(fieldType, false) + ")V", false);
                }
            }

            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLineNumber(rowNum, label5);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitInsn(ARETURN);
            Label label6 = new Label();
            methodVisitor.visitLabel(label6);
            methodVisitor.visitLocalVariable("this", "L" + className + ";", null, label0, label6, 0);
            methodVisitor.visitLocalVariable("rs", "Ljava/sql/ResultSet;", null, label0, label6, 1);
            methodVisitor.visitLocalVariable("monitor", "L" + beanClassName + ";", null, labelFirst, label6, 2);
            methodVisitor.visitMaxs(4, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "mapRow", "(Ljava/sql/ResultSet;)Ljava/lang/Object;", null, new String[]{"java/sql/SQLException"});
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(9, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, className, "mapRow", "(Ljava/sql/ResultSet;)L" + beanClassName + ";", false);
            methodVisitor.visitInsn(ARETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "L" + className + ";", null, label0, label1, 0);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        try {
            DynamicBeanClassLoader beanClassLoader = new DynamicBeanClassLoader(ClassUtils.getDefaultClassLoader(), "D:\\" + className);
            Class<?> processor = beanClassLoader.defineClass(className.replace('/', '.'), classWriter.toByteArray());
            return (RowMapper<T>) processor.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("使用ASM创建 [ " + className + " ] 失败", e);
        }
    }

    private String getLocalGetter(Class<?> clazz) {
        if (Integer.TYPE.equals(clazz)) {
            return "getPrimitiveInteger";
        } else if (Integer.class.equals(clazz)) {
            return "getInteger";
        } else if (Long.TYPE.equals(clazz)) {
            return "getPrimitiveLong";
        } else if (Long.class.equals(clazz)) {
            return "getLong";
        } else if (Float.TYPE.equals(clazz)) {
            return "getPrimitiveFloat";
        } else if (Float.class.equals(clazz)) {
            return "getFloat";
        } else if (Double.TYPE.equals(clazz)) {
            return "getPrimitiveDouble";
        } else if (Double.class.equals(clazz)) {
            return "getDouble";
        } else if (Short.TYPE.equals(clazz)) {
            return "getPrimitiveShort";
        } else if (Short.class.equals(clazz)) {
            return "getShort";
        } else if (Byte.TYPE.equals(clazz)) {
            return "getPrimitiveByte";
        } else if (Byte.class.equals(clazz)) {
            return "getByte";
        } else if (Boolean.TYPE.equals(clazz)) {
            return "getPrimitiveBoolean";
        } else if (Boolean.class.equals(clazz)) {
            return "getBoolean";
        }
        else if (byte[].class.equals(clazz)) {
            return "getByteArr";
        }
        return null;
    }

    /**
     * 实体类set方法的参数类型
     *
     * @param clazz
     * @return
     */
    private String getDescriptor(Class<?> clazz, boolean forRs) {
        if (Integer.TYPE.equals(clazz)) {
            return "I";
        } else if (Long.TYPE.equals(clazz)) {
            return "J";
        } else if (forRs && java.util.Date.class.equals(clazz)) {
            return "Ljava/sql/Date;";
        } else if (Float.TYPE.equals(clazz)) {
            return "F";
        } else if (Double.TYPE.equals(clazz)) {
            return "D";
        } else if (Boolean.TYPE.equals(clazz)) {
            return "Z";
        }  else if (Byte.TYPE.equals(clazz)) {
            return "B";
        } else if (Short.TYPE.equals(clazz)) {
            return "S";
        } else if (byte[].class.equals(clazz)) {
            return "[B";
        }
        return "L" + clazz.getName().replace('.', '/') + ";";
    }

    private String getSetter(Field field) {
        String fieldName = field.getName();
        if (field.getType() == Boolean.TYPE && fieldName.startsWith("is")) {
            return "set" + fieldName.substring(2);
        }
        return "set" + upperFirst(field.getName());
    }

    private String upperFirst(String name) {
        char[] chars = name.toCharArray();
        return Character.toUpperCase(chars[0]) + new String(chars, 1, chars.length - 1);
    }
}
