package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.ReportController;

public class ViewBalancCommand implements CommandMenu {

    ReportController controller;

    public ViewBalancCommand(DependencyContainer container) {
        controller = container.get(ReportController.class);
    }

    @Override
    public void execute() {
        controller.viewCurrentBalance();
    }
}
