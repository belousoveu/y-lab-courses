package belousov.eu.repository.imp;

import belousov.eu.exception.DatabaseOperationException;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import belousov.eu.repository.UserRepository;
import belousov.eu.utils.Password;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления пользователями.
 * Обеспечивает хранение, добавление, удаление и поиск пользователей.
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImp implements UserRepository {

    private final JdbcTemplate jdbcTemplate;


    /**
     * Инициализация репозитория. Добавляет администратора по умолчанию.
     */
    @PostConstruct
    private void init() {
        if (getAllAdminIds().isEmpty()) {
            String encodedPassword = Password.encode("Admin123");
            save(new User(0, "admin", "admin@admin.com", encodedPassword, Role.ADMIN, true));
        }
    }

    /**
     * Сохраняет или обновляет пользователя в базе данных.
     *
     * @param user - пользователь для сохранения или обновления
     * @return - сохраненный или обновленный пользователь
     */
    @Override
    public User save(@NotNull User user) {

        String sql = """
                INSERT INTO app.users (id, name, email, password, role, is_active)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO
                UPDATE SET name=?, email=?, password=?, role=?, is_active=?
                RETURNING id
                """;

        Integer userId = jdbcTemplate.queryForObject(sql, Integer.class,
                user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.getRole().toString(), user.isActive());

        if (userId == null) {
            throw new DatabaseOperationException("Не получилось сохранить пользователя %s".formatted(user.getName()));
        }

        user.setId(userId);

        return user;
    }

    /**
     * Удаляет пользователя из репозитория.
     *
     * @param user пользователь для удаления
     */
    @Override
    public void delete(User user) {
        jdbcTemplate.update("DELETE FROM app.users WHERE id=?", user.getId());
    }

    /**
     * Находит пользователя по его ID.
     *
     * @param id ID пользователя
     * @return найденный пользователь или Optional.empty(), если пользователь не найден
     */
    @Override
    public Optional<User> findById(int id) {
        User user = jdbcTemplate.queryForObject("SELECT * FROM app.users WHERE id=?", User.class, id);
        return Optional.ofNullable(user);
    }

    /**
     * Находит пользователя по его email.
     *
     * @param email email пользователя
     * @return найденный пользователь или Optional.empty(), если пользователь не найден
     */
    @Override
    public Optional<User> findByEmail(String email) {
        User user = jdbcTemplate.queryForObject("SELECT * FROM app.users WHERE email=?", User.class, email);
        return Optional.ofNullable(user);
    }


    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей
     */
    @Override
    public List<User> findAll() {
        return jdbcTemplate.queryForList("SELECT * FROM app.users", User.class);
    }

    /**
     * Возвращает список всех ID администраторов.
     *
     * @return список всех ID администраторов
     */
    @Override
    public List<Integer> getAllAdminIds() {
        return jdbcTemplate.queryForList("SELECT id FROM app.users WHERE role='ADMIN'", Integer.class);
    }

}
