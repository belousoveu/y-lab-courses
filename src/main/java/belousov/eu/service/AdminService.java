package belousov.eu.service;

import belousov.eu.model.Role;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.UserDto;

import java.util.List;

public interface AdminService {
    List<UserDto> getAllUsers();

    void blockUser(int userId);

    void deleteUserById(int userId);

    void setRole(int userId, Role role);

    void unblockUser(int userId);

    List<TransactionDto> getAllTransactions();
}
