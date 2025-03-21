package belousov.eu.repository;

import belousov.eu.model.Goal;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления финансовыми целями пользователей.
 * Обеспечивает хранение, добавление, удаление и поиск целей.
 */
@RequiredArgsConstructor
public class GoalRepository {

    private final SessionFactory sessionFactory;


    /**
     * Находит цель по ID.
     *
     * @param id ID цели
     * @return Optional с целью, если найдена, иначе пустой Optional
     */
    public Optional<Goal> findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(Goal.class, id));
        }
    }

    /**
     * Возвращает список всех целей, созданных пользователем.
     *
     * @param userId идентификатор текущего пользователя
     * @return список всех целей пользователя
     */
    public List<Goal> findAllByUser(int userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Goal WHERE user.id = :userId", Goal.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }

    /**
     * Удаляет цель из репозитория.
     *
     * @param goal цель для удаления
     */
    public void delete(Goal goal) {
        sessionFactory.inTransaction(session -> session.remove(goal));
    }

    /**
     * Сохраняет цель в репозитории. Если цель новая (ID = 0), генерирует для неё ID.
     *
     * @param goal цель для сохранения
     */
    public void save(Goal goal) {
        sessionFactory.inTransaction(session -> session.merge(goal));
    }
}
