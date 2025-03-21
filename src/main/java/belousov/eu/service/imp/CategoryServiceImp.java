package belousov.eu.service.imp;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.CategoryNotFoundException;
import belousov.eu.model.Category;
import belousov.eu.repository.CategoryRepository;
import belousov.eu.service.CategoryService;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Реализация сервиса для управления категориями.
 * Обеспечивает добавление, удаление, редактирование и получение категорий.
 */
@AllArgsConstructor
public class CategoryServiceImp implements CategoryService {
    /**
     * Репозиторий для работы с категориями.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Добавляет новую категорию.
     *
     * @param name название категории
     */
    @Override
    public void addCategory(String name) {
        categoryRepository.save(new Category(0, name, PersonalMoneyTracker.getCurrentUser()));
    }

    /**
     * Удаляет категорию по ID.
     *
     * @param id ID категории
     * @throws CategoryNotFoundException если категория не найдена или не принадлежит текущему пользователю
     */
    @Override
    public void deleteCategory(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        checkForCategoryBelongsToUser(category);
        categoryRepository.delete(category);
    }

    /**
     * Редактирует категорию по ID.
     *
     * @param id   ID категории
     * @param name новое название категории
     * @throws CategoryNotFoundException если категория не найдена или не принадлежит текущему пользователю
     */
    @Override
    public void editCategory(int id, String name) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        checkForCategoryBelongsToUser(category);
        category.setName(name);
        categoryRepository.save(category);
    }

    /**
     * Возвращает список всех категорий текущего пользователя.
     *
     * @return список категорий
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByUser(PersonalMoneyTracker.getCurrentUser());
    }

    /**
     * Проверяет, принадлежит ли категория текущему пользователю.
     *
     * @param category категория для проверки
     * @throws CategoryNotFoundException если категория не принадлежит текущему пользователю
     */
    private void checkForCategoryBelongsToUser(Category category) {
        if (!category.getUser().equals(PersonalMoneyTracker.getCurrentUser())) {
            throw new CategoryNotFoundException(category.getId());
        }
    }
}
