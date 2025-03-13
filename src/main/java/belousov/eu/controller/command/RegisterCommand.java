package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.AuthController;

public class RegisterCommand implements CommandMenu {
    private final AuthController controller;

    public RegisterCommand(DependencyContainer container) {
        controller = container.get(AuthController.class);
    }

    @Override
    public void execute() {
        controller.register();
    }
}
