package dev.ioexception.crawling.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TimeAspect {
    @Around("execution(* dev.ioexception.crawling.service.CrawlingService.getInflearn(..))")
    public Object crawlingTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object retVal = joinPoint.proceed();
        long end = System.currentTimeMillis();

        log.info("crawling method execution time: {} milliseconds.", end - start);

        return retVal;
    }

    @Around("execution(* dev.ioexception.crawling.service.IndexService.inputIndex(..))")
    public Object indexTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object retVal = joinPoint.proceed();
        long end = System.currentTimeMillis();

        log.info("index method execution time: {} milliseconds.", end - start);

        return retVal;
    }
}
