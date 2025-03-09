package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.TransactionController;

public class AddTransactionCommand implements CommandMenu {

    TransactionController controller;

    public AddTransactionCommand(DependencyContainer container) {
        controller = container.get(TransactionController.class);
    }

    @Override
    public void execute() {
        controller.createTransaction();
    }
}
