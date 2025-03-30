package belousov.eu.service;

import belousov.eu.exception.*;
import belousov.eu.mapper.UserMapper;
import belousov.eu.model.dto.LoginDto;
import belousov.eu.model.dto.RegisterDto;
import belousov.eu.model.dto.UserDto;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import belousov.eu.repository.imp.UserRepositoryImp;
import belousov.eu.service.imp.UserService;
import belousov.eu.utils.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для {@link UserService}.
 */
class UserServiceTest {

    @Mock
    private UserRepositoryImp userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private User admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1, "John Doe", "john@example.com", Password.encode("password123"), Role.USER, true);
        admin = new User(2, "Admin", "admin@example.com", Password.encode("admin123"), Role.ADMIN, true);
    }

    @Test
    void test_register_shouldSaveUserAndSetCurrentUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.register(new RegisterDto("John Doe", "john@example.com", "password123"));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void test_login_whenUserExistsAndActive_shouldSetCurrentUser() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        userService.login(new LoginDto("john@example.com", "password123"));
        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void test_login_whenUserDoesNotExist_shouldThrowException() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        LoginDto loginDto = new LoginDto("john@example.com", "password123");
        assertThatThrownBy(() -> userService.login(loginDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь с электронной почтой john@example.com не найден");
    }

    @Test
    void test_login_whenUserIsBlocked_shouldThrowException() {
        user.setActive(false);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        LoginDto loginDto = new LoginDto("john@example.com", "password123");
        assertThatThrownBy(() -> userService.login(loginDto))
                .isInstanceOf(UserWasBlockedException.class)
                .hasMessage("Пользователь John Doe заблокирован");
    }

    @Test
    void test_login_whenPasswordIsIncorrect_shouldThrowException() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        LoginDto loginDto = new LoginDto("john@example.com", "unknown");
        assertThatThrownBy(() -> userService.login(loginDto))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void test_deleteUser_whenAdminDeletesAnotherUser_shouldDeleteUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.getAllAdminIds()).thenReturn(List.of(admin.getId()));

        userService.deleteUser(user.getId(), "", admin);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void test_deleteUser_whenUserDeletesSelf_shouldDeleteUserAndSetCurrentUserToNull() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deleteUser(user.getId(), "password123", user);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void test_deleteUser_whenUserDeletesAnotherUser_shouldThrowException() {

        User otherUser = new User(3, "Jane Doe", "jane@example.com", Password.encode("password456"), Role.USER, true);
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        int otherUserId = otherUser.getId();
        assertThatThrownBy(() -> userService.deleteUser(otherUserId, "", user))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void test_deleteUser_whenDeletingLastAdmin_shouldThrowException() {
        when(userRepository.getAllAdminIds()).thenReturn(List.of(admin.getId()));
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        int adminId = admin.getId();
        assertThatThrownBy(() -> userService.deleteUser(adminId, "", admin))
                .isInstanceOf(LastAdminDeleteException.class);
    }

    @Test
    void test_blockUser_whenBlockingLastAdmin_shouldThrowException() {
        when(userRepository.getAllAdminIds()).thenReturn(List.of(admin.getId()));
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        int adminId = admin.getId();
        assertThatThrownBy(() -> userService.blockUser(adminId))
                .isInstanceOf(LastAdminDeleteException.class);
    }

    @Test
    void test_unblockUser_shouldSetUserActive() {
        user.setActive(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.unblockUser(user.getId());

        assertThat(user.isActive()).isTrue();
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void test_getAllUsers_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user, admin));
        UserMapper mapper = Mappers.getMapper(UserMapper.class);

        List<UserDto> users = userService.getAllUsers();
        assertThat(users).containsExactlyInAnyOrder(mapper.toDto(user), mapper.toDto(admin));
    }

    @Test
    void test_setRole_whenSettingAdmin_shouldUpdateRole() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.setRole(user.getId(), Role.ADMIN);

        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void test_setRole_whenSettingUserAndLastAdmin_shouldThrowException() {
        when(userRepository.getAllAdminIds()).thenReturn(List.of(admin.getId()));
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));

        int adminId = admin.getId();
        assertThatThrownBy(() -> userService.setRole(adminId, Role.USER))
                .isInstanceOf(LastAdminDeleteException.class);
    }
}