package belousov.eu.aspect;

import belousov.eu.exception.ForbiddenException;
import belousov.eu.exception.UnAuthorizedAccessException;
import belousov.eu.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
@Slf4j
public class SecurityAspect {

    private static final String CURRENT_USER = "currentUser";

    @Before("@within(belousov.eu.annotation.AuthorizationRequired) && execution(* belousov.eu.servlet.*.*(..))")
    public void checkAuthorization(JoinPoint joinPoint) {
        if (joinPoint.getArgs().length == 0) {
            return;
        }
        HttpServletRequest req = (HttpServletRequest) joinPoint.getArgs()[0];
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(CURRENT_USER);
        if (user == null) {
            log.warn("Попытка неавторизованного доступа к методу: {}", joinPoint.getSignature());

            throw new UnAuthorizedAccessException();
        }
    }

    @Before("@within(belousov.eu.annotation.AdminAccessRequired) && execution(* belousov.eu.servlet.*.*(..))")
    public void checkAdminAuthorization(JoinPoint joinPoint) {
        if (joinPoint.getArgs().length == 0) {
            return;
        }
        HttpServletRequest req = (HttpServletRequest) joinPoint.getArgs()[0];
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(CURRENT_USER);
        if (user == null || !user.isAdmin()) {
            log.warn("Попытка доступа не администратора к методу: {}", joinPoint.getSignature());

            throw new ForbiddenException();
        }
    }

}
