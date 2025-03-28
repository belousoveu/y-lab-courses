package belousov.eu.repository.imp;

import belousov.eu.model.entity.Budget;
import belousov.eu.model.entity.Category;
import belousov.eu.model.entity.User;
import belousov.eu.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления бюджетами пользователей.
 * Обеспечивает хранение, добавление и поиск бюджетов по категориям и периодам.
 */
@Repository
@RequiredArgsConstructor
public class BudgetRepositoryImp implements BudgetRepository {

    private final JdbcTemplate jdbcTemplate;


    /**
     * Сохраняет бюджет в репозитории. Если бюджет новый (ID = 0), генерирует для него ID.
     *
     * @param budget бюджет для сохранения
     */
    @Override
    public void save(Budget budget) {
        if (budget.getId() == 0) {
            jdbcTemplate.update("""
                                  INSERT INTO app.budgets (period, category_id, user_id, amount)
                            VALUES (?, ?, ?, ?)
                            
                            """,
                    budget.getPeriod(),
                    budget.getCategory().getId(),
                    budget.getUser().getId(),
                    budget.getAmount());
        } else {
            jdbcTemplate.update("""
                                  UPDATE app.budgets SET
                                    period = ?,
                                    category_id = ?,
                                    user_id = ?,
                                    amount = ?
                                WHERE id = ?
                            """,
                    budget.getPeriod(),
                    budget.getCategory().getId(),
                    budget.getUser().getId(),
                    budget.getAmount());
        }
    }

    /**
     * Возвращает список всех бюджетов для указанного пользователя и периода.
     *
     * @param currentUser пользователь, чьи бюджеты нужно найти
     * @param period      период (год и месяц)
     * @return список бюджетов пользователя за указанный период
     */
    @Override
    public List<Budget> findAllByPeriod(User currentUser, YearMonth period) {

        return jdbcTemplate.queryForList("""
                        SELECT b.* FROM app.budgets b WHERE b.user_id = ? AND b.period = ?
                        """,
                Budget.class,
                currentUser.getId(),
                period.atDay(1));
    }

    /**
     * Находит бюджет по категории, пользователю и периоду.
     *
     * @param category категория бюджета
     * @param user     пользователь
     * @param period   период (год и месяц)
     * @return Optional с бюджетом, если найден, иначе пустой Optional
     */
    @Override
    public Optional<Budget> findByCategoryAndPeriod(Category category, User user, YearMonth period) {

        Budget budget = jdbcTemplate.queryForObject("SELECT b.* FROM app.budgets b WHERE b.category_id = ? AND b.user_id = ? AND b.period = ?",
                Budget.class,
                category.getId(),
                user.getId(),
                period.atDay(1));
        return Optional.ofNullable(budget);

    }

}
