package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.BudgetController;

public class ViewBudgetCommand implements CommandMenu {

    private final BudgetController controller;

    public ViewBudgetCommand(DependencyContainer container) {
        controller = container.get(BudgetController.class);
    }

    @Override
    public void execute() {
        controller.viewBudgetDetails();
    }
}
