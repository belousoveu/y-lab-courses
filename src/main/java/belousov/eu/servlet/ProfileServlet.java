package belousov.eu.servlet;

import belousov.eu.controller.ProfileController;
import belousov.eu.model.User;
import belousov.eu.model.dto.UserProfileUpdateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class ProfileServlet extends HttpServlet {

    private final transient ProfileController profileController;
    private final transient ObjectMapper objectMapper;


    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String path = req.getPathInfo();

        if (path == null || path.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String[] parts = path.split("/");
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            if (parts.length > 2 && parts[2].equals("update")) {
                User currentUser = (User) session.getAttribute("currentUser");
                int pathId = Integer.parseInt(parts[1]);
                if (currentUser.getId() != pathId && !currentUser.isAdmin()) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                UserProfileUpdateDto updateDto = objectMapper.readValue(req.getInputStream(), UserProfileUpdateDto.class);

                if (!validatorFactory.getValidator().validate(updateDto).isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                profileController.updateProfile(pathId, updateDto);
            }
        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

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
            String password = objectMapper.readValue(req.getInputStream(), String.class);
            profileController.deleteProfile(pathId, password, currentUser);
            resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
            resp.sendRedirect("/api/auth/login");
            session.invalidate();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

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
