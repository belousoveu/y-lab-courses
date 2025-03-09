package belousov.eu.repository;

import belousov.eu.model.Goal;
import belousov.eu.model.User;
import belousov.eu.utils.IdGenerator;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий для управления финансовыми целями пользователей.
 * Обеспечивает хранение, добавление, удаление и поиск целей.
 */
@NoArgsConstructor
public class GoalRepository {

    private final Map<Integer, Goal> goals = new HashMap<>();
    private final IdGenerator<Integer> idCounter = IdGenerator.create(Integer.class);

    /**
     * Находит цель по ID.
     *
     * @param id ID цели
     * @return Optional с целью, если найдена, иначе пустой Optional
     */
    public Optional<Goal> findById(int id) {
        return Optional.ofNullable(goals.get(id));
    }

    /**
     * Возвращает список всех целей, созданных пользователем.
     *
     * @param currentUser текущий пользователь
     * @return список всех целей пользователя
     */
    public List<Goal> findAllByUser(User currentUser) {
        return goals.values().stream().filter(goal -> goal.getUser().equals(currentUser)).toList();
    }

    /**
     * Удаляет цель из репозитория.
     *
     * @param goal цель для удаления
     */
    public void delete(Goal goal) {
        goals.remove(goal.getId());
    }

    /**
     * Сохраняет цель в репозитории. Если цель новая (ID = 0), генерирует для неё ID.
     *
     * @param goal цель для сохранения
     */
    public void save(Goal goal) {
        if (goal.getId() == 0) {
            goal.setId(idCounter.nextId());
        }
        goals.put(goal.getId(), goal);
    }
}
