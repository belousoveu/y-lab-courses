package belousov.eu.controller;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.model.User;
import belousov.eu.service.AuthService;
import belousov.eu.utils.InputPattern;
import belousov.eu.utils.MessageColor;
import belousov.eu.view.ConsoleView;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ConsoleView consoleView;


    public void register() {
        String name = consoleView.readString("Введите имя: ", InputPattern.NAME);
        String email = consoleView.readString("Введите email: ", InputPattern.EMAIL);
        String password = consoleView.readString("Введите пароль: ", InputPattern.PASSWORD);
        authService.register(name, email, password);
        consoleView.println(String.format("Пользователь %s успешно зарегистрирован", name), MessageColor.CYAN);

    }

    public void login() {
        String email = consoleView.readString("Введите email: ", InputPattern.EMAIL);
        String password = consoleView.readString("Введите пароль: ", InputPattern.PASSWORD);
        authService.login(email, password);
        User user = PersonalMoneyTracker.getCurrentUser();
        consoleView.println(String.format("Пользователь %s успешно авторизован", user.getName()), MessageColor.CYAN);
    }
}
