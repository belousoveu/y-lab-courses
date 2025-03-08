package belousov.eu.controller;

import belousov.eu.model.Role;
import belousov.eu.service.AdminService;
import belousov.eu.utils.InputPattern;
import belousov.eu.utils.MessageColor;
import belousov.eu.view.ConsoleView;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ConsoleView consoleView;

    public void showAllUsers() {
        consoleView.println("Список пользователей:", adminService.getAllUsers(), MessageColor.WHITE, MessageColor.YELLOW);
    }

    public void blockUser() {
        int userId = consoleView.readInt("Введите ID пользователя, которого хотите заблокировать:", InputPattern.POSITIVE_INTEGER);
        adminService.blockUser(userId);
        consoleView.println("Пользователь успешно заблокирован", MessageColor.CYAN);
    }

    public void unblockUser() {
        int userId = consoleView.readInt("Введите ID пользователя, которого хотите разблокировать:", InputPattern.POSITIVE_INTEGER);
        adminService.unblockUser(userId);
        consoleView.println("Пользователь успешно разблокирован", MessageColor.CYAN);
    }

    public void deleteUser() {
        int userId = consoleView.readInt("Введите ID пользователя, которого хотите удалить:", InputPattern.POSITIVE_INTEGER);
        adminService.deleteUserById(userId);
        consoleView.println("Пользователь успешно удален", MessageColor.CYAN);
    }

    public void setRole() {
        int userId = consoleView.readInt("Введите ID пользователя", InputPattern.POSITIVE_INTEGER);
        Role role = consoleView.readFromList("Введите роль пользователя", List.of(Role.values()));
        adminService.setRole(userId, role);
        consoleView.println(String.format("Пользователю с ID %d назначена роль %s", userId, role), MessageColor.CYAN);
    }

}
