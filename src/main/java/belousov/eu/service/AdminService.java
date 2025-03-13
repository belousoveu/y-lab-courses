package belousov.eu.service;

import belousov.eu.model.Role;
import belousov.eu.model.User;

import java.util.List;

public interface AdminService {
    List<User> getAllUsers();

    void blockUser(int userId);

    void deleteUserById(int userId);

    void setRole(int userId, Role role);

    void unblockUser(int userId);

    List<String> getAllTransactions();
}
