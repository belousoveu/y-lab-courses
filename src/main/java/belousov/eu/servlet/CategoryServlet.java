package belousov.eu.servlet;

import belousov.eu.annotation.AuthorizationRequired;
import belousov.eu.controller.CategoryController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.model.User;
import belousov.eu.model.dto.CategoryDto;
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
public class CategoryServlet extends HttpServlet {

    private static final String CURRENT_USER = "currentUser";
    private static final String CONTENT_TYPE = "application/json";
    private static final String PATH_CATEGORY = "/categories";
    private static final String FULL_PATH_CATEGORY = "/api/categories";
    private static final Pattern PATTERN_CATEGORY = Pattern.compile("/categories/(\\d+)");

    private final transient CategoryController categoryController;
    private final ObjectMapper objectMapper;

    @Override
    @AuthorizationRequired
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (PATH_CATEGORY.equals(path)) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);

            resp.setContentType(CONTENT_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(categoryController.getCategories(user)));
            return;
        }
        throw new PathNotFoundException(path);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (PATH_CATEGORY.equals(path)) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);

            CategoryDto categoryDto = objectMapper.readValue(req.getInputStream(), CategoryDto.class);
            categoryController.addCategory(user, categoryDto);
            resp.sendRedirect(FULL_PATH_CATEGORY);
            resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            return;
        }
        throw new PathNotFoundException(path);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Matcher matcher = PATTERN_CATEGORY.matcher(path);

        if (matcher.matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            int id = Integer.parseInt(matcher.group(1));
            categoryController.editCategory(id, user, objectMapper.readValue(req.getInputStream(), CategoryDto.class));
            resp.sendRedirect(FULL_PATH_CATEGORY);
            resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            return;
        }
        throw new PathNotFoundException(path);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Matcher matcher = PATTERN_CATEGORY.matcher(path);

        if (matcher.matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            int id = Integer.parseInt(matcher.group(1));
            categoryController.deleteCategory(id, user);
            resp.sendRedirect(FULL_PATH_CATEGORY);
            resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            return;
        }
        throw new PathNotFoundException(path);
    }
}

