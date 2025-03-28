package belousov.eu.controller;

import belousov.eu.model.dto.CategoryDto;
import belousov.eu.model.entity.User;
import belousov.eu.service.CategoryService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    public void addCategory(User user, CategoryDto categoryDto) {
        categoryService.addCategory(user, categoryDto);
    }

    public void deleteCategory(int id, User user) {
        categoryService.deleteCategory(id, user);
    }

    public void editCategory(int id, User user, CategoryDto categoryDto) {
        categoryService.editCategory(id, user, categoryDto);
    }

    public List<CategoryDto> getCategories(User user) {
        return categoryService.getAllCategories(user);
    }
}
