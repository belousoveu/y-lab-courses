package belousov.eu.service;

import belousov.eu.model.Category;

import java.util.List;

public interface CategoryService {
    void addCategory(String name);

    void deleteCategory(int categoryId);

    void editCategory(int categoryId, String name);

    List<Category> getAllCategories();
}
