package belousov.eu.service;

import belousov.eu.model.Role;
import belousov.eu.model.User;

import java.util.List;

public interface AdminAccessUserService {
    List<User> getAllUsers();

    void blockUser(int userId);

    void unblockUser(int userId);

    void deleteUserById(int userId);

    void setRole(int userId, Role role);
}
