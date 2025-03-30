package belousov.eu.repository.imp;

import belousov.eu.exception.DatabaseOperationException;
import belousov.eu.model.entity.Goal;
import belousov.eu.model.entity.User;
import belousov.eu.repository.GoalRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления финансовыми целями пользователей.
 * Обеспечивает хранение, добавление, удаление и поиск целей.
 */
@Repository
public class GoalRepositoryImp implements GoalRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Goal> rowMapper;

    public GoalRepositoryImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = (rs, rowNum) -> {
            Goal goal = new Goal();
            goal.setId(rs.getInt("id"));
            goal.setName(rs.getString("name"));
            goal.setDescription(rs.getString("description"));
            goal.setPoint(rs.getDouble("point"));

            User user = new User();
            user.setId(rs.getInt("user_id"));
            user.setName(rs.getString("user_name"));

            goal.setUser(user);
            return goal;
        };
    }


    /**
     * Находит цель по ID.
     *
     * @param id ID цели
     * @return Optional с целью, если найдена, иначе пустой Optional
     */
    @Override
    public Optional<Goal> findById(int id) {
        try {
            Goal goal = jdbcTemplate.queryForObject("""
                            SELECT
                                g.id AS id,
                                g.name AS name,
                                g.description AS description,
                                g.point AS point,
                                u.id AS user_id,
                                u.name AS user_name
                            FROM app.goals g
                            JOIN app.users u ON g.user_id=u.id
                            WHERE g.id = ?
                            """,
                    rowMapper, id);
            return Optional.ofNullable(goal);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Возвращает список всех целей, созданных пользователем.
     *
     * @param userId идентификатор текущего пользователя
     * @return список всех целей пользователя
     */
    @Override
    public List<Goal> findAllByUser(int userId) {
        return jdbcTemplate.query("""
                        SELECT
                            g.id,
                            g.name,
                            g.description,
                            g.point,
                            u.id AS user_id,
                            u.name AS user_name
                        FROM app.goals g
                        JOIN app.users u ON g.user_id=u.id
                        WHERE u.id = ?
                        """,
                rowMapper, userId);
    }

    /**
     * Удаляет цель из репозитория.
     *
     * @param goal цель для удаления
     */
    @Override
    public void delete(Goal goal) {
        jdbcTemplate.update("DELETE FROM app.goals WHERE id = ?", goal.getId());
    }

    /**
     * Сохраняет цель в репозитории. Если цель новая (ID = 0), генерирует для неё ID.
     *
     * @param goal цель для сохранения
     */
    @Override
    public Goal save(Goal goal) {

        if (goal.getId() == 0) {
            Integer newId = jdbcTemplate.queryForObject(
                    "INSERT INTO app.goals (user_id, name, description, point) VALUES (?, ?, ?, ?) RETURNING id",
                    Integer.class,
                    goal.getUser().getId(), goal.getName(), goal.getDescription(), goal.getPoint());
            if (newId == null) {
                throw new DatabaseOperationException("Failed to save goal");
            }
            goal.setId(newId);
        } else {
            jdbcTemplate.update("UPDATE app.goals SET user_id = ?, name = ?, description = ?, point = ? WHERE id = ?",
                    goal.getUser().getId(), goal.getName(), goal.getDescription(), goal.getPoint(), goal.getId());
        }
        return goal;
    }
}
