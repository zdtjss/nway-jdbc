package com.nway.spring.jdbc;

import com.nway.spring.jdbc.bean.processor.BeanAccess;
import com.nway.spring.jdbc.bean.processor.asm.DynamicBeanClassLoader;
import com.nway.spring.jdbc.performance.entity.Monitor;
import org.objectweb.asm.*;
import org.springframework.util.ClassUtils;

public class MonitorAccessDump implements Opcodes {

    public static void main(String[] args) {
        try {
            DynamicBeanClassLoader beanClassLoader = new DynamicBeanClassLoader(ClassUtils.getDefaultClassLoader(), "D:\\com\\nway\\spring\\jdbc\\MonitorAccess");
            Class<?> processor = beanClassLoader.defineClass("com.nway.spring.jdbc.MonitorAccess", dump());
            BeanAccess beanAccess = (BeanAccess) processor.getConstructor().newInstance();
            Monitor o = (Monitor) beanAccess.newInstance();
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

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/nway/spring/jdbc/MonitorAccess", null, "java/lang/Object", new String[]{"com/nway/spring/jdbc/bean/processor/asm/BeanAccess"});

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
            methodVisitor.visitLineNumber(8, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "Lcom/nway/spring/jdbc/MonitorAccess;", null, label0, label1, 0);
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
            methodVisitor.visitTypeInsn(NEW, "com/nway/spring/jdbc/performance/entity/Monitor");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/nway/spring/jdbc/performance/entity/Monitor", "<init>", "()V", false);
            methodVisitor.visitFieldInsn(PUTFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(14, label1);
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
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "setId", "(Ljava/lang/Integer;)V", false);

            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(20, label1);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

            methodVisitor.visitLdcInsn("brand");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label4 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label4);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLineNumber(21, label5);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "setBrand", "(Ljava/lang/String;)V", false);
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(22, label4);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

            methodVisitor.visitLdcInsn("photo");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label6 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label6);
            Label label7 = new Label();
            methodVisitor.visitLabel(label7);
            methodVisitor.visitLineNumber(23, label7);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "[B");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "setPhoto", "([B)V", false);
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label6);
            methodVisitor.visitLineNumber(24, label6);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

           /* methodVisitor.visitLdcInsn("photo");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            methodVisitor.visitJumpInsn(IFEQ, label3);
            Label label8 = new Label();
            methodVisitor.visitLabel(label8);
            methodVisitor.visitLineNumber(25, label8);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/MonitorAccess", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "[B");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "setPhoto", "([B)V", false);
*/
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(27, label3);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

            methodVisitor.visitInsn(RETURN);

            Label label9 = new Label();
            methodVisitor.visitLabel(label9);
            methodVisitor.visitLocalVariable("this", "Lcom/nway/spring/jdbc/MonitorAccess;", null, label0, label9, 0);
            methodVisitor.visitLocalVariable("fieldName", "Ljava/lang/String;", null, label0, label9, 1);
            methodVisitor.visitLocalVariable("val", "Ljava/lang/Object;", null, label0, label9, 2);
            methodVisitor.visitMaxs(2, 3);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
