package belousov.eu.controller;

import belousov.eu.model.Role;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.UserDto;
import belousov.eu.service.AdminService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
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

    public void deleteUser(int userId) {
        adminService.deleteUserById(userId);
    }

    public void setRole(int userId, Role role) {
        adminService.setRole(userId, role);
    }

    public List<TransactionDto> getTransactions() {
        return adminService.getAllTransactions();
    }

}
