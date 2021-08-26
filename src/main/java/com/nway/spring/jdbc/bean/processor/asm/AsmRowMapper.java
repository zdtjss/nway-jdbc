package com.nway.spring.jdbc.bean.processor.asm;

import com.nway.spring.jdbc.bean.processor.BeanAccess;
import com.nway.spring.jdbc.bean.processor.DefaultRowMapper;
import org.objectweb.asm.*;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;


/**
 * {@link RowMapper} implementation that converts a row into a new instance
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
 * For best performance, consider using a custom {@link RowMapper} implementation.
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

    private BeanAccess beanAccess;

    /**
     * Create a new {@code BeanPropertyRowMapper}, accepting unpopulated
     * properties in the target bean.
     *
     * @param mappedClass the class that each row should be mapped to
     */
    public AsmRowMapper(Class<T> mappedClass) {
        super(mappedClass);
    }

    /**
     * Initialize the mapping meta-data for the given class.
     *
     * @param mappedClass the mapped class
     */
    @Override
    protected void initialize(Class<T> mappedClass) {
        super.initialize(mappedClass);
        this.beanAccess = createBeanAccess(mappedClass);
    }

    @Override
    protected T getBeanInstance() {
        return beanAccess.newInstance();
    }

    @Override
    protected void setVal(Object mappedObject, Field field, Object value) {
        beanAccess.setVal(field.getName(), value);
    }

    private BeanAccess createBeanAccess(Class<T> type) {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;

        // "com/nway/spring/jdbc/performance/entity/Monitor"
        String beanClassName = type.getCanonicalName().replace('.', '/');
        String className = beanClassName + "Access";

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, null, "java/lang/Object", new String[]{"com/nway/spring/jdbc/bean/processor/BeanAccess"});

        classWriter.visitSource(type.getSimpleName() + "Access.java", null);

        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE, "bean", "L" + beanClassName + ";", null, null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(8, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "L" + className + ";", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", "<T:Ljava/lang/Object;>()TT;", null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(13, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitTypeInsn(NEW, beanClassName);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, beanClassName, "<init>", "()V", false);
            methodVisitor.visitFieldInsn(PUTFIELD, className, "bean", "L" + beanClassName + ";");
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(14, label1);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, className, "bean", "L" + beanClassName + ";");
            methodVisitor.visitInsn(ARETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "L" + className + ";", null, label0, label2, 0);
            methodVisitor.visitMaxs(3, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setVal", "(Ljava/lang/String;Ljava/lang/Object;)V", null, null);
            methodVisitor.visitCode();

            Field[] fields = type.getDeclaredFields();
            Field field = fields[0];
            String fieldTypeStr = getClassName(field.getType());
            String descriptor = getDescriptor(field.getType());

            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(18, label0);
            methodVisitor.visitLdcInsn("id");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label1 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label1);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(19, label2);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, className, "bean", "L" + beanClassName + ";");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            if (field.getType().isPrimitive()) {
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, fieldTypeStr, getMethodName(field.getType()), "()" + descriptor, false);
            }
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanClassName, getSetter(field), "(" + (descriptor.startsWith("[") || field.getType().isPrimitive() ? descriptor : "L" + descriptor + ";") + ")V", false);

            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(21, label1);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

            int rowNum = 21;
            for (int i = 1; i < fields.length; i++) {

                field = fields[i];
                fieldTypeStr = getClassName(field.getType());
                descriptor = getDescriptor(field.getType());

                methodVisitor.visitLdcInsn(field.getName());
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
                Label label4 = new Label();
                methodVisitor.visitJumpInsn(IFEQ, label4);
                Label label5 = new Label();
                methodVisitor.visitLabel(label5);
                methodVisitor.visitLineNumber(rowNum++, label5);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitFieldInsn(GETFIELD, className, "bean", "L" + beanClassName + ";");
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitTypeInsn(CHECKCAST, fieldTypeStr);
                if (field.getType().isPrimitive()) {
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, fieldTypeStr, getMethodName(field.getType()), "()" + descriptor, false);
                }
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanClassName, getSetter(field), "(" + (descriptor.startsWith("[") || field.getType().isPrimitive() ? descriptor : "L" + descriptor + ";") + ")V", false);
                methodVisitor.visitJumpInsn(GOTO, label3);
                methodVisitor.visitLabel(label4);
                methodVisitor.visitLineNumber(rowNum++, label4);
                methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            }

            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(rowNum + 2, label3);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(RETURN);

            Label label15 = new Label();
            methodVisitor.visitLabel(label15);
            methodVisitor.visitLocalVariable("this", "L" + className + ";", null, label0, label15, 0);
            methodVisitor.visitLocalVariable("fieldName", "Ljava/lang/String;", null, label0, label15, 1);
            methodVisitor.visitLocalVariable("val", "Ljava/lang/Object;", null, label0, label15, 2);
            methodVisitor.visitMaxs(3, 3);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        try {
            DynamicBeanClassLoader beanClassLoader = new DynamicBeanClassLoader(ClassUtils.getDefaultClassLoader());
            Class<?> processor = beanClassLoader.defineClass(className.replace('/', '.'), classWriter.toByteArray());
            return (BeanAccess) processor.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("使用ASM创建 [ " + className + " ] 失败", e);
        }
    }

    private String toPrimitiveMethod(Class<?> clazz) {
        if (Integer.TYPE.equals(clazz) || Integer.class.equals(clazz)) {
            return "parseInt";
        } else if (Long.TYPE.equals(clazz) || Long.class.equals(clazz)) {
            return "parseLong";
        } else if (Boolean.TYPE.equals(clazz) || Boolean.class.equals(clazz)) {
            return "parseBoolean";
        } else if (Float.TYPE.equals(clazz) || Float.class.equals(clazz)) {
            return "parseFloat";
        } else if (Double.TYPE.equals(clazz) || Double.class.equals(clazz)) {
            return "parseDouble";
        } else if (Byte.TYPE.equals(clazz) || Byte.class.equals(clazz)) {
            return "parseByte";
        } else if (Short.TYPE.equals(clazz) || Short.class.equals(clazz)) {
            return "parseShort";
        }
        return "";
    }

    private String getMethodName(Class<?> clazz) {
        if (Integer.TYPE.equals(clazz) || Integer.class.equals(clazz)) {
            return "intValue";
        } else if (Long.TYPE.equals(clazz) || Long.class.equals(clazz)) {
            return "longValue";
        } else if (Boolean.TYPE.equals(clazz) || Boolean.class.equals(clazz)) {
            return "booleanValue";
        } else if (Float.TYPE.equals(clazz) || Float.class.equals(clazz)) {
            return "floatValue";
        } else if (Double.TYPE.equals(clazz) || Double.class.equals(clazz)) {
            return "doubleValue";
        } else if (Byte.TYPE.equals(clazz) || Byte.class.equals(clazz)) {
            return "byteValue";
        } else if (Short.TYPE.equals(clazz) || Short.class.equals(clazz)) {
            return "shortValue";
        }
        return "";
    }

    private String getClassName(Class<?> clazz) {
        if (Integer.TYPE.equals(clazz)) {
            return "java/lang/Integer";
        } else if (Long.TYPE.equals(clazz)) {
            return "java/lang/Long";
        } else if (Boolean.TYPE.equals(clazz)) {
            return "java/lang/Boolean";
        } else if (Float.TYPE.equals(clazz)) {
            return "java/lang/Float";
        } else if (Double.TYPE.equals(clazz)) {
            return "java/lang/DOUBLE";
        } else if (Byte.TYPE.equals(clazz)) {
            return "java/lang/Byte";
        } else if (Short.TYPE.equals(clazz)) {
            return "java/lang/Short";
        }
        return clazz.getName().replace('.', '/');
    }

    private String getDescriptor(Class<?> clazz) {
        if (Integer.TYPE.equals(clazz)) {
            return "I";
        } else if (Long.TYPE.equals(clazz)) {
            return "J";
        } else if (Boolean.TYPE.equals(clazz)) {
            return "Z";
        } else if (Float.TYPE.equals(clazz)) {
            return "F";
        } else if (Double.TYPE.equals(clazz)) {
            return "D";
        } else if (Byte.TYPE.equals(clazz)) {
            return "B";
        } else if (Short.TYPE.equals(clazz)) {
            return "S";
        }
        return clazz.getName().replace('.', '/');
    }

    private String getSetter(Field field) {
        if (field.getType().isPrimitive() && field.getType().equals(Boolean.class)) {
            return field.getName();
        }
        return "set" + upperFirst(field.getName());
    }

    private String upperFirst(String str) {
        char[] chars = str.toCharArray();
        return Character.toUpperCase(chars[0]) + new String(chars, 1, chars.length - 1);
    }
}
