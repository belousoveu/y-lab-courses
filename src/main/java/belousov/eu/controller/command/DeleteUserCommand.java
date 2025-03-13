package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.AdminController;

public class DeleteUserCommand implements CommandMenu {

    AdminController controller;

    public DeleteUserCommand(DependencyContainer container) {
        controller = container.get(AdminController.class);
    }

    @Override
    public void execute() {
        controller.deleteUser();
    }
}
