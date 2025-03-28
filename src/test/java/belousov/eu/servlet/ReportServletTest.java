package belousov.eu.servlet;

import belousov.eu.controller.ReportController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.exception.RequiredArgumentException;
import belousov.eu.model.dto.BalanceDto;
import belousov.eu.model.dto.IncomeStatement;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServletTest {

    @Mock
    private ReportController reportController;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ReportServlet reportServlet;

    private final ObjectMapper realMapper = new ObjectMapper();


    private final StringWriter responseWriter = new StringWriter();
    private final User testUser = new User(1, "testUser", "testUser@gmail.com", "", Role.USER, true);

    @BeforeEach
    void setUp() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(testUser);
    }

    @Test
    void doGet_ShouldReturnBalance() throws Exception {
        when(request.getPathInfo()).thenReturn("/reports/1/balance");
        BalanceDto testBalance = new BalanceDto(LocalDate.now().toString(), testUser.getName(), 5000.0);
        when(reportController.getCurrentBalance(testUser)).thenReturn(testBalance);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(objectMapper.writeValueAsString(any(BalanceDto.class))).thenReturn(realMapper.writeValueAsString(testBalance));

        reportServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(objectMapper).writeValueAsString(any(BalanceDto.class));
    }

    @Test
    void doGet_ShouldReturnStatement() throws Exception {
        String fromDate = "2025-02-01";
        String toDate = "2025-03-01";
        when(request.getPathInfo()).thenReturn("/reports/1/statement");
        when(request.getParameter("from")).thenReturn(fromDate);
        when(request.getParameter("to")).thenReturn(toDate);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        IncomeStatement statement = IncomeStatement.builder().build();
        when(reportController.getStatement(testUser, LocalDate.parse(fromDate), LocalDate.parse(toDate))).thenReturn(statement);
        when(objectMapper.writeValueAsString(statement)).thenReturn(realMapper.writeValueAsString(statement));

        reportServlet.doGet(request, response);

        ArgumentCaptor<LocalDate> fromCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> toCaptor = ArgumentCaptor.forClass(LocalDate.class);

        verify(reportController).getStatement(
                eq(testUser),
                fromCaptor.capture(),
                toCaptor.capture()
        );

        assertEquals(LocalDate.parse(fromDate), fromCaptor.getValue());
        assertEquals(LocalDate.parse(toDate), toCaptor.getValue());
        verify(response).setContentType("application/json");
    }

    @Test
    void doGet_ShouldThrowExceptionForMissingStatementParams() {
        when(request.getPathInfo()).thenReturn("/reports/1/statement");

        assertThrows(RequiredArgumentException.class, () -> reportServlet.doGet(request, response));
    }

    @Test
    void doGet_ShouldReturnCategoriesReport() throws Exception {
        String fromDate = "2025-02-01";
        String toDate = "2025-03-01";
        when(request.getPathInfo()).thenReturn("/reports/1/categories");
        when(request.getParameter("from")).thenReturn(fromDate);
        when(request.getParameter("to")).thenReturn(toDate);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        List<String> categoriesReport = List.of();
        when(objectMapper.writeValueAsString(categoriesReport)).thenReturn(realMapper.writeValueAsString(categoriesReport));

        reportServlet.doGet(request, response);

        verify(reportController).getCategories(
                testUser,
                LocalDate.parse(fromDate),
                LocalDate.parse(toDate)
        );
        verify(response).setContentType("application/json");
    }

    @Test
    void doGet_ShouldThrowExceptionForMissingCategoriesParams() {
        when(request.getPathInfo()).thenReturn("/reports/1/categories");

        assertThrows(RequiredArgumentException.class, () -> reportServlet.doGet(request, response));
    }

    @Test
    void doGet_InvalidPathShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/invalid/path");

        assertThrows(PathNotFoundException.class, () -> reportServlet.doGet(request, response));
    }

    @Test
    void doGet_ShouldHandleDateParsingError() {
        when(request.getPathInfo()).thenReturn("/reports/1/statement");
        when(request.getParameter("from")).thenReturn("invalid-date");
        when(request.getParameter("to")).thenReturn("2023-01-31");

        assertThrows(DateTimeParseException.class, () -> reportServlet.doGet(request, response));
    }


}