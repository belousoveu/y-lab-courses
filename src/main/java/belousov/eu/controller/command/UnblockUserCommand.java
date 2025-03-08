package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.AdminController;

public class UnblockUserCommand implements CommandMenu {
    AdminController controller;

    public UnblockUserCommand(DependencyContainer container) {
        controller = container.get(AdminController.class);
    }

    @Override
    public void execute() {
        controller.unblockUser();
    }
}
