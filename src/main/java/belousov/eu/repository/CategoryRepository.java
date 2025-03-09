package belousov.eu.repository;

import belousov.eu.model.Category;
import belousov.eu.model.User;
import belousov.eu.utils.IdGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий для управления категориями транзакций.
 * Обеспечивает хранение, добавление, удаление и поиск категорий.
 */
public class CategoryRepository {

    private final Map<Integer, Category> categories = new HashMap<>();
    private final IdGenerator<Integer> idCounter = IdGenerator.create(Integer.class);

    /**
     * Находит категорию по ID.
     *
     * @param id ID категории
     * @return Optional с категорией, если найдена, иначе пустой Optional
     */
    public Optional<Category> findById(int id) {
        return Optional.ofNullable(categories.get(id));
    }

    /**
     * Сохраняет категорию в репозитории. Если категория новая (ID = 0), генерирует для неё ID.
     *
     * @param category категория для сохранения
     */
    public void save(Category category) {
        if (category.getId() == 0) {
            category.setId(idCounter.nextId());
        }
        categories.put(category.getId(), category);
    }

    /**
     * Удаляет категорию из репозитория.
     *
     * @param category категория для удаления
     */
    public void delete(Category category) {
        categories.remove(category.getId());
    }

    /**
     * Возвращает список всех категорий для указанного пользователя.
     *
     * @param currentUser пользователь, чьи категории нужно найти
     * @return список категорий пользователя
     */
    public List<Category> findAllByUser(User currentUser) {
        return categories.values().stream().filter(category -> category.getUser().equals(currentUser)).toList();
    }
}
