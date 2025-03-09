package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.AdminController;

public class ListOperationsCommand implements CommandMenu {

    private final AdminController controller;

    public ListOperationsCommand(DependencyContainer container) {
        controller = container.get(AdminController.class);
    }

    @Override
    public void execute() {
        controller.getTransactions();
    }
}
