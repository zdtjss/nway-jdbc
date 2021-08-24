package com.nway.spring.jdbc;

import com.nway.spring.jdbc.bean.processor.asm.BeanAccess;
import com.nway.spring.jdbc.bean.processor.asm.DynamicBeanClassLoader;
import org.objectweb.asm.*;
import org.springframework.util.ClassUtils;

public class MonitorAccessDump implements Opcodes {

    public static void main(String[] args) {
        try {
            DynamicBeanClassLoader beanClassLoader = new DynamicBeanClassLoader(ClassUtils.getDefaultClassLoader(), "D:\\com\\nway\\spring\\jdbc\\MonitorAccess");
            Class<?> processor = beanClassLoader.defineClass("com.nway.spring.jdbc.MonitorAccess", dump());
            BeanAccess beanAccess = (BeanAccess) processor.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("使用ASM创建 [ className ] 失败", e);
        }
    }

    public static byte[] dump() throws Exception {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/nway/spring/jdbc/MonitorAccess", "Ljava/lang/Object;Lcom/nway/spring/jdbc/bean/processor/asm/BeanAccess<Lcom/nway/spring/jdbc/performance/entity/Monitor;>;", "java/lang/Object", new String[]{"com/nway/spring/jdbc/bean/processor/asm/BeanAccess"});

        classWriter.visitSource("MonitorAccess.java", null);

        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE, "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(12, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(13, label1);
            methodVisitor.visitInsn(RETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "Lcom/nway/spring/jdbc/MonitorAccess;", null, label0, label2, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "newInstance", "()Lcom/nway/spring/jdbc/performance/entity/Monitor;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(16, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitTypeInsn(NEW, "com/nway/spring/jdbc/performance/entity/Monitor");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/nway/spring/jdbc/performance/entity/Monitor", "<init>", "()V", false);
            methodVisitor.visitFieldInsn(PUTFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(17, label1);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitInsn(ARETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "Lcom/nway/spring/jdbc/MonitorAccess;", null, label0, label2, 0);
            methodVisitor.visitMaxs(3, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setVal", "(Ljava/lang/String;Ljava/lang/Object;)V", null, null);
            methodVisitor.visitCode();

            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(21, label0);
            methodVisitor.visitLdcInsn("id");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label1 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label1);

            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(22, label2);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "setId", "(I)V", false);
            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(23, label1);

            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

            methodVisitor.visitLdcInsn("brand");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label4 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label4);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLineNumber(24, label5);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "setBrand", "(Ljava/lang/String;)V", false);
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(25, label4);

            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitLdcInsn("model");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label6 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label6);
            Label label7 = new Label();
            methodVisitor.visitLabel(label7);
            methodVisitor.visitLineNumber(26, label7);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "setModel", "(Ljava/lang/String;)V", false);
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label6);
            methodVisitor.visitLineNumber(27, label6);

            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitLdcInsn("price");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label8 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label8);
            Label label9 = new Label();
            methodVisitor.visitLabel(label9);
            methodVisitor.visitLineNumber(28, label9);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Float");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "setPrice", "(F)V", false);
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label8);
            methodVisitor.visitLineNumber(29, label8);

            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitLdcInsn("type");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label10 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label10);
            Label label11 = new Label();
            methodVisitor.visitLabel(label11);
            methodVisitor.visitLineNumber(30, label11);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "setType", "(I)V", false);
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label10);
            methodVisitor.visitLineNumber(31, label10);

            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitLdcInsn("maxResolution");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            methodVisitor.visitJumpInsn(IFEQ, label3);
            Label label12 = new Label();
            methodVisitor.visitLabel(label12);
            methodVisitor.visitLineNumber(32, label12);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "setMaxResolution", "(Ljava/lang/String;)V", false);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(34, label3);

            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(RETURN);
            Label label13 = new Label();
            methodVisitor.visitLabel(label13);
            methodVisitor.visitLocalVariable("this", "Lcom/nway/spring/jdbc/MonitorAccess;", null, label0, label13, 0);
            methodVisitor.visitLocalVariable("fieldName", "Ljava/lang/String;", null, label0, label13, 1);
            methodVisitor.visitLocalVariable("val", "Ljava/lang/Object;", null, label0, label13, 2);
            methodVisitor.visitMaxs(2, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "newInstance", "()Ljava/lang/Object;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(8, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/MonitorAccess", "newInstance", "()Lcom/nway/spring/jdbc/performance/entity/Monitor;", false);
            methodVisitor.visitInsn(ARETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "Lcom/nway/spring/jdbc/MonitorAccess;", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
