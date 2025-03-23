package belousov.eu.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class BenchmarkAspect {

    @Around("@annotation(belousov.eu.annotation.Benchmark)")
    public Object benchmarkAdvice(ProceedingJoinPoint pjp, JoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        Object result = pjp.proceed();

        log.info("Выполнение метода {} заняло {}", joinPoint.getSignature(), System.currentTimeMillis() - startTime);

        return result;

    }
}
