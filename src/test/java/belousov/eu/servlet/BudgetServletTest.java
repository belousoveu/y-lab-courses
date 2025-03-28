package belousov.eu.servlet;

import belousov.eu.controller.BudgetController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.model.dto.BudgetDto;
import belousov.eu.model.dto.BudgetReport;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServletTest {

    @Mock
    private BudgetController budgetController;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private BudgetServlet budgetServlet;

    private final ServletInputStream inputStream = mock(ServletInputStream.class);

    private final StringWriter responseWriter = new StringWriter();
    private final User testUser = new User(1, "testUser", "testUser@gmail.com", "", Role.USER, true);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private BudgetDto testBudgetDto;

    @BeforeEach
    void setUp() throws IOException {
        ObjectMapper realMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        lenient().when(objectMapper.writeValueAsString(any())).thenAnswer(invocation ->
                realMapper.writeValueAsString(invocation.getArgument(0)));

        lenient().when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(testUser);

        testBudgetDto = new BudgetDto();
        testBudgetDto.setId(1);
        testBudgetDto.setPeriod(YearMonth.parse("2025-02"));
        testBudgetDto.setUserId(testUser.getId());
        testBudgetDto.setCategoryId(1);
        testBudgetDto.setAmount(1000.00);
    }

    @Test
    void doGet_ShouldRedirectToCurrentPeriod() throws Exception {
        when(request.getPathInfo()).thenReturn("/budgets");
        String expectedPeriod = LocalDate.now().format(DATE_FORMAT);
        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);

        budgetServlet.doGet(request, response);

        verify(response).sendRedirect(redirectCaptor.capture());
        assertEquals("/api/budgets/" + expectedPeriod, redirectCaptor.getValue());
    }

    @Test
    void doGet_ShouldReturnBudgetForPeriod() throws Exception {
        String testPeriod = "2025-02";
        BudgetReport expectedBudget = new BudgetReport();
        expectedBudget.setPeriod(YearMonth.parse(testPeriod));
        expectedBudget.setUser(testUser);

        when(request.getPathInfo()).thenReturn("/budgets/" + testPeriod);
        when(budgetController.getBudgetByPeriod(testUser, testPeriod)).thenReturn(expectedBudget);

        budgetServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("[2025,2]"));
        assertTrue(jsonResponse.contains(testUser.getName()));
    }

    @Test
    void doGet_InvalidPathShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/invalid");

        assertThrows(PathNotFoundException.class, () -> budgetServlet.doGet(request, response));
    }

    @Test
    void doPost_ShouldAddBudgetAndRedirect() throws Exception {
        String testPeriod = "2025-02";
        BudgetDto budgetDto = testBudgetDto;

        when(request.getPathInfo()).thenReturn("/budgets");
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, BudgetDto.class)).thenReturn(budgetDto);

        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);

        budgetServlet.doPost(request, response);

        verify(budgetController).addBudget(testUser, budgetDto);
        verify(response).sendRedirect(redirectCaptor.capture());
        assertEquals("/api/budgets/" + testPeriod, redirectCaptor.getValue());
        verify(response).setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    }

    @Test
    void doPost_InvalidPathShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/invalid");

        assertThrows(PathNotFoundException.class, () -> budgetServlet.doPost(request, response));
    }


}