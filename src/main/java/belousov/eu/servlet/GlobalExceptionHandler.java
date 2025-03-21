package belousov.eu.servlet;

import belousov.eu.exception.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GlobalExceptionHandler {

    private final Map<Class<? extends Throwable>, Integer> statusCodes = new HashMap<>();

    public GlobalExceptionHandler() {
        statusCodes.put(UserNotFoundException.class, HttpServletResponse.SC_NOT_FOUND);
        statusCodes.put(InvalidPasswordException.class, HttpServletResponse.SC_UNAUTHORIZED);
        statusCodes.put(ForbiddenException.class, HttpServletResponse.SC_FORBIDDEN);
        statusCodes.put(EmailAlreadyExistsException.class, HttpServletResponse.SC_CONFLICT);
        statusCodes.put(BudgetNotFoundException.class, HttpServletResponse.SC_NOT_FOUND);
        statusCodes.put(CategoryNotFoundException.class, HttpServletResponse.SC_NOT_FOUND);
        statusCodes.put(GoalNotFoundException.class, HttpServletResponse.SC_NOT_FOUND);
        statusCodes.put(ValidationParametersException.class, HttpServletResponse.SC_BAD_REQUEST);
        statusCodes.put(TransactionNotFoundException.class, HttpServletResponse.SC_NOT_FOUND);
        statusCodes.put(PathNotFoundException.class, HttpServletResponse.SC_NOT_FOUND);
        statusCodes.put(UserWasBlockedException.class, HttpServletResponse.SC_FORBIDDEN);
        statusCodes.put(IOException.class, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        statusCodes.put(JsonProcessingException.class, HttpServletResponse.SC_BAD_REQUEST);
        statusCodes.put(NullPointerException.class, HttpServletResponse.SC_BAD_REQUEST);
        statusCodes.put(IllegalArgumentException.class, HttpServletResponse.SC_BAD_REQUEST);
        statusCodes.put(IllegalStateException.class, HttpServletResponse.SC_CONFLICT);
    }

    public void handle(Throwable e, HttpServletResponse resp) {
        int statusCode = statusCodes.getOrDefault(e.getClass(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.setStatus(statusCode);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        try {
            resp.getWriter().println("Error: " + e.getMessage());
        } catch (IOException ex) {
            log.error("Failed to write error response: {}", ex.getMessage());
        }
    }
}
