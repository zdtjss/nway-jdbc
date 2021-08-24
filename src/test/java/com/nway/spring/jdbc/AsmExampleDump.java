package com.nway.spring.jdbc;

import org.objectweb.asm.*;

public class AsmExampleDump implements Opcodes {

    public static byte[] dump() throws Exception {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/nway/spring/jdbc/AsmExample", null, "java/lang/Object", new String[]{"com/nway/spring/jdbc/BeanAccess"});

        classWriter.visitSource("AsmExample.java", null);

        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE, "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;", null, null);
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
            methodVisitor.visitLocalVariable("this", "Lcom/nway/spring/jdbc/AsmExample;", null, label0, label1, 0);
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
            methodVisitor.visitTypeInsn(NEW, "com/nway/spring/jdbc/performance/entity/Monitor");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/nway/spring/jdbc/performance/entity/Monitor", "<init>", "()V", false);
            methodVisitor.visitFieldInsn(PUTFIELD, "com/nway/spring/jdbc/AsmExample", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(12, label1);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/AsmExample", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitInsn(ARETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "Lcom/nway/spring/jdbc/AsmExample;", null, label0, label2, 0);
            methodVisitor.visitLocalVariable("className", "Ljava/lang/String;", null, label0, label2, 1);
            methodVisitor.visitMaxs(3, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setVal", "(Ljava/lang/String;Ljava/lang/Object;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(16, label0);
            methodVisitor.visitLdcInsn("brand");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label1 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label1);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(17, label2);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/AsmExample", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "setBrand", "(Ljava/lang/String;)V", false);
            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(18, label1);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitLdcInsn("price");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label4 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label4);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLineNumber(19, label5);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/AsmExample", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Float");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "setPrice", "(F)V", false);
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(21, label4);
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
            methodVisitor.visitLineNumber(23, label3);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(RETURN);
            Label label6 = new Label();
            methodVisitor.visitLabel(label6);
            methodVisitor.visitLocalVariable("this", "Lcom/nway/spring/jdbc/AsmExample;", null, label0, label6, 0);
            methodVisitor.visitLocalVariable("fieldName", "Ljava/lang/String;", null, label0, label6, 1);
            methodVisitor.visitLocalVariable("val", "Ljava/lang/Object;", null, label0, label6, 2);
            methodVisitor.visitMaxs(4, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getVal", "(Ljava/lang/String;)Ljava/lang/Object;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(27, label0);
            methodVisitor.visitLdcInsn("brand");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label1 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label1);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(28, label2);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/AsmExample", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "getBrand", "()Ljava/lang/String;", false);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(29, label1);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitLdcInsn("price");
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label3 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label3);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(30, label4);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "com/nway/spring/jdbc/AsmExample", "bean", "Lcom/nway/spring/jdbc/performance/entity/Monitor;");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/nway/spring/jdbc/performance/entity/Monitor", "getPrice", "()F", false);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(32, label3);
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
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLocalVariable("this", "Lcom/nway/spring/jdbc/AsmExample;", null, label0, label5, 0);
            methodVisitor.visitLocalVariable("fieldName", "Ljava/lang/String;", null, label0, label5, 1);
            methodVisitor.visitMaxs(4, 2);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
