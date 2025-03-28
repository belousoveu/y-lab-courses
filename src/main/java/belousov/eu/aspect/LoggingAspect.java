package belousov.eu.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Slf4j
public class LoggingAspect {

    @After("@annotation(belousov.eu.annotation.Loggable) && execution(* belousov.eu.service.*.*(..))")
    public void loggingUserActivity(JoinPoint joinPoint) {
        log.info("Успешно выполнен метод {} с параметрами {}", joinPoint.getSignature(), joinPoint.getArgs());
    }
}
