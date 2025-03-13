package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.TransactionController;

public class ViewTransactionCommand implements CommandMenu {

    private final TransactionController controller;

    public ViewTransactionCommand(DependencyContainer container) {

        controller = container.get(TransactionController.class);
    }

    @Override
    public void execute() {
        controller.getTransactions();
    }
}
