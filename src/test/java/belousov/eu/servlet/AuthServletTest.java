package belousov.eu.servlet;

import belousov.eu.controller.AuthController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.exception.ValidationParametersException;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.model.dto.LoginDto;
import belousov.eu.model.dto.RegisterDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServletTest {

    @Mock
    private AuthController authController;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthServlet authServlet;

    private User user;
    private ServletInputStream inputStream;

    private final StringWriter responseWriter = new StringWriter();

    @BeforeEach
    void setUp() {
        inputStream = mock(ServletInputStream.class);
        user = new User(1, "user", "user@gmail.com", "", Role.USER, true);

    }

    @Test
    void doPost_LoginSuccess() throws Exception {
        when(request.getPathInfo()).thenReturn("/auth/login");
        when(request.getInputStream()).thenReturn(inputStream);
        when(request.getSession()).thenReturn(session);

        LoginDto loginDto = new LoginDto("user@gmail.com", "User0123");

        when(objectMapper.readValue(inputStream, LoginDto.class)).thenReturn(loginDto);
        when(authController.login(loginDto)).thenReturn(user);

        authServlet.doPost(request, response);

        verify(session).setAttribute("currentUser", user);
        verify(response).setStatus(HttpServletResponse.SC_SEE_OTHER);
        verify(response).sendRedirect("/api/profile/1");
    }

    @Test
    void doPost_RegisterSuccess() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/auth/register");
        when(request.getInputStream()).thenReturn(inputStream);
        when(request.getSession()).thenReturn(session);

        RegisterDto registerDto = new RegisterDto("newUser", "newUser@gmail.com", "newUser0123");
        User newUser = new User(2, "newUser", "newUser@gmail.com", "", Role.USER, true);

        when(objectMapper.readValue(inputStream, RegisterDto.class)).thenReturn(registerDto);
        when(authController.register(registerDto)).thenReturn(newUser);

        authServlet.doPost(request, response);

        verify(session).setAttribute("currentUser", newUser);
        verify(response).sendRedirect("/api/profile/2");
    }

    @Test
    void doPost_LogoutSuccess() throws Exception {
        when(request.getPathInfo()).thenReturn("/auth/logout");
        when(request.getSession()).thenReturn(session);

        authServlet.doPost(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("/api/auth/login");
    }

    @Test
    void doPost_InvalidPathShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/auth/invalid");

        assertThrows(PathNotFoundException.class, () -> authServlet.doPost(request, response));
    }

    @Test
    void doPost_ValidationFailed() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/auth/login");
        when(request.getInputStream()).thenReturn(inputStream);

        LoginDto invalidDto = new LoginDto("", "");
        when(objectMapper.readValue(inputStream, LoginDto.class)).thenReturn(invalidDto);

        assertThrows(ValidationParametersException.class, () -> authServlet.doPost(request, response));
    }

    @Test
    void doGet_LoginPage() throws Exception {
        when(request.getPathInfo()).thenReturn("/auth/login");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        authServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("<h1>Авторизуйтесь или зарегистрируйтесь</h1>"));
    }

    @Test
    void doGet_InvalidPathShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/invalid");

        assertThrows(PathNotFoundException.class, () -> authServlet.doGet(request, response));
    }
}