package belousov.eu.controller;

import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.UserDto;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import belousov.eu.service.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private static final String CURRENT_USER = "currentUser";

    private final AdminService adminService;

    @GetMapping("/users")
    public List<UserDto> getUsers() {

        return adminService.getAllUsers();
    }

    @GetMapping("/{userId}/block")
    public void blockUser(@PathVariable int userId) {
        adminService.blockUser(userId);
    }

    @GetMapping("/{userId}/unblock")
    public void unblockUser(@PathVariable int userId) {
        adminService.unblockUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        adminService.deleteUserById(userId, user);
    }

    @PutMapping("/{userId}")
    public void setRole(@PathVariable int userId, @RequestParam String role) {
        adminService.setRole(userId, Role.valueOf(role));
    }

    @GetMapping("/api/admin/transactions")
    public List<TransactionDto> getTransactions() {
        return adminService.getAllTransactions();
    }

}
