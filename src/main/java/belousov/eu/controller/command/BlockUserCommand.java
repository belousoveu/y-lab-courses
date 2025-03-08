package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.AdminController;

public class BlockUserCommand implements CommandMenu {

    AdminController controller;

    public BlockUserCommand(DependencyContainer container) {
        controller = container.get(AdminController.class);
    }

    @Override
    public void execute() {
        controller.blockUser();
    }
}
