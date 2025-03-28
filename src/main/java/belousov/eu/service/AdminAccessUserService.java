package belousov.eu.service;

import belousov.eu.model.dto.UserDto;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;

import java.util.List;

public interface AdminAccessUserService {
    List<UserDto> getAllUsers();

    void blockUser(int userId);

    void unblockUser(int userId);

    void deleteUserById(int userId, User currentUser);

    void setRole(int userId, Role role);
}
