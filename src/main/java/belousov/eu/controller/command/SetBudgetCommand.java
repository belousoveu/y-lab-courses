package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.BudgetController;

public class SetBudgetCommand implements CommandMenu {

    private final BudgetController controller;

    public SetBudgetCommand(DependencyContainer container) {
        controller = container.get(BudgetController.class);
    }

    @Override
    public void execute() {
        controller.addBudget();
    }
}
