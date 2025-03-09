package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.ReportController;

public class ViewCategoryReportCommand implements CommandMenu {

    ReportController controller;

    public ViewCategoryReportCommand(DependencyContainer container) {

        controller = container.get(ReportController.class);
    }


    @Override
    public void execute() {
        controller.viewCostsByCategory();
    }
}
