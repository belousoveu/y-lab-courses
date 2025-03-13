package belousov.eu.controller;

import belousov.eu.service.CategoryService;
import belousov.eu.utils.InputPattern;
import belousov.eu.utils.MessageColor;
import belousov.eu.view.ConsoleView;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ConsoleView consoleView;

    public void addCategory() {
        String name = consoleView.readString("Ведите название категории: ", InputPattern.NAME);
        categoryService.addCategory(name);
        consoleView.println(String.format("Категория %s успешно добавлена", name), MessageColor.CYAN);
    }

    public void deleteCategory() {
        int categoryId = consoleView.readInt("Введите id категории: ", InputPattern.POSITIVE_INTEGER);
        categoryService.deleteCategory(categoryId);
        consoleView.println(String.format("Категория с id %d успешно удалена", categoryId), MessageColor.CYAN);
    }

    public void editCategory() {
        int categoryId = consoleView.readInt("Введите id категории: ", InputPattern.POSITIVE_INTEGER);
        String name = consoleView.readString("Введите новое название категории: ", InputPattern.NAME);
        categoryService.editCategory(categoryId, name);
        consoleView.println(String.format("Категория с id %d успешно отредактирована", categoryId), MessageColor.CYAN);
    }

    public void viewCategories() {
        consoleView.println("Ваши категории:", categoryService.getAllCategories(), MessageColor.WHITE, MessageColor.YELLOW);

    }
}
