package belousov.eu.servlet;

import belousov.eu.controller.AuthController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.exception.ValidationParametersException;
import belousov.eu.model.dto.LoginDto;
import belousov.eu.model.dto.RegisterDto;
import belousov.eu.model.dto.Validatable;
import belousov.eu.model.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
public class AuthServlet extends HttpServlet {

    private final transient AuthController authController;
    private final ObjectMapper objectMapper;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String path = request.getPathInfo();
        if (path == null || path.isEmpty()) {
            throw new PathNotFoundException(path);
        }

        String[] parts = path.split("/");

        HttpSession session = request.getSession();
        Set<ConstraintViolation<Validatable>> violations;

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            if (parts.length > 2) {
                switch (parts[2]) {
                    case "login":
                        LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

                        violations = validator.validate(loginDto);

                        if (!violations.isEmpty()) {
                            throw new ValidationParametersException(violations.toString());
                        }
                        User authUser = authController.login(loginDto);
                        session.setAttribute("currentUser", authUser);

                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                        response.sendRedirect("/api/profile/" + authUser.getId());
                        break;

                    case "register":
                        RegisterDto registerDto = objectMapper.readValue(request.getInputStream(), RegisterDto.class);

                        violations = validator.validate(registerDto);

                        if (!violations.isEmpty()) {
                            throw new ValidationParametersException(violations.toString());
                        }
                        User newUser = authController.register(registerDto);
                        session.setAttribute("currentUser", newUser);

                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                        response.sendRedirect("/api/profile/" + newUser.getId());
                        break;

                    case "logout":
                        session.invalidate();
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                        response.sendRedirect("/api/auth/login");
                        break;
                    default:
                        throw new PathNotFoundException(path);

                }
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String path = req.getPathInfo();
        if (path == null || path.isEmpty()) {
            throw new PathNotFoundException(path);
        }

        String[] parts = path.split("/");

        if (parts.length > 2 && parts[2].equals("login")) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("<h1>Авторизуйтесь или зарегистрируйтесь</h1>");
        } else {
            throw new PathNotFoundException(path);
        }

    }
}
