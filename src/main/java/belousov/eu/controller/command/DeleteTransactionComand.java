package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.TransactionController;

public class DeleteTransactionComand implements CommandMenu {

    TransactionController controller;

    public DeleteTransactionComand(DependencyContainer container) {

        controller = container.get(TransactionController.class);
    }

    @Override
    public void execute() {
        controller.deleteTransaction();
    }
}
