package belousov.eu.servlet;

import belousov.eu.controller.AdminController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.UserDto;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServletTest {

    @Mock
    private AdminController adminController;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AdminServlet adminServlet;

    ObjectMapper realMapper;

    private User admin;
    private UserDto userAdminDto, userDto;
    private TransactionDto transactionDto;

    @BeforeEach
    void setUp() {
        adminServlet.init(); // Initialize handlers
        userAdminDto = new UserDto(1, "admin", "admin@Admin.com", "ADMIN", true);
        userDto = new UserDto(2, "user", "user@gmail.com", "USER", true);
        admin = new User(1, "admin", "admin@Admin.com", "", Role.ADMIN, true);
        transactionDto = new TransactionDto(1, LocalDate.of(2025, 2, 5), "DEPOSIT", null, 1000, "Salary", 2);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(admin);

        realMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void doGet_GetAllUsers_ReturnsUsersList() throws Exception {
        when(request.getPathInfo()).thenReturn("/admin/users");
        when(adminController.getUsers()).thenReturn(List.of(userAdminDto, userDto));
        when(objectMapper.writeValueAsString(any())).thenAnswer(invocation ->
                realMapper.writeValueAsString(invocation.getArgument(0)));

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        adminServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        verify(adminController).getUsers();
    }

    @Test
    void doGet_GetAllTransactions_ReturnsTransactionsList() throws Exception {
        when(request.getPathInfo()).thenReturn("/admin/transactions");
        when(adminController.getTransactions()).thenReturn(List.of(transactionDto));
        when(objectMapper.writeValueAsString(any())).thenAnswer(invocation ->
                realMapper.writeValueAsString(invocation.getArgument(0)));

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        adminServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");
        verify(adminController).getTransactions();
    }

    @Test
    void doGet_BlockUser_BlocksAndRedirectsIfSelf() throws Exception {
        when(request.getPathInfo()).thenReturn("/admin/users/1/block");

        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);

        adminServlet.doGet(request, response);

        verify(adminController).blockUser(1);
        verify(session).invalidate();
        verify(response).sendRedirect(redirectCaptor.capture());
        assertEquals("/api/auth/login", redirectCaptor.getValue());
        verify(response).setStatus(HttpServletResponse.SC_SEE_OTHER);
    }

    @Test
    void doPut_SetUserRole_UpdatesRole() {
        when(request.getPathInfo()).thenReturn("/admin/users/2");
        when(request.getQueryString()).thenReturn("role=ADMIN");

        adminServlet.doPut(request, response);

        verify(adminController).setRole(2, Role.ADMIN);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDelete_DeleteUser_InvalidatesSessionIfSelf() throws Exception {
        when(request.getPathInfo()).thenReturn("/admin/users/1");

        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);

        adminServlet.doDelete(request, response);

        verify(adminController).deleteUser(1, admin);
        verify(session).invalidate();
        verify(response).sendRedirect(redirectCaptor.capture());
        assertEquals("/api/auth/login", redirectCaptor.getValue());
        verify(response).setStatus(HttpServletResponse.SC_SEE_OTHER);
    }

    @Test
    void doGet_InvalidPath_ThrowsPathNotFoundException() {
        when(request.getPathInfo()).thenReturn("/invalid/path");

        assertThrows(PathNotFoundException.class, () -> adminServlet.doGet(request, response));
    }

    @Test
    void doGet_UnblockUser_UnblocksUser() throws Exception {
        when(request.getPathInfo()).thenReturn("/admin/users/2/unblock");

        adminServlet.doGet(request, response);

        verify(adminController).unblockUser(2);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDelete_DeleteOtherUser_ReturnsNoContent() throws Exception {
        when(request.getPathInfo()).thenReturn("/admin/users/2");

        adminServlet.doDelete(request, response);

        verify(adminController).deleteUser(2, admin);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
        verify(session, never()).invalidate();
    }
}