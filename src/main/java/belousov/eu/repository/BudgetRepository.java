package belousov.eu.repository;

import belousov.eu.model.Budget;
import belousov.eu.model.Category;
import belousov.eu.model.User;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления бюджетами пользователей.
 * Обеспечивает хранение, добавление и поиск бюджетов по категориям и периодам.
 */
@RequiredArgsConstructor
public class BudgetRepository {

    private final SessionFactory sessionFactory;


    /**
     * Сохраняет бюджет в репозитории. Если бюджет новый (ID = 0), генерирует для него ID.
     *
     * @param budget бюджет для сохранения
     */
    public void save(Budget budget) {
        sessionFactory.inTransaction(session -> session.merge(budget));
    }

    /**
     * Возвращает список всех бюджетов для указанного пользователя и периода.
     *
     * @param currentUser пользователь, чьи бюджеты нужно найти
     * @param period      период (год и месяц)
     * @return список бюджетов пользователя за указанный период
     */
    public List<Budget> findAllByPeriod(User currentUser, YearMonth period) {

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("""
                            SELECT b
                            FROM Budget b
                            WHERE b.user = :currentUser AND b.period = :period
                            """, Budget.class)
                    .setParameter("currentUser", currentUser)
                    .setParameter("period", period.atDay(1))
                    .getResultList();
        }

    }

    /**
     * Находит бюджет по категории, пользователю и периоду.
     *
     * @param category категория бюджета
     * @param user     пользователь
     * @param period   период (год и месяц)
     * @return Optional с бюджетом, если найден, иначе пустой Optional
     */
    public Optional<Budget> findByCategoryAndPeriod(Category category, User user, YearMonth period) {

        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(
                    session.createQuery("""
                                    
                                                    SELECT b
                                    FROM Budget b
                                    WHERE b.category = :category AND b.user = :user AND b.period = :period
                                    """, Budget.class)
                            .setParameter("category", category)
                            .setParameter("user", user)
                            .setParameter("period", period.atDay(1))
                            .getSingleResultOrNull());

        }
    }

}
