package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.ForbiddenException;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для {@link AdminServiceImp}.
 */
class AdminServiceImpTest {

    @Mock
    private AdminAccessUserService adminAccessUserService;

    @Mock
    private AdminAccessTransactionService adminAccessTransactionService;

    @InjectMocks
    private AdminServiceImp adminServiceImp;

    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminUser = new User(1, "John Doe", "john@example.com", "password123", Role.ADMIN, true);
        regularUser = new User(2, "Jane Doe", "jane@example.com", "password456", Role.USER, true);
        PersonalMoneyTracker.setCurrentUser(adminUser); // Устанавливаем текущего пользователя как администратора
    }

    @Test
    void test_getAllUsers_whenAdmin_shouldReturnAllUsers() {
        List<User> users = List.of(adminUser, regularUser);
        when(adminAccessUserService.getAllUsers()).thenReturn(users);

        List<User> result = adminServiceImp.getAllUsers();
        assertThat(result).containsExactlyInAnyOrder(adminUser, regularUser);
        verify(adminAccessUserService, times(1)).getAllUsers();
    }

    @Test
    void test_getAllUsers_whenNotAdmin_shouldThrowForbiddenException() {
        PersonalMoneyTracker.setCurrentUser(regularUser); // Устанавливаем текущего пользователя как обычного пользователя

        assertThatThrownBy(() -> adminServiceImp.getAllUsers())
                .isInstanceOf(ForbiddenException.class);
        verify(adminAccessUserService, never()).getAllUsers();
    }

    @Test
    void test_blockUser_whenAdmin_shouldBlockUser() {
        adminServiceImp.blockUser(regularUser.getId());

        verify(adminAccessUserService, times(1)).blockUser(regularUser.getId());
    }

    @Test
    void test_blockUser_whenBlockingSelf_shouldSetCurrentUserToNull() {
        adminServiceImp.blockUser(adminUser.getId());

        verify(adminAccessUserService, times(1)).blockUser(adminUser.getId());
        assertThat(PersonalMoneyTracker.getCurrentUser()).isNull();
    }

    @Test
    void test_unblockUser_whenAdmin_shouldUnblockUser() {
        adminServiceImp.unblockUser(regularUser.getId());

        verify(adminAccessUserService, times(1)).unblockUser(regularUser.getId());
    }

    @Test
    void test_getAllTransactions_whenAdmin_shouldReturnAllTransactions() {
        List<String> transactions = List.of("Транзакция 1", "Транзакция 2");
        when(adminAccessTransactionService.getAllTransactions()).thenReturn(transactions);

        List<String> result = adminServiceImp.getAllTransactions();
        assertThat(result).containsExactlyInAnyOrder("Транзакция 1", "Транзакция 2");
        verify(adminAccessTransactionService, times(1)).getAllTransactions();
    }

    @Test
    void test_deleteUserById_whenAdmin_shouldDeleteUser() {
        adminServiceImp.deleteUserById(regularUser.getId());

        verify(adminAccessUserService, times(1)).deleteUserById(regularUser.getId());
    }

    @Test
    void test_deleteUserById_whenDeletingSelf_shouldSetCurrentUserToNull() {
        adminServiceImp.deleteUserById(adminUser.getId());

        verify(adminAccessUserService, times(1)).deleteUserById(adminUser.getId());
        assertThat(PersonalMoneyTracker.getCurrentUser()).isNull();
    }

    @Test
    void test_setRole_whenAdmin_shouldSetRole() {
        adminServiceImp.setRole(regularUser.getId(), Role.ADMIN);

        verify(adminAccessUserService, times(1)).setRole(regularUser.getId(), Role.ADMIN);
    }
}