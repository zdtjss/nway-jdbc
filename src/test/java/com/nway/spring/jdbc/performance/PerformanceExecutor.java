package com.nway.spring.jdbc.performance;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceExecutor extends ThreadPoolExecutor {

	private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
	private final Logger log = LoggerFactory.getLogger(PerformanceExecutor.class);
	private final AtomicLong numTasks = new AtomicLong();
	private final AtomicLong totalTime = new AtomicLong();

	public PerformanceExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		
		super.beforeExecute(t, r);
		
		log.info(String.format("%s: start %s", t, r));
		
		startTime.set(System.currentTimeMillis());
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {

		super.afterExecute(r, t);

		try 
		{
			long endTime = System.currentTimeMillis();
			long taskTime = endTime - startTime.get();

			numTasks.incrementAndGet();
			totalTime.addAndGet(taskTime);
			
			log.info(String.format("%s: end %s,time = %dns", t, r, taskTime));
		}
		finally
		{
			super.afterExecute(r, t);
		}
	}

	@Override
	protected void terminated() {

		try 
		{
			log.info(String.format("Terminated: avg time = %dns", totalTime.get() / numTasks.get()));
		}
		finally
		{
			super.terminated();
		}
	}

}
