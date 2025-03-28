package belousov.eu.servlet;

import belousov.eu.controller.TransactionController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.TransactionFilter;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletInputStream;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServletTest {

    @Mock
    private TransactionController transactionController;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private TransactionServlet transactionServlet;

    private final ObjectMapper realMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    private final ServletInputStream inputStream = mock(ServletInputStream.class);

    private final StringWriter responseWriter = new StringWriter();
    private final User testUser = new User(1, "testUser", "testUser@gmail.com", "", Role.USER, true);
    private final TransactionDto testTransaction = new TransactionDto(1, LocalDate.of(2025, 2, 5), "DEPOSIT", null, 1000.0, "Salary", 1);

    @BeforeEach
    void setUp() {
        lenient().when(request.getSession()).thenReturn(session);
        lenient().when(session.getAttribute("currentUser")).thenReturn(testUser);
    }

    @Test
    void doGet_ShouldReturnAllTransactions() throws Exception {
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getPathInfo()).thenReturn("/transactions/1");
        when(transactionController.getTransactions(any(TransactionFilter.class))).thenReturn(List.of(testTransaction));
        when(objectMapper.writeValueAsString(any(List.class))).thenReturn(realMapper.writeValueAsString(List.of(testTransaction)));
        transactionServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("DEPOSIT"));
    }

    @Test
    void doGet_ShouldReturnSingleTransaction() throws Exception {
        when(request.getPathInfo()).thenReturn("/transaction/1/1");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        when(transactionController.getTransactionById(1, testUser)).thenReturn(testTransaction);
        when(objectMapper.writeValueAsString(testTransaction)).thenReturn(realMapper.writeValueAsString(testTransaction));

        transactionServlet.doGet(request, response);

        verify(transactionController).getTransactionById(1, testUser);
        verify(response).setContentType("application/json");
    }

    @Test
    void doGet_ShouldFilterTransactions() throws Exception {
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getPathInfo()).thenReturn("/api/transaction/1/search");
        when(request.getQueryString()).thenReturn("from=2025-01-01&to=2025-12-31");
        when(request.getParameter("from")).thenReturn("2025-01-01");
        when(request.getParameter("to")).thenReturn("2025-12-31");
        when(request.getParameter("type")).thenReturn("DEPOSIT");
        when(request.getParameter("category")).thenReturn("");

        List<TransactionDto> transactions = List.of(testTransaction);
        when(transactionController.getTransactions(any(TransactionFilter.class))).thenReturn(transactions);
        when(objectMapper.writeValueAsString(transactions)).thenReturn(realMapper.writeValueAsString(transactions));

        transactionServlet.doGet(request, response);

        // Assert
        ArgumentCaptor<TransactionFilter> filterCaptor = ArgumentCaptor.forClass(TransactionFilter.class);
        verify(transactionController).getTransactions(filterCaptor.capture());

        TransactionFilter filter = filterCaptor.getValue();
        assertEquals(LocalDate.parse("2025-01-01"), filter.getFrom());
    }

    @Test
    void doPost_ShouldCreateTransaction() throws Exception {

        when(request.getPathInfo()).thenReturn("/transactions/1");
        when(request.getInputStream()).thenReturn(inputStream);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(objectMapper.readValue(inputStream, TransactionDto.class)).thenReturn(testTransaction);
        when(transactionController.createTransaction(testUser, testTransaction)).thenReturn(testTransaction);
        when(objectMapper.writeValueAsString(testTransaction)).thenReturn(realMapper.writeValueAsString(testTransaction));

        transactionServlet.doPost(request, response);

        verify(transactionController).createTransaction(eq(testUser), any(TransactionDto.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void doPut_ShouldUpdateTransaction() throws Exception {

        when(request.getPathInfo()).thenReturn("/transaction/1/1");
        when(request.getInputStream()).thenReturn(inputStream);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(objectMapper.readValue(inputStream, TransactionDto.class)).thenReturn(testTransaction);
        when(transactionController.updateTransaction(1, testTransaction, testUser)).thenReturn(testTransaction);
        when(objectMapper.writeValueAsString(testTransaction)).thenReturn(realMapper.writeValueAsString(testTransaction));

        transactionServlet.doPut(request, response);

        verify(transactionController).updateTransaction(eq(1), any(TransactionDto.class), eq(testUser));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doDelete_ShouldDeleteTransaction() {
        when(request.getPathInfo()).thenReturn("/transaction/1/1");

        transactionServlet.doDelete(request, response);

        verify(transactionController).deleteTransaction(1, testUser);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doGet_InvalidPathShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/invalid/path");

        assertThrows(PathNotFoundException.class, () -> transactionServlet.doGet(request, response));
    }

    @Test
    void doPost_InvalidPathShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/invalid");

        assertThrows(PathNotFoundException.class, () -> transactionServlet.doPost(request, response));
    }


}