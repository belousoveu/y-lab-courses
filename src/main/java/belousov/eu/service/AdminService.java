package belousov.eu.service;

import belousov.eu.annotation.Loggable;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.UserDto;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;

import java.util.List;

public interface AdminService {
    List<UserDto> getAllUsers();

    @Loggable
    void blockUser(int userId);

    @Loggable
    void deleteUserById(int userId, User user);

    @Loggable
    void setRole(int userId, Role role);

    @Loggable
    void unblockUser(int userId);

    List<TransactionDto> getAllTransactions();
}
