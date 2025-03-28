package belousov.eu.servlet;

import belousov.eu.controller.ProfileController;
import belousov.eu.exception.ForbiddenException;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.exception.ValidationParametersException;
import belousov.eu.mapper.UserProfileMapper;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.model.dto.UserProfileUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServletTest {

    @Mock
    private ProfileController profileController;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ProfileServlet profileServlet;

    private final UserProfileMapper userMapper = Mappers.getMapper(UserProfileMapper.class);


    private final ServletInputStream inputStream = mock(ServletInputStream.class);

    private final StringWriter responseWriter = new StringWriter();
    private final User testUser = new User(2, "testUser", "testUser@gmail.com", "", Role.USER, true);
    private final User adminUser = new User(1, "admin", "admin@Admin.com", "", Role.ADMIN, true);

    @BeforeEach
    void setUp() {

        when(request.getSession()).thenReturn(session);
    }

    @Test
    void doGet_ShouldReturnProfileForOwner() throws Exception {
        when(request.getPathInfo()).thenReturn("/profile/2");
        when(session.getAttribute("currentUser")).thenReturn(testUser);
        when(profileController.viewProfile(2)).thenReturn(userMapper.toDto(testUser));
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        profileServlet.doGet(request, response);

        verify(response).setContentType("application/json");
        assertTrue(responseWriter.toString().contains("testUser"));
    }

    @Test
    void doGet_ShouldThrowForbiddenForNonAdmin() {
        when(request.getPathInfo()).thenReturn("/profile/1");
        when(session.getAttribute("currentUser")).thenReturn(testUser);

        assertThrows(ForbiddenException.class, () -> profileServlet.doGet(request, response));
    }

    @Test
    void doGet_AdminCanViewAnyProfile() throws Exception {
        when(request.getPathInfo()).thenReturn("/profile/2");
        when(session.getAttribute("currentUser")).thenReturn(adminUser);
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        when(profileController.viewProfile(2)).thenReturn(userMapper.toDto(testUser));

        profileServlet.doGet(request, response);

        verify(profileController).viewProfile(2);
    }

    @Test
    void doPatch_ShouldUpdateOwnProfile() throws Exception {
        when(request.getPathInfo()).thenReturn("/profile/2/update");
        when(session.getAttribute("currentUser")).thenReturn(testUser);
        UserProfileUpdateDto updateDto = new UserProfileUpdateDto("New Name", "new@email.com", null, null);
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, UserProfileUpdateDto.class)).thenReturn(updateDto);

        profileServlet.doPatch(request, response);

        verify(profileController).updateProfile(2, updateDto, testUser);
    }

    @Test
    void doPatch_ShouldThrowForbiddenForNonAdmin() {
        when(request.getPathInfo()).thenReturn("/profile/1/update");
        when(session.getAttribute("currentUser")).thenReturn(testUser);

        assertThrows(ForbiddenException.class, () -> profileServlet.doPatch(request, response));
    }

    @Test
    void doPatch_AdminCanUpdateAnyProfile() throws Exception {
        when(request.getPathInfo()).thenReturn("/profile/2/update");
        when(session.getAttribute("currentUser")).thenReturn(adminUser);
        UserProfileUpdateDto updateDto = new UserProfileUpdateDto("New Name", "new@email.com", "oldPassword1", "NewPassword0");
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, UserProfileUpdateDto.class)).thenReturn(updateDto);
        profileServlet.doPatch(request, response);

        verify(profileController).updateProfile(2, updateDto, adminUser);
    }

    @Test
    void doPatch_ShouldThrowValidationException() throws Exception {
        when(request.getPathInfo()).thenReturn("/profile/2/update");
        when(session.getAttribute("currentUser")).thenReturn(testUser);
        UserProfileUpdateDto invalidDto = new UserProfileUpdateDto("name", "invalid-email", "", "");
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, UserProfileUpdateDto.class)).thenReturn(invalidDto);

        assertThrows(ValidationParametersException.class, () -> profileServlet.doPatch(request, response));
    }

    @Test
    void doDelete_ShouldDeleteOwnProfile() throws Exception {
        when(request.getPathInfo()).thenReturn("/profile/2");
        when(session.getAttribute("currentUser")).thenReturn(testUser);
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, String.class)).thenReturn("password");

        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);

        profileServlet.doDelete(request, response);

        verify(profileController).deleteProfile(2, "password", testUser);
        verify(response).sendRedirect(redirectCaptor.capture());
        assertEquals("/api/auth/login", redirectCaptor.getValue());
        verify(session).invalidate();
    }

    @Test
    void doDelete_AdminCanDeleteAnyProfile() throws Exception {
        when(request.getPathInfo()).thenReturn("/profile/2");
        when(session.getAttribute("currentUser")).thenReturn(adminUser);
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, String.class)).thenReturn("adminpass");

        profileServlet.doDelete(request, response);

        verify(profileController).deleteProfile(2, "adminpass", adminUser);
    }

    @Test
    void doDelete_ShouldThrowForbiddenForNonAdmin() {
        when(request.getPathInfo()).thenReturn("/profile/1");
        when(session.getAttribute("currentUser")).thenReturn(testUser);

        assertThrows(ForbiddenException.class, () -> profileServlet.doDelete(request, response));
    }

    @Test
    void doGet_InvalidPathShouldThrowException() {
        when(request.getPathInfo()).thenReturn("/invalid");

        assertThrows(PathNotFoundException.class, () -> profileServlet.doGet(request, response));
    }
}