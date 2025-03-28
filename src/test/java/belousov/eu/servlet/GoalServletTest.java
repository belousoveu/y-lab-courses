package belousov.eu.servlet;

import belousov.eu.controller.GoalController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.model.dto.GoalDto;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServletTest {

    @Mock
    private GoalController goalController;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private GoalServlet goalServlet;

    private final ObjectMapper realMapper = new ObjectMapper();

    private final ServletInputStream inputStream = mock(ServletInputStream.class);

    private final StringWriter responseWriter = new StringWriter();
    private final User testUser = new User(1, "testUser", "testUser@gmail.com", "", Role.USER, true);
    private final GoalDto testGoal1 = new GoalDto(1, testUser, "Save for car", "", 10000L);
    private final GoalDto testGoal2 = new GoalDto(2, testUser, "Vacation", "", 5000L);

    @BeforeEach
    void setUp() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(testUser);
    }

    @Test
    void doGet_ShouldReturnGoalsList() throws Exception {
        when(request.getPathInfo()).thenReturn("/goals");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(goalController.getAllGoals(testUser.getId())).thenReturn(List.of(testGoal1, testGoal2));

        when(objectMapper.writeValueAsString(List.of(testGoal1, testGoal2))).thenReturn(realMapper.writeValueAsString(List.of(testGoal1, testGoal2)));

        goalServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        assertTrue(responseWriter.toString().contains("Save for car"));
    }

    @Test
    void doGet_InvalidPathShouldThrowException() {

        when(request.getPathInfo()).thenReturn("/invalid");

        assertThrows(PathNotFoundException.class, () -> goalServlet.doGet(request, response));
    }

    @Test
    void doPost_ShouldAddGoalAndRedirect() throws Exception {

        when(request.getPathInfo()).thenReturn("/goals");
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, GoalDto.class)).thenReturn(testGoal1);

        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);

        goalServlet.doPost(request, response);

        verify(goalController).addGoal(eq(testUser), any(GoalDto.class));
        verify(response).sendRedirect(redirectCaptor.capture());
        assertEquals("/goals", redirectCaptor.getValue());
    }

    @Test
    void doPut_ShouldUpdateGoal() throws Exception {
        int goalId = 2;

        when(request.getPathInfo()).thenReturn("/goals/" + goalId);
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, GoalDto.class)).thenReturn(testGoal2);

        goalServlet.doPut(request, response);

        verify(goalController).editGoal(eq(goalId), eq(testUser), any(GoalDto.class));
        verify(response).sendRedirect("/goals");
    }

    @Test
    void doDelete_ShouldDeleteGoal() throws Exception {
        int goalId = 2;
        when(request.getPathInfo()).thenReturn("/goals/" + goalId);

        goalServlet.doDelete(request, response);

        verify(goalController).deleteGoal(goalId, testUser);
        verify(response).sendRedirect("/goals");
    }

    @Test
    void doPut_InvalidIdShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/goals/invalid");

        assertThrows(PathNotFoundException.class, () -> goalServlet.doPut(request, response));
    }

    @Test
    void doPost_InvalidDataShouldThrowException() throws Exception {
        when(request.getPathInfo()).thenReturn("/goals");
        when(request.getInputStream()).thenThrow(new IOException("Invalid data"));

        assertThrows(IOException.class, () -> goalServlet.doPost(request, response));
    }

}