package belousov.eu.servlet;

import belousov.eu.controller.ProfileController;
import belousov.eu.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class ProfileServlet extends HttpServlet {

    private final transient ProfileController profileController;
    private final transient ObjectMapper objectMapper;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession();

        String path = req.getPathInfo();
        if (path == null || path.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String[] parts = path.split("/");


        if (parts.length > 1) {
            User currentUser = (User) session.getAttribute("currentUser");
            int pathId = Integer.parseInt(parts[1]);
            if (currentUser.getId() != pathId && !currentUser.isAdmin()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            resp.setContentType("application/json");
            resp.getWriter().write(String.format("<h1>Добро пожаловать, %s!</h1>", currentUser.getName()));
            objectMapper.writeValue(resp.getWriter(), profileController.viewProfile(pathId));


        }


    }
}
