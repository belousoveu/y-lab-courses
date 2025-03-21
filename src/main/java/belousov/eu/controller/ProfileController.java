package belousov.eu.controller;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.model.User;
import belousov.eu.model.dto.UserProfileDto;
import belousov.eu.service.ProfileService;
import belousov.eu.utils.InputPattern;
import belousov.eu.utils.MessageColor;
import belousov.eu.view.ConsoleView;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final ConsoleView consoleView;


    public void changeName() {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        String newName = consoleView.readString("Введите новое имя: ", InputPattern.NAME);
        profileService.changeName(currentUser, newName);
        consoleView.println("Имя успешно изменено", MessageColor.CYAN);
    }

    public void changeEmail() {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        String newEmail = consoleView.readString("Введите новый email: ", InputPattern.EMAIL);
        profileService.changeEmail(currentUser, newEmail);
        consoleView.println("Email успешно изменен", MessageColor.CYAN);
    }

    public void changePassword() {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        String oldPassword = consoleView.readString("Введите старый пароль: ", InputPattern.PASSWORD);
        String newPassword = consoleView.readString("Введите новый пароль: ", InputPattern.PASSWORD);
        profileService.changePassword(currentUser, oldPassword, newPassword);
        consoleView.println("Пароль успешно изменен", MessageColor.CYAN);
    }

    public void deleteProfile() {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        String password = consoleView.readString("Введите пароль: ", InputPattern.PASSWORD);
        profileService.deleteUser(currentUser, password);
        consoleView.println("Пользователь успешно удален", MessageColor.CYAN);
    }

    public UserProfileDto viewProfile(int id) {
        return profileService.getUserById(id);
    }
}
