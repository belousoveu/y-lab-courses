package belousov.eu.repository.imp;

import belousov.eu.exception.DatabaseOperationException;
import belousov.eu.model.entity.Category;
import belousov.eu.model.entity.User;
import belousov.eu.repository.CategoryRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления категориями транзакций.
 * Обеспечивает хранение, добавление, удаление и поиск категорий.
 */
@Repository
public class CategoryRepositoryImp implements CategoryRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Category> rowMapper;

    public CategoryRepositoryImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = (rs, rowNum) -> {
            Category category = new Category();
            category.setId(rs.getInt("category_id"));
            category.setName(rs.getString("category_name"));

            User user = new User();
            user.setId(rs.getInt("user_id"));
            user.setName(rs.getString("user_name"));
            category.setUser(user);
            return category;
        };
    }


    /**
     * Находит категорию по ID.
     *
     * @param id ID категории
     * @return Optional с категорией, если найдена, иначе пустой Optional
     */
    @Override
    public Optional<Category> findById(int id) {
        try {
            Category category = jdbcTemplate.queryForObject("""
                            SELECT
                                c.id AS category_id,
                                c.name AS category_name,
                                u.id AS user_id,
                                u.name AS user_name
                            FROM app.categories c
                            JOIN app.users u ON c.user_id=u.id
                            WHERE c.id = ?
                            """,
                    rowMapper, id);
            return Optional.ofNullable(category);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    /**
     * Сохраняет категорию в репозитории. Если категория новая (ID = 0), генерирует для неё ID.
     *
     * @param category категория для сохранения
     * @return сохранённая категория
     */
    @Override
    public Category save(Category category) {
        if (category.getId() == 0) {
            Integer newId = jdbcTemplate.queryForObject("INSERT INTO app.categories (name, user_id) VALUES (?, ?) RETURNING id",
                    Integer.class, category.getName(), category.getUser().getId());
            if (newId == null) {
                throw new DatabaseOperationException("Failed to save category");
            }
            category.setId(newId);
        } else {
            jdbcTemplate.update("UPDATE app.categories SET name = ?, user_id = ? WHERE id = ?",
                    category.getName(), category.getUser().getId(), category.getId());
        }
        return category;
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
        return jdbcTemplate.query("""
                        SELECT
                            c.id AS category_id,
                            c.name AS category_name,
                            u.id AS user_id,
                            u.name AS user_name
                        FROM app.categories c
                        JOIN app.users u ON c.user_id=u.id
                        WHERE u.id = ?
                        """,
                rowMapper, currentUser.getId());
    }

    @Override
    public Optional<Category> findByNameAndUser(String categoryName, User user) {
        Category category = jdbcTemplate.queryForObject("""
                        SELECT
                            c.id AS category_id,
                            c.name AS category_name,
                            u.id AS user_id,
                            u.name AS user_name
                        FROM app.categories c
                        JOIN app.users u ON c.user_id=u.id
                        WHERE
                            c.name=? AND u.id = ?
                        """,
                Category.class, categoryName, user.getId());
        return Optional.ofNullable(category);
    }
}
