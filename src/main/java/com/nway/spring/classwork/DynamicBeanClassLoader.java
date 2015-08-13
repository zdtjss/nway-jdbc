/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nway.spring.classwork;

import java.io.File;
import java.io.IOException;

import org.springframework.util.FileCopyUtils;

/**
 * 动态类加载器
 * 
 * @author zdtjss@163.com
 * 
 * @since 2014-1-13
 */
public class DynamicBeanClassLoader extends ClassLoader {

	private String fileName;

	/**
	 * 
	 * @param classLoader 上级 ClassLoader
	 */
	public DynamicBeanClassLoader(ClassLoader classLoader) {

		super(classLoader);
	}

	/**
	 * 
	 * 
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
		
		return super.defineClass(name, classContent, 0, classContent.length);
	}

	private void write(byte[] b, String filePath) throws IOException {

		File file = new File(filePath);
		File parentFile = file.getParentFile();

		if (!parentFile.exists()) {

			boolean isScuess = parentFile.mkdirs();

			if (!isScuess) {
				throw new IOException("无法创建文件 " + parentFile.getAbsolutePath());
			}

		}

		FileCopyUtils.copy(b, file);
	}
}
