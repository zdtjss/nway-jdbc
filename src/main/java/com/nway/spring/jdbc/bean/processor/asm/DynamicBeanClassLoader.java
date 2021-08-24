package com.nway.spring.jdbc.bean.processor.asm;

import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

public class DynamicBeanClassLoader extends ClassLoader {

    private String fileName;

    /**
     * @param classLoader 上级 ClassLoader
     */
    public DynamicBeanClassLoader(ClassLoader classLoader) {

        super(classLoader);
    }

    /**
     * @param classLoader 上级 ClassLoader
     * @param fileName    class文件名 ( 建议指定路径信息 )
     */
    public DynamicBeanClassLoader(ClassLoader classLoader, String fileName) {

        super(classLoader);
        this.fileName = fileName;
    }

    /**
     * 将一个 byte 数组转换为 Class 类的实例
     * <p>
     *
     * @param name
     * @param classContent
     * @return Class 实例,如果设置了保存路径，而保存失败，则返回null
     */
    public Class<?> defineClass(String name, byte[] classContent) throws IOException {

        if (fileName != null) {
            write(classContent, fileName + ".class");
        }

        Class<?> classz = super.defineClass(name, classContent, 0, classContent.length);

        resolveClass(classz);

        return classz;
    }

    private void write(byte[] b, String filePath) throws IOException {

        File file = new File(filePath);
        File parentFile = file.getParentFile();

        if (!parentFile.exists()) {
            boolean isScc = parentFile.mkdirs();
            if (!isScc) {
                throw new IOException("无法创建文件 " + parentFile.getAbsolutePath());
            }
        }

        FileCopyUtils.copy(b, file);
    }
}