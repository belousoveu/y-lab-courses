package belousov.eu.service.imp;

import belousov.eu.exception.CategoryNotFoundException;
import belousov.eu.mapper.CategoryMapper;
import belousov.eu.model.Category;
import belousov.eu.model.User;
import belousov.eu.model.dto.CategoryDto;
import belousov.eu.repository.CategoryRepository;
import belousov.eu.service.CategoryService;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;

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

    private final CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    /**
     * Добавляет новую категорию.
     *
     * @param user текущий авторизованный пользователь
     * @param categoryDto объект с данными по новой категории
     */
    @Override
    public void addCategory(User user, CategoryDto categoryDto) {
        categoryDto.setId(0);
        categoryDto.setUser(user);
        categoryRepository.save(categoryMapper.toEntity(categoryDto));
    }

    /**
     * Удаляет категорию по ID.
     *
     * @param id ID категории
     * @param user текущий авторизованный пользователь
     *
     * @throws CategoryNotFoundException если категория не найдена или не принадлежит текущему пользователю
     */
    @Override
    public void deleteCategory(int id, User user) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        checkForCategoryBelongsToUser(category, user);
        categoryRepository.delete(category);
    }

    /**
     * Редактирует категорию по ID.
     *
     * @param id   ID категории
     * @param user текущий авторизованный пользователь
     * @param categoryDto объект с обновленными данными о категории
     *
     * @throws CategoryNotFoundException если категория не найдена или не принадлежит текущему пользователю
     */
    @Override
    public void editCategory(int id, User user, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        checkForCategoryBelongsToUser(category, user);
        categoryDto.setId(id);
        categoryDto.setUser(user);
        categoryRepository.save(categoryMapper.toEntity(categoryDto));
    }

    /**
     * Возвращает список всех категорий текущего пользователя.
     *
     * @return список категорий
     */
    @Override
    public List<CategoryDto> getAllCategories(User user) {
        return categoryRepository.findAllByUser(user)
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }


    /**
     * Возвращает категорию по ID.
     *
     * @param categoryId ID категории
     * @param user       текущий авторизованный пользователь
     * @return объект CategoryDto с данными о категории
     */
    @Override
    public CategoryDto getCategory(int categoryId, User user) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        checkForCategoryBelongsToUser(category, user);
        return categoryMapper.toDto(category);
    }

    @Override
    public Category getCategoryByName(String categoryName, User user) {
        return categoryRepository.findByNameAndUser(categoryName, user)
                .orElseThrow(() -> new CategoryNotFoundException(categoryName, user.getId()));
    }

    /**
     * Проверяет, принадлежит ли категория текущему пользователю.
     *
     * @param category категория для проверки
     * @param user текущий авторизованный пользователь
     * @throws CategoryNotFoundException если категория не принадлежит текущему пользователю
     */
    private void checkForCategoryBelongsToUser(Category category, User user) {
        if (!category.getUser().equals(user)) {
            throw new CategoryNotFoundException(category.getId());
        }
    }
}
