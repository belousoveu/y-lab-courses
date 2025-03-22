package belousov.eu.service;

import belousov.eu.model.User;
import belousov.eu.model.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    void addCategory(User user, CategoryDto categoryDto);

    void deleteCategory(int categoryId, User user);

    void editCategory(int categoryId, User user, CategoryDto categoryDto);

    CategoryDto getCategory(int categoryId, User user);

    List<CategoryDto> getAllCategories(User user);
}
