package belousov.eu.servlet;

import belousov.eu.controller.CategoryController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.model.dto.CategoryDto;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServletTest {

    @Mock
    private CategoryController categoryController;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private CategoryServlet categoryServlet;

    private final ObjectMapper realMapper = new ObjectMapper();

    private final ServletInputStream inputStream = mock(ServletInputStream.class);

    private final StringWriter responseWriter = new StringWriter();
    private final User testUser = new User(1, "testUser", "testUser@gmail.com", "", Role.USER, true);
    private final CategoryDto testCategoryDto1 = new CategoryDto();
    private final CategoryDto testCategoryDto2 = new CategoryDto();

    @BeforeEach
    void setUp() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(testUser);
        testCategoryDto1.setId(1);
        testCategoryDto1.setName("Food");
        testCategoryDto1.setUser(testUser);
        testCategoryDto2.setId(2);
        testCategoryDto2.setName("Transport");
        testCategoryDto2.setUser(testUser);
    }

    @Test
    void doGet_ShouldReturnCategoriesList() throws Exception {
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getPathInfo()).thenReturn("/categories");

        List<CategoryDto> categories = List.of(
                testCategoryDto1,
                testCategoryDto2
        );
        when(objectMapper.writeValueAsString(categories)).thenReturn(realMapper.writeValueAsString(categories));
        when(categoryController.getCategories(testUser)).thenReturn(categories);
        categoryServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(categoryController).getCategories(testUser);
        assertTrue(responseWriter.toString().contains("Food"));
    }

    @Test
    void doGet_InvalidPathShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/invalid");

        assertThrows(PathNotFoundException.class, () -> categoryServlet.doGet(request, response));
    }

    @Test
    void doPost_ShouldAddCategoryAndRedirect() throws Exception {
        CategoryDto categoryDto = testCategoryDto1;
        when(request.getPathInfo()).thenReturn("/categories");
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, CategoryDto.class)).thenReturn(categoryDto);

        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);

        categoryServlet.doPost(request, response);

        verify(categoryController).addCategory(testUser, categoryDto);
        verify(response).sendRedirect(redirectCaptor.capture());
        assertEquals("/api/categories", redirectCaptor.getValue());
        verify(response).setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    }

    @Test
    void doPut_ShouldUpdateCategory() throws Exception {
        int categoryId = 1;
        when(request.getPathInfo()).thenReturn("/categories/" + categoryId);
        CategoryDto updatedDto = testCategoryDto1;
        updatedDto.setName("Updated Category");
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, CategoryDto.class)).thenReturn(updatedDto);

        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);

        categoryServlet.doPut(request, response);

        verify(categoryController).editCategory(categoryId, testUser, updatedDto);
        verify(response).sendRedirect(redirectCaptor.capture());
        assertEquals("/api/categories", redirectCaptor.getValue());
    }

    @Test
    void doDelete_ShouldDeleteCategory() throws Exception {
        int categoryId = 2;
        when(request.getPathInfo()).thenReturn("/categories/" + categoryId);

        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);

        categoryServlet.doDelete(request, response);

        verify(categoryController).deleteCategory(categoryId, testUser);
        verify(response).sendRedirect(redirectCaptor.capture());
        assertEquals("/api/categories", redirectCaptor.getValue());
        verify(response).setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    }

    @Test
    void doPut_InvalidIdShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/categories/invalid");

        assertThrows(PathNotFoundException.class, () -> categoryServlet.doPut(request, response));
    }

    @Test
    void doPost_InvalidPathShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/invalid");

        assertThrows(PathNotFoundException.class, () -> categoryServlet.doPost(request, response));
    }

    @Test
    void doDelete_InvalidPathShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/categories/invalid");

        assertThrows(PathNotFoundException.class, () -> categoryServlet.doDelete(request, response));
    }

    @Test
    void doPut_ShouldHandleInvalidCategoryData() throws Exception {
        int categoryId = 2;
        when(request.getPathInfo()).thenReturn("/categories/" + categoryId);
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, CategoryDto.class)).thenThrow(new IOException("Invalid data"));

        assertThrows(IOException.class, () -> categoryServlet.doPut(request, response));
    }
}