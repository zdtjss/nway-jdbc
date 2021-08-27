package com.nway.spring.jdbc.performance;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceMonitor {

    @Around("execution(* com.nway.spring.jdbc.performance.*Performance.*(..))")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {

        long begin = System.currentTimeMillis();

        Object retVal = pjp.proceed();

        log.info("{}\t{}", System.currentTimeMillis() - begin, pjp.getSignature().toShortString());

        return retVal;
    }
}
