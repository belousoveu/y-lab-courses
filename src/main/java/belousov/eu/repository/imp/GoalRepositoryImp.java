package belousov.eu.repository.imp;

import belousov.eu.model.entity.Goal;
import belousov.eu.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления финансовыми целями пользователей.
 * Обеспечивает хранение, добавление, удаление и поиск целей.
 */
@Repository
@RequiredArgsConstructor
public class GoalRepositoryImp implements GoalRepository {

    private final JdbcTemplate jdbcTemplate;


    /**
     * Находит цель по ID.
     *
     * @param id ID цели
     * @return Optional с целью, если найдена, иначе пустой Optional
     */
    @Override
    public Optional<Goal> findById(int id) {
        Goal goal = jdbcTemplate.queryForObject("SELECT * FROM app.goals WHERE id = ?", Goal.class, id);
        return Optional.ofNullable(goal);
    }

    /**
     * Возвращает список всех целей, созданных пользователем.
     *
     * @param userId идентификатор текущего пользователя
     * @return список всех целей пользователя
     */
    @Override
    public List<Goal> findAllByUser(int userId) {
        return jdbcTemplate.queryForList("SELECT * FROM app.goals WHERE user_id = ?", Goal.class, userId);
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
    public void save(Goal goal) {

        if (goal.getId() == 0) {
            jdbcTemplate.update("INSERT INTO app.goals (user_id, name, description, point) VALUES (?, ?, ?, ?)",
                    goal.getUser().getId(), goal.getName(), goal.getDescription(), goal.getPoint());
        } else {
            jdbcTemplate.update("UPDATE app.goals SET user_id = ?, name = ?, description = ?, point = ? WHERE id = ?",
                    goal.getUser().getId(), goal.getName(), goal.getDescription(), goal.getPoint(), goal.getId());
        }
    }
}
