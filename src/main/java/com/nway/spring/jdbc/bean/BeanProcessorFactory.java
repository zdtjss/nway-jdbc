package com.nway.spring.jdbc.bean;

public class BeanProcessorFactory {

	private static BeanProcessorFactory factory = new BeanProcessorFactory();

	private BeanProcessor beanProcessor;

	private BeanProcessorFactory() {

		beanProcessor = new DefaultBeanProcessor();
	}

	public static BeanProcessor getBeanProcessor() {

		return factory.beanProcessor;
	}
}
