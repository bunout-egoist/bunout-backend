package dough.global.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class TimeTraceAspect {

    @Around("@annotation(dough.global.annotation.TimeTrace)")
    public Object doTrace(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        String target = joinPoint.getSignature().toShortString();

        log.info("Method Time: {} , {} ms", target, resultTime);

        return result;
    }
}