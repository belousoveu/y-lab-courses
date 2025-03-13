package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.*;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.repository.UserRepository;
import belousov.eu.utils.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private User admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1, "John Doe", "john@example.com", Password.encode("password123"), Role.USER, true);
        admin = new User(2, "Admin", "admin@example.com", Password.encode("admin123"), Role.ADMIN, true);
        PersonalMoneyTracker.setCurrentUser(user); // Устанавливаем текущего пользователя
    }

    @Test
    void test_register_shouldSaveUserAndSetCurrentUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.register("John Doe", "john@example.com", "password123");

        verify(userRepository, times(1)).save(any(User.class));
        assertThat(PersonalMoneyTracker.getCurrentUser()).isEqualTo(user);
    }

    @Test
    void test_login_whenUserExistsAndActive_shouldSetCurrentUser() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        userService.login("john@example.com", "password123");

        assertThat(PersonalMoneyTracker.getCurrentUser()).isEqualTo(user);
    }

    @Test
    void test_login_whenUserDoesNotExist_shouldThrowException() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login("john@example.com", "password123"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь с электронной почтой john@example.com не найден");
    }

    @Test
    void test_login_whenUserIsBlocked_shouldThrowException() {
        user.setActive(false);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.login("john@example.com", "password123"))
                .isInstanceOf(UserWasBlockedException.class)
                .hasMessage("Пользователь John Doe заблокирован");
    }

    @Test
    void test_login_whenPasswordIsIncorrect_shouldThrowException() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.login("john@example.com", "wrongpassword"))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void test_changeName_shouldUpdateUserName() {
        userService.changeName(user, "Jane Doe");

        assertThat(user.getName()).isEqualTo("Jane Doe");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void test_changePassword_whenOldPasswordIsCorrect_shouldUpdatePassword() {
        userService.changePassword(user, "password123", "newpassword");

        assertThat(Password.verify("newpassword", user.getPassword())).isTrue();
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void test_changePassword_whenOldPasswordIsIncorrect_shouldThrowException() {
        assertThatThrownBy(() -> userService.changePassword(user, "wrongpassword", "newpassword"))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void test_changeEmail_shouldUpdateUserEmail() {
        userService.changeEmail(user, "jane@example.com");

        assertThat(user.getEmail()).isEqualTo("jane@example.com");
        verify(userRepository, times(1)).removeOldEmail("john@example.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void test_deleteUser_whenAdminDeletesAnotherUser_shouldDeleteUser() {
        PersonalMoneyTracker.setCurrentUser(admin);
        when(userRepository.getAllAdminIds()).thenReturn(List.of(admin.getId()));

        userService.deleteUser(user, "");

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void test_deleteUser_whenUserDeletesSelf_shouldDeleteUserAndSetCurrentUserToNull() {
        userService.deleteUser(user, "password123");

        verify(userRepository, times(1)).delete(user);
        assertThat(PersonalMoneyTracker.getCurrentUser()).isNull();
    }

    @Test
    void test_deleteUser_whenUserDeletesAnotherUser_shouldThrowException() {
        User otherUser = new User(3, "Jane Doe", "jane@example.com", Password.encode("password456"), Role.USER, true);
        assertThatThrownBy(() -> userService.deleteUser(otherUser, ""))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void test_deleteUser_whenDeletingLastAdmin_shouldThrowException() {
        PersonalMoneyTracker.setCurrentUser(admin);
        when(userRepository.getAllAdminIds()).thenReturn(List.of(admin.getId()));

        assertThatThrownBy(() -> userService.deleteUser(admin, ""))
                .isInstanceOf(LastAdminDeleteException.class);
    }

    @Test
    void test_blockUser_whenBlockingLastAdmin_shouldThrowException() {
        PersonalMoneyTracker.setCurrentUser(admin);
        when(userRepository.getAllAdminIds()).thenReturn(List.of(admin.getId()));
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        assertThatThrownBy(() -> userService.blockUser(admin.getId()))
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

        List<User> users = userService.getAllUsers();
        assertThat(users).containsExactlyInAnyOrder(user, admin);
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
        PersonalMoneyTracker.setCurrentUser(admin);
        when(userRepository.getAllAdminIds()).thenReturn(List.of(admin.getId()));
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> userService.setRole(admin.getId(), Role.USER))
                .isInstanceOf(LastAdminDeleteException.class);
    }
}