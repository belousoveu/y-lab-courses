package belousov.eu.service;

import belousov.eu.mapper.UserMapper;
import belousov.eu.model.OperationType;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.UserDto;
import belousov.eu.service.imp.AdminServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    }

    @Test
    void test_getAllUsers_whenAdmin_shouldReturnAllUsers() {
        UserMapper mapper = Mappers.getMapper(UserMapper.class);
        List<UserDto> users = List.of(mapper.toDto(adminUser), mapper.toDto(regularUser));
        when(adminAccessUserService.getAllUsers()).thenReturn(users);

        List<UserDto> result = adminServiceImp.getAllUsers();
        assertThat(result).containsExactlyInAnyOrder(mapper.toDto(adminUser), mapper.toDto(regularUser));
        verify(adminAccessUserService, times(1)).getAllUsers();
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
    }

    @Test
    void test_unblockUser_whenAdmin_shouldUnblockUser() {
        adminServiceImp.unblockUser(regularUser.getId());

        verify(adminAccessUserService, times(1)).unblockUser(regularUser.getId());
    }

    @Test
    void test_getAllTransactions_whenAdmin_shouldReturnAllTransactions() {
        TransactionDto dto1 = new TransactionDto(1, LocalDate.of(2025, 1, 10), OperationType.DEPOSIT.name(), "Категория 1", 100.0, "Транзакция 1", 1);
        TransactionDto dto2 = new TransactionDto(2, LocalDate.of(2025, 1, 15), OperationType.WITHDRAW.name(), "Категория 2", 50.0, "Транзакция 2", 2);

        List<TransactionDto> transactions = List.of(dto1, dto2);
        when(adminAccessTransactionService.getAllTransactions()).thenReturn(transactions);

        List<TransactionDto> result = adminServiceImp.getAllTransactions();
        assertThat(result).containsExactlyInAnyOrder(dto1, dto2);
        verify(adminAccessTransactionService, times(1)).getAllTransactions();
    }

    @Test
    void test_deleteUserById_whenAdmin_shouldDeleteUser() {
        adminServiceImp.deleteUserById(regularUser.getId(), adminUser);

        verify(adminAccessUserService, times(1)).deleteUserById(regularUser.getId(), adminUser);
    }

    @Test
    void test_deleteUserById_whenDeletingSelf_shouldSetCurrentUserToNull() {
        adminServiceImp.deleteUserById(adminUser.getId(), adminUser);

        verify(adminAccessUserService, times(1)).deleteUserById(adminUser.getId(), adminUser);
    }

    @Test
    void test_setRole_whenAdmin_shouldSetRole() {
        adminServiceImp.setRole(regularUser.getId(), Role.ADMIN);

        verify(adminAccessUserService, times(1)).setRole(regularUser.getId(), Role.ADMIN);
    }
}