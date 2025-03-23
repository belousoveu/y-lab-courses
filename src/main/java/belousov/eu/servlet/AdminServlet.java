package belousov.eu.servlet;

import belousov.eu.annotation.AdminAccessRequired;
import belousov.eu.controller.AdminController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@AdminAccessRequired
public class AdminServlet extends HttpServlet {

    private static final String CURRENT_USER = "currentUser";
    private static final String CONTENT_TYPE = "application/json";
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final Pattern PATTERN_USER = Pattern.compile("^/admin/users/(\\d+)$");
    private static final Pattern PATTERN_ROLE = Pattern.compile("^/admin/users/(\\d+)(\\?role=([^&]+))?$");

    private final transient AdminController adminController;
    private final ObjectMapper objectMapper;

    private final Map<Pattern, RequestHandler> handlers = new HashMap<>();

    @Override
    public void init() {
        handlers.put(Pattern.compile("^/admin/users$"), this::getAllUsers);
        handlers.put(Pattern.compile("^/admin/transactions$"), this::getAllTransactions);
        handlers.put(Pattern.compile("^/admin/users/(\\d+)/block$"), this::handleBlockUser);
        handlers.put(Pattern.compile("^/admin/users/(\\d+)/unblock$"), this::handleUnblockUser);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String path = req.getPathInfo();
        String queryString = req.getQueryString();
        String fullPath = queryString != null ? path + "?" + queryString : path;

        for (Map.Entry<Pattern, RequestHandler> entry : handlers.entrySet()) {
            Matcher matcher = entry.getKey().matcher(fullPath);
            if (matcher.matches()) {
                entry.getValue().handle(req, resp, matcher);
                return;
            }
        }
        throw new PathNotFoundException(path);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getPathInfo();
        Matcher matcher = PATTERN_ROLE.matcher(path);
        if (matcher.matches()) {
            int userId = Integer.parseInt(matcher.group(1));
            String role = matcher.group(3);
            adminController.setRole(userId, Role.valueOf(role));
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        throw new PathNotFoundException(path);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Matcher matcher = PATTERN_USER.matcher(path);
        if (matcher.matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            int userId = Integer.parseInt(matcher.group(1));
            adminController.deleteUser(userId, user);
            if (user.getId() == userId) {
                session.invalidate();
                resp.sendRedirect(LOGIN_PATH);
                resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            throw new PathNotFoundException(path);
        }
    }

    private void getAllUsers(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) throws IOException {
        List<UserDto> users = adminController.getUsers();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(CONTENT_TYPE);
        resp.getWriter().write(objectMapper.writeValueAsString(users));
    }

    private void getAllTransactions(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) throws IOException {
        List<TransactionDto> transactions = adminController.getTransactions();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(CONTENT_TYPE);
        resp.getWriter().write(objectMapper.writeValueAsString(transactions));
    }

    private void handleBlockUser(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) throws IOException {
        int userId = Integer.parseInt(matcher.group(1));
        adminController.blockUser(userId);
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(CURRENT_USER);
        if (user.getId() == userId) {
            session.invalidate();
            resp.sendRedirect(LOGIN_PATH);
            resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
            return;
        }
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void handleUnblockUser(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) {
        int userId = Integer.parseInt(matcher.group(1));
        adminController.unblockUser(userId);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }


}
