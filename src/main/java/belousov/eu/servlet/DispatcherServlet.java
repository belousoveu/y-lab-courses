package belousov.eu.servlet;

import belousov.eu.config.DependencyContainer;
import belousov.eu.exception.ServletInitializationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class DispatcherServlet extends HttpServlet {

    private final DependencyContainer container;
    private final Map<String, HttpServlet> handlers = new HashMap<>();
    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Override
    public void init() {
        handlers.put("profile", container.get(ProfileServlet.class));
        handlers.put("auth", container.get(AuthServlet.class));
        handlers.put("admin", container.get(AdminServlet.class));
        handlers.put("goals", container.get(GoalServlet.class));
        handlers.put("categories", container.get(CategoryServlet.class));
        handlers.put("budgets", container.get(BudgetServlet.class));


        for (HttpServlet handler : handlers.values()) {
            try {
                handler.init();
            } catch (ServletException e) {
                throw new ServletInitializationException(e.getMessage());
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {

        try {
            String path = req.getPathInfo();
            if (path == null || path.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String[] parts = path.split("/");
            HttpServlet handler = handlers.get(parts[1]);

            if (handler != null) {
                handler.service(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            exceptionHandler.handle(e, resp);
        }
    }
}
