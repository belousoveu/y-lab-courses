package belousov.eu.repository.imp;

import belousov.eu.model.entity.Category;
import belousov.eu.model.entity.User;
import belousov.eu.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления категориями транзакций.
 * Обеспечивает хранение, добавление, удаление и поиск категорий.
 */
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImp implements CategoryRepository {

    private final JdbcTemplate jdbcTemplate;


    /**
     * Находит категорию по ID.
     *
     * @param id ID категории
     * @return Optional с категорией, если найдена, иначе пустой Optional
     */
    @Override
    public Optional<Category> findById(int id) {
        Category category = jdbcTemplate.queryForObject("SELECT * FROM app.categories WHERE id = ?",
                Category.class, id);
        return Optional.ofNullable(category);
    }

    /**
     * Сохраняет категорию в репозитории. Если категория новая (ID = 0), генерирует для неё ID.
     *
     * @param category категория для сохранения
     */
    @Override
    public void save(Category category) {
        if (category.getId() == 0) {
            jdbcTemplate.queryForObject("INSERT INTO app.categories (name, user_id) VALUES (?, ?)",
                    Integer.class, category.getName(), category.getUser().getId());
        } else {
            jdbcTemplate.update("UPDATE app.categories SET name = ?, user_id = ? WHERE id = ?",
                    category.getName(), category.getUser().getId(), category.getId());
        }
    }


    /**
     * Удаляет категорию из репозитория.
     *
     * @param category категория для удаления
     */
    @Override
    public void delete(Category category) {
        jdbcTemplate.update("DELETE FROM app.categories WHERE id = ?", category.getId());
    }

    /**
     * Возвращает список всех категорий для указанного пользователя.
     *
     * @param currentUser пользователь, чьи категории нужно найти
     * @return список категорий пользователя
     */
    @Override
    public List<Category> findAllByUser(User currentUser) {
        return jdbcTemplate.queryForList("SELECT * FROM app.categories WHERE user_id = ?",
                Category.class, currentUser.getId());
    }

    @Override
    public Optional<Category> findByNameAndUser(String categoryName, User user) {
        Category category = jdbcTemplate.queryForObject("SELECT * FROM app.categories WHERE name = ? AND user_id = ?",
                Category.class, categoryName, user.getId());
        return Optional.ofNullable(category);
    }
}
