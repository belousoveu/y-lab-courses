package belousov.eu.servlet;

import belousov.eu.annotation.AuthorizationRequired;
import belousov.eu.controller.ProfileController;
import belousov.eu.exception.ForbiddenException;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.exception.ValidationParametersException;
import belousov.eu.model.dto.UserProfileUpdateDto;
import belousov.eu.model.entity.User;
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
@AuthorizationRequired
public class ProfileServlet extends HttpServlet {

    private static final String CURRENT_USER = "currentUser";

    private final transient ProfileController profileController;
    private final ObjectMapper objectMapper;


    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String path = req.getPathInfo();

        if (path == null || path.isEmpty()) {
            throw new PathNotFoundException(path);
        }

        String[] parts = path.split("/");
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            if (parts.length > 3 && parts[3].equals("update")) {
                User currentUser = (User) session.getAttribute(CURRENT_USER);
                int pathId = Integer.parseInt(parts[2]);
                if (currentUser.getId() != pathId && !currentUser.isAdmin()) {
                    throw new ForbiddenException();
                }

                UserProfileUpdateDto updateDto = objectMapper.readValue(req.getInputStream(), UserProfileUpdateDto.class);

                if (!validatorFactory.getValidator().validate(updateDto).isEmpty()) {
                    throw new ValidationParametersException(
                            objectMapper.writeValueAsString(
                                    validatorFactory.getValidator().validate(updateDto)
                            ));
                }

                profileController.updateProfile(pathId, updateDto, currentUser);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        HttpSession session = req.getSession();
        String path = req.getPathInfo();

        if (path == null || path.isEmpty()) {
            throw new PathNotFoundException(path);
        }

        String[] parts = path.split("/");

        if (parts.length > 2) {
            User currentUser = (User) session.getAttribute(CURRENT_USER);
            int pathId = Integer.parseInt(parts[2]);
            if (currentUser.getId() != pathId && !currentUser.isAdmin()) {
                throw new ForbiddenException();
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
            throw new PathNotFoundException(path);
        }

        String[] parts = path.split("/");


        if (parts.length > 2) {
            User currentUser = (User) session.getAttribute(CURRENT_USER);
            int pathId = Integer.parseInt(parts[2]);
            if (currentUser.getId() != pathId && !currentUser.isAdmin()) {
                throw new ForbiddenException();
            }

            resp.setContentType("application/json");
            resp.getWriter().write(String.format("<h1>Добро пожаловать, %s!</h1>", currentUser.getName()));
            objectMapper.writeValue(resp.getWriter(), profileController.viewProfile(pathId));

        } else {
            throw new PathNotFoundException(path);
        }
    }
}
