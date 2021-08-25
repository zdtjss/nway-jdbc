package com.nway.spring.jdbc.bean.processor.asm;

import com.nway.spring.jdbc.bean.processor.BeanProcessor;
import org.objectweb.asm.*;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class AsmBeanProcessor implements BeanProcessor {

    @Override
    public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
        return null;
    }

    @Override
    public <T> List<T> toBeanList(ResultSet rs, Class<T> type) throws SQLException {
        return null;
    }

    public <T> BeanAccess dump(Class<T> type) throws Exception {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;

        // "com/nway/spring/jdbc/performance/entity/Monitor"
        String beanClassName = type.getCanonicalName().replace('.', '/');
        String className = beanClassName + "Access";

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, null, "java/lang/Object", new String[]{"com/nway/spring/jdbc/bean/processor/asm/BeanAccess"});

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
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanClassName, "setId", "(I)V", false);
            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(20, label1);

            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitLdcInsn("productionDate");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            methodVisitor.visitJumpInsn(IFEQ, label3);
            Label label14 = new Label();
            methodVisitor.visitLabel(label14);
            methodVisitor.visitLineNumber(31, label14);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, className, "bean", "L" + beanClassName + ";");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/util/Date");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanClassName, "setProductionDate", "(Ljava/util/Date;)V", false);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(33, label3);

            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(RETURN);
            Label label15 = new Label();
            methodVisitor.visitLabel(label15);
            methodVisitor.visitLocalVariable("this", "L" + className + ";", null, label0, label15, 0);
            methodVisitor.visitLocalVariable("fieldName", "Ljava/lang/String;", null, label0, label15, 1);
            methodVisitor.visitLocalVariable("val", "Ljava/lang/Object;", null, label0, label15, 2);
            methodVisitor.visitMaxs(2, 3);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        try {
            DynamicBeanClassLoader beanClassLoader = new DynamicBeanClassLoader(ClassUtils.getDefaultClassLoader(), "D:\\" + className);
            Class<?> processor = beanClassLoader.defineClass(className.replace('/', '.'), classWriter.toByteArray());
            return (BeanAccess) processor.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("使用ASM创建 [ " + className + " ] 失败", e);
        }
    }

    private String getMethodName(Class<?> clazz) {
        if (Integer.class.equals(clazz) || Integer.TYPE.equals(clazz)) {
            return "intValue";
        } else if (Float.class.equals(clazz)) {
            return "floatValue";
        }
        return "";
    }

    private String getClassName(Class<?> clazz) {
        if (Integer.TYPE.equals(clazz)) {
            return "java/lang/Integer";
        } else if (Float.TYPE.equals(clazz)) {
            return "java/lang/Float";
        } else if (Double.TYPE.equals(clazz)) {
            return "java/lang/DOUBLE";
        }
        return clazz.getName().replace('.', '/');
    }

    private String getDescriptor(Class<?> clazz) {
        if (Integer.class.equals(clazz) || Integer.TYPE.equals(clazz)) {
            return "I";
        } else if (Float.class.equals(clazz) || Float.TYPE.equals(clazz)) {
            return "F";
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
