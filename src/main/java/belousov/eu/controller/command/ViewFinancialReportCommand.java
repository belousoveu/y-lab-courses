package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.ReportController;

public class ViewFinancialReportCommand implements CommandMenu {

    ReportController controller;

    public ViewFinancialReportCommand(DependencyContainer container) {

        controller = container.get(ReportController.class);
    }

    @Override
    public void execute() {
        controller.viewBalanceSheet();
    }
}
