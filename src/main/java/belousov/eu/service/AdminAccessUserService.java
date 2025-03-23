package belousov.eu.service;

import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.model.dto.UserDto;

import java.util.List;

public interface AdminAccessUserService {
    List<UserDto> getAllUsers();

    void blockUser(int userId);

    void unblockUser(int userId);

    void deleteUserById(int userId, User currentUser);

    void setRole(int userId, Role role);
}
