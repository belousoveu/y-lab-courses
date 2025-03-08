package belousov.eu.service;

import belousov.eu.model.Role;
import belousov.eu.model.User;

import java.util.List;

public interface AdminAccess {
    List<User> getAllUsers();

    void blockUser(long userId);

    void unblockUser(long userId);

    void deleteUserById(long userId);

    void setRole(long userId, Role role);
}
