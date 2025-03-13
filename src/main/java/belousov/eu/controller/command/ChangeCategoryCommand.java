package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.CategoryController;

public class ChangeCategoryCommand implements CommandMenu {

    private final CategoryController controller;

    public ChangeCategoryCommand(DependencyContainer container) {
        controller = container.get(CategoryController.class);
    }

    @Override
    public void execute() {
        controller.editCategory();
    }
}
