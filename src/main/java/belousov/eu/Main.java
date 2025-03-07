package belousov.eu;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.command.AuthCommand;
import belousov.eu.controller.command.RegisterCommand;
import belousov.eu.view.Menu;

public class Main {
    public static void main(String[] args) {

        DependencyContainer container = new DependencyContainer();

        Menu registrationMenu = new Menu("Войдите в программу или зарегистрируйтесь");
        registrationMenu.add(1, "Войти", new AuthCommand(container));
        registrationMenu.add(2, "Регистрация", new RegisterCommand(container));
        registrationMenu.add(0, "Выход", () -> System.exit(0));

        registrationMenu.display();
    }
}