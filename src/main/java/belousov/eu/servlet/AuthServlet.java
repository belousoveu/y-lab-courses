package belousov.eu.servlet;

import belousov.eu.controller.AuthController;
import belousov.eu.exception.InvalidPasswordException;
import belousov.eu.model.User;
import belousov.eu.model.dto.LoginDto;
import belousov.eu.model.dto.RegisterDto;
import belousov.eu.model.dto.Validatable;
import com.fasterxml.jackson.core.JsonProcessingException;
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
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String[] parts = path.split("/");

        HttpSession session = request.getSession();
        Set<ConstraintViolation<Validatable>> violations;

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            if (parts.length > 1) {
                switch (parts[1]) {
                    case "login":
                        LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

                        violations = validator.validate(loginDto);

                        if (!violations.isEmpty()) {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.getWriter().write("Указаны неверные данные: " + violations);
                            return;
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
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            response.getWriter().write("Указаны неверные данные: " + violations);
                            return;
                        }
                        User newUser = authController.register(registerDto);
                        session.setAttribute("currentUser", newUser);

                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                        response.sendRedirect("/api/profile" + newUser.getId());
                        break;
                    case "logout":
                        session.invalidate();
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                        response.sendRedirect("/api/auth/login");
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                }
            }
        } catch (JsonProcessingException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) {
            response.setStatus(416);
        } catch (InvalidPasswordException e) {
            response.setStatus(401);
            response.getWriter().write("Invalid Password");
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String path = req.getPathInfo();
        if (path == null || path.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String[] parts = path.split("/");

        if (parts.length > 1 && parts[1].equals("login")) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("<h1>Авторизуйтесь или зарегистрируйтесь</h1>");
        }

    }
}
