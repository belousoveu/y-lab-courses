package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.TransactionController;

public class UpdateTransactionCommand implements CommandMenu {

    TransactionController controller;

    public UpdateTransactionCommand(DependencyContainer container) {

        controller = container.get(TransactionController.class);
    }

    @Override
    public void execute() {
        controller.updateTransaction();
    }
}
