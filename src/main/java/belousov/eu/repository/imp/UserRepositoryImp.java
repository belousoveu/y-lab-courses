package belousov.eu.repository.imp;

import belousov.eu.exception.DatabaseOperationException;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import belousov.eu.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления пользователями.
 * Обеспечивает хранение, добавление, удаление и поиск пользователей.
 */
@Repository
public class UserRepositoryImp implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> rowMapper;

    public UserRepositoryImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRole(Role.valueOf(rs.getString("role")));
            user.setActive(rs.getBoolean("is_active"));
            return user;
        };
    }


    /**
     * Инициализация репозитория. Добавляет администратора по умолчанию.
     */
//    @PostConstruct
//    private void init() {
//        if (getAllAdminIds().isEmpty()) {
//            String encodedPassword = Password.encode("Admin123");
//            save(new User(0, "admin", "admin@admin.com", encodedPassword, Role.ADMIN, true));
//        }
//    }

    /**
     * Сохраняет или обновляет пользователя в базе данных.
     *
     * @param user - пользователь для сохранения или обновления
     * @return - сохраненный или обновленный пользователь
     */
    @Override
    public User save(@NotNull User user) {

        if (user.getId() == 0) {
            Integer newId = jdbcTemplate.queryForObject("""
                            INSERT INTO app.users (name, email, password, role, is_active)
                            VALUES (?, ?, ?, ?, ?)
                            RETURNING id
                            """,
                    Integer.class,
                    user.getName(), user.getEmail(), user.getPassword(), user.getRole().toString(), user.isActive());
            if (newId == null) {
                throw new DatabaseOperationException("Failed to save user");
            }
            user.setId(newId);
        } else {
            jdbcTemplate.update("""
                    UPDATE app.users
                    SET name=?, email=?, password=?, role=?, is_active=?
                    WHERE id=?
                    """, user.getName(), user.getEmail(), user.getPassword(), user.getRole().toString(), user.isActive(), user.getId());
        }

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
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM app.users WHERE id=?", rowMapper, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Находит пользователя по его email.
     *
     * @param email email пользователя
     * @return найденный пользователь или Optional.empty(), если пользователь не найден
     */
    @Override
    public Optional<User> findByEmail(String email) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM app.users WHERE email=?", rowMapper, email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей
     */
    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM app.users", rowMapper);
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
