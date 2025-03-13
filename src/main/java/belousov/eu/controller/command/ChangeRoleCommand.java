package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.AdminController;

public class ChangeRoleCommand implements CommandMenu {

    private final AdminController controller;

    public ChangeRoleCommand(DependencyContainer container) {
        controller = container.get(AdminController.class);
    }

    @Override
    public void execute() {
        controller.setRole();
    }
}
