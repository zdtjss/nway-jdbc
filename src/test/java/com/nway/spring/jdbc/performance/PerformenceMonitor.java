package com.nway.spring.jdbc.performance;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformenceMonitor {

	@Around("execution(* com.nway.spring.jdbc.performance.*Performance.*(..))")
	public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {

		long begin = System.nanoTime();

		Object retVal = pjp.proceed();

		System.out.println(String.format("%d     %s     %s", System.nanoTime() - begin,
				 pjp.getSignature().toShortString(), Thread.currentThread().getName()));

		return retVal;
	}
}
