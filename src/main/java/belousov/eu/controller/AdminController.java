package belousov.eu.controller;

import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.UserDto;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import belousov.eu.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    public List<UserDto> getUsers() {

        return adminService.getAllUsers();
    }

    public void blockUser(int userId) {
        adminService.blockUser(userId);
    }

    public void unblockUser(int userId) {
        adminService.unblockUser(userId);
    }

    public void deleteUser(int userId, User user) {
        adminService.deleteUserById(userId, user);
    }

    public void setRole(int userId, Role role) {
        adminService.setRole(userId, role);
    }

    public List<TransactionDto> getTransactions() {
        return adminService.getAllTransactions();
    }

}
