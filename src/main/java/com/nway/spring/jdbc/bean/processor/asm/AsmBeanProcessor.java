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
            fieldVisitor = classWriter.visitField(ACC_PRIVATE, "bean", "L“+beanClassName+”;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(5, label0);
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
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "newInstance", "(Ljava/lang/String;)Ljava/lang/Object;", "<T:Ljava/lang/Object;>(Ljava/lang/String;)TT;", null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(11, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitTypeInsn(NEW, beanClassName);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, beanClassName, "<init>", "()V", false);
            methodVisitor.visitFieldInsn(PUTFIELD, className, "bean", "L" + beanClassName + ";");
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(12, label1);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, className, "bean", "L" + beanClassName + ";");
            methodVisitor.visitInsn(ARETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "L" + className + ";", null, label0, label2, 0);
            methodVisitor.visitLocalVariable("className", "Ljava/lang/String;", null, label0, label2, 1);
            methodVisitor.visitMaxs(3, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setVal", "(Ljava/lang/String;Ljava/lang/Object;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);

            Field[] declaredFields = type.getDeclaredFields();

            Field firstField = declaredFields[0];

            methodVisitor.visitLineNumber(16, label0);
            methodVisitor.visitLdcInsn(firstField.getName());
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);

            Label label1 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label1);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(17, label2);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, className, "bean", "L“+beanClassName+”;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            String fieldType = firstField.getType().getName().replace('.', '/');
            methodVisitor.visitTypeInsn(CHECKCAST, fieldType);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanClassName, getSetter(firstField), "(L" + fieldType + ";)V", false);

            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(18, label1);

            int rowNum = 18;
            for (int i = 1; i < declaredFields.length; i++) {

                Field field = declaredFields[i];

                methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                methodVisitor.visitLdcInsn(field.getName());
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
                Label label4 = new Label();
                methodVisitor.visitJumpInsn(IFEQ, label4);
                Label label5 = new Label();
                methodVisitor.visitLabel(label5);
                methodVisitor.visitLineNumber(++rowNum, label5);
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitFieldInsn(GETFIELD, className, "bean", "L" + beanClassName + ";");
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitTypeInsn(CHECKCAST, fieldType);
//                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanClassName, getSetter(firstField), "(L" + fieldType + ";)V", false);
                methodVisitor.visitJumpInsn(GOTO, label3);
                methodVisitor.visitLabel(label4);
                methodVisitor.visitLineNumber((rowNum = rowNum + 2), label4);
            }

            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            methodVisitor.visitLdcInsn("\u5c5e\u6027");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitLdcInsn("\u4e0d\u5b58\u5728");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
            methodVisitor.visitInsn(ATHROW);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber((rowNum = rowNum + 2), label3);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(RETURN);
            Label label6 = new Label();
            methodVisitor.visitLabel(label6);
            methodVisitor.visitLocalVariable("this", "L" + className + ";", null, label0, label6, 0);
            methodVisitor.visitLocalVariable("fieldName", "Ljava/lang/String;", null, label0, label6, 1);
            methodVisitor.visitLocalVariable("val", "Ljava/lang/Object;", null, label0, label6, 2);
            methodVisitor.visitMaxs(4, 3);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        try {
            DynamicBeanClassLoader beanClassLoader = new DynamicBeanClassLoader(ClassUtils.getDefaultClassLoader(), "E:\\"+type.getName() + "Access.class");
            Class<?> processor = beanClassLoader.defineClass(className, classWriter.toByteArray());
            return (BeanAccess) processor.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("使用ASM创建 [ " + className + " ] 失败", e);
        }
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
