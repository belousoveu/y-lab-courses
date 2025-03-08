package belousov.eu.service;

import belousov.eu.model.Role;
import belousov.eu.model.User;

import java.util.List;

public interface AdminService {
    List<User> getAllUsers();

    void blockUser(long userId);

    void deleteUserById(long userId);

    void setRole(long userId, Role role);

    void unblockUser(long userId);
}
