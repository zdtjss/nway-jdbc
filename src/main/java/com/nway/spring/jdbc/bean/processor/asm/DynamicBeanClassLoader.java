package com.nway.spring.jdbc.bean.processor.asm;

import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

public class DynamicBeanClassLoader extends ClassLoader {

    /**
     * Shared singleton instance for runtime ASM class generation (no file output).
     * Thread-safe: defineClass is synchronized.
     */
    private static volatile DynamicBeanClassLoader INSTANCE;

    private String fileName;

    /**
     * Get the shared singleton instance for ASM mapper class loading.
     * Uses double-checked locking for thread-safe lazy initialization.
     */
    public static DynamicBeanClassLoader getInstance() {
        if (INSTANCE == null) {
            synchronized (DynamicBeanClassLoader.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DynamicBeanClassLoader(ClassUtils.getDefaultClassLoader());
                }
            }
        }
        return INSTANCE;
    }

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
     * Thread-safe: synchronized to prevent concurrent defineClass issues within the same ClassLoader.
     *
     * @param name
     * @param classContent
     * @return Class 实例,如果设置了保存路径，而保存失败，则返回null
     */
    public synchronized Class<?> defineClass(String name, byte[] classContent) throws IOException {

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