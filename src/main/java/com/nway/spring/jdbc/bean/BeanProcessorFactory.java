package com.nway.spring.jdbc.bean;

import org.springframework.util.ClassUtils;

public class BeanProcessorFactory {

	private static BeanProcessorFactory factory = new BeanProcessorFactory();
	
	private BeanProcessor beanProcessor;

	private BeanProcessorFactory() {

		if (ClassUtils.isPresent("org.objectweb.asm.ClassWriter", ClassUtils.getDefaultClassLoader())) {

			beanProcessor = new AsmBeanProcessor();
		} 
		else if (ClassUtils.isPresent("javassist.ClassPool", ClassUtils.getDefaultClassLoader())) {

			beanProcessor = new JavassistBeanProcessor();
		} 
		else {
			
			beanProcessor = new SpringBeanProcessor();
		}
	}

	public static BeanProcessor getBeanProcessor() {

		return factory.beanProcessor;
	}
}
