package belousov.eu.servlet;

import belousov.eu.annotation.AuthorizationRequired;
import belousov.eu.controller.GoalController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.model.dto.GoalDto;
import belousov.eu.model.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AuthorizationRequired
@RequiredArgsConstructor
public class GoalServlet extends HttpServlet {

    private static final String CURRENT_USER = "currentUser";
    private static final String CONTENT_TYPE = "application/json";
    private static final Pattern PATTERN_GOAL = Pattern.compile("/goals/(\\d+)");
    private static final String PATH_GOAL = "/goals";

    private final transient GoalController goalController;
    private final ObjectMapper objectMapper;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        if (PATH_GOAL.equals(path)) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            if (user != null) {
                resp.setContentType(CONTENT_TYPE);
                resp.getWriter().write(objectMapper.writeValueAsString(goalController.getAllGoals(user.getId())));
                return;
            }
        }
        throw new PathNotFoundException(path);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (PATH_GOAL.equals(path)) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            GoalDto dto = objectMapper.readValue(req.getInputStream(), GoalDto.class);

            goalController.addGoal(user, dto);
            resp.sendRedirect(PATH_GOAL);
            resp.setContentType(CONTENT_TYPE);
            resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            return;

        }
        throw new PathNotFoundException(path);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Matcher matcher = PATTERN_GOAL.matcher(path);

        if (matcher.matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            int id = Integer.parseInt(matcher.group(1));
            goalController.editGoal(id, user, objectMapper.readValue(req.getInputStream(), GoalDto.class));
            resp.sendRedirect(PATH_GOAL);
            resp.setContentType(CONTENT_TYPE);
            resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            return;
        }
        throw new PathNotFoundException(path);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Matcher matcher = PATTERN_GOAL.matcher(path);

        if (matcher.matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            int id = Integer.parseInt(matcher.group(1));
            goalController.deleteGoal(id, user);
            resp.sendRedirect(PATH_GOAL);
            resp.setContentType(CONTENT_TYPE);
            resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            return;
        }
        throw new PathNotFoundException(path);
    }
}

