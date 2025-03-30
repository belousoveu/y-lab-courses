package belousov.eu.repository.imp;

import belousov.eu.exception.DatabaseOperationException;
import belousov.eu.model.entity.Budget;
import belousov.eu.model.entity.Category;
import belousov.eu.model.entity.User;
import belousov.eu.repository.BudgetRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления бюджетами пользователей.
 * Обеспечивает хранение, добавление и поиск бюджетов по категориям и периодам.
 */
@Repository
public class BudgetRepositoryImp implements BudgetRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Budget> rowMapper;

    public BudgetRepositoryImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = (rs, rowNum) -> {
            Budget budget = new Budget();
            budget.setId(rs.getInt("id"));
            budget.setPeriod(YearMonth.from(rs.getDate("period").toLocalDate()));
            budget.setAmount(rs.getDouble("amount"));

            Category category = new Category();
            category.setId(rs.getInt("category_id"));
            category.setName(rs.getString("category_name"));
            budget.setCategory(category);

            User user = new User();
            user.setId(rs.getInt("user_id"));
            user.setName(rs.getString("user_name"));
            budget.setUser(user);

            return budget;
        };
    }


    /**
     * Сохраняет бюджет в репозитории. Если бюджет новый (ID = 0), генерирует для него ID.
     *
     * @param budget бюджет для сохранения
     * @return сохраненный бюджет
     */
    @Override
    public Budget save(Budget budget) {
        if (budget.getId() == 0) {
            Integer newId = jdbcTemplate.queryForObject("""
                            INSERT INTO app.budgets (period, category_id, user_id, amount)
                            VALUES (?, ?, ?, ?)
                            RETURNING id
                            """,
                    Integer.class,
                    budget.getPeriod().atDay(1),
                    budget.getCategory().getId(),
                    budget.getUser().getId(),
                    budget.getAmount());
            if (newId == null) {
                throw new DatabaseOperationException("Failed to save budget");
            }
            budget.setId(newId);
        } else {
            jdbcTemplate.update("""
                                  UPDATE app.budgets SET
                                    period = ?,
                                    category_id = ?,
                                    user_id = ?,
                                    amount = ?
                                WHERE id = ?
                            """,
                    budget.getPeriod().atDay(1),
                    budget.getCategory().getId(),
                    budget.getUser().getId(),
                    budget.getAmount());
        }
        return budget;
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

        return jdbcTemplate.query("""
                        SELECT
                            b.id AS id,
                            b.period AS period,
                            b.amount AS amount,
                            c.id AS category_id,
                            c.name AS category_name,
                            u.id AS user_id,
                            u.name AS user_name
                        FROM app.budgets b
                        JOIN app.users u ON b.user_id = u.id
                        JOIN app.categories c on c.id = b.category_id
                        WHERE b.user_id = ? AND b.period = ?
                        """,
                rowMapper,
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
        try {
            Budget budget = jdbcTemplate.queryForObject("""
                            SELECT
                                b.id AS id,
                                b.period AS period,
                                b.amount AS amount,
                                c.id AS category_id,
                                c.name AS category_name,
                                u.id AS user_id,
                                u.name AS user_name
                            FROM app.budgets b
                            JOIN app.users u ON b.user_id = u.id
                            JOIN app.categories c on c.id = b.category_id
                            WHERE b.category_id =? ANd b.user_id = ? AND b.period = ?
                            
                            """,
                    rowMapper,
                    category.
                            getId(),
                    user.getId(),
                    period.atDay(1));
            return Optional.ofNullable(budget);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

}
