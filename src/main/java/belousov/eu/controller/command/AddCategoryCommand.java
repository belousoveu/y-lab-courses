package belousov.eu.controller.command;

import belousov.eu.config.DependencyContainer;
import belousov.eu.controller.CategoryController;

public class AddCategoryCommand implements CommandMenu {

    private final CategoryController controller;

    public AddCategoryCommand(DependencyContainer container) {
        controller = container.get(CategoryController.class);
    }

    @Override
    public void execute() {
        controller.addCategory();
    }
}
