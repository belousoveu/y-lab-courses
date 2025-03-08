package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.GoalController;

public class ViewGoalCommand implements CommandMenu {

    private final GoalController controller;

    public ViewGoalCommand(DependencyContainer container) {
        controller = container.get(GoalController.class);
    }

    @Override
    public void execute() {
        controller.viewGoals();
    }
}
