package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.CategoryNotFoundException;
import belousov.eu.model.Category;
import belousov.eu.repository.CategoryRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public void addCategory(String name) {
        categoryRepository.save(new Category(0, name, PersonalMoneyTracker.getCurrentUser()));
    }

    @Override
    public void deleteCategory(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        checkForCategoryBelongsToUser(category);
        categoryRepository.delete(category);
    }

    @Override
    public void editCategory(int id, String name) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        checkForCategoryBelongsToUser(category);
        category.setName(name);
        categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByUser(PersonalMoneyTracker.getCurrentUser());
    }

    private void checkForCategoryBelongsToUser(Category category) {
        if (!category.getUser().equals(PersonalMoneyTracker.getCurrentUser())) {
            throw new CategoryNotFoundException(category.getId());
        }
    }
}
