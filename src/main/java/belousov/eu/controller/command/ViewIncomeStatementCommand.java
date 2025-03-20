package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.ReportController;

public class ViewIncomeStatementCommand implements CommandMenu {

    private final ReportController controller;

    public ViewIncomeStatementCommand(DependencyContainer container) {
        controller = container.get(ReportController.class);
    }

    @Override
    public void execute() {
        controller.viewIncomeStatement();
    }
}
