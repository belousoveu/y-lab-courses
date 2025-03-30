package belousov.eu.repository.imp;

import belousov.eu.exception.DatabaseOperationException;
import belousov.eu.model.entity.Category;
import belousov.eu.model.entity.OperationType;
import belousov.eu.model.entity.Transaction;
import belousov.eu.model.entity.User;
import belousov.eu.repository.TransactionRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления транзакциями.
 * Обеспечивает хранение, добавление, удаление и поиск транзакций, а также расчёт текущего баланса пользователя.
 */
@Repository
public class TransactionRepositoryImp implements TransactionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Transaction> rowMapper;

    public TransactionRepositoryImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = (rs, rowNum) -> {
            Transaction transaction = new Transaction();
            transaction.setId(rs.getInt("id"));
            transaction.setDate(rs.getDate("date").toLocalDate());
            transaction.setAmount(rs.getDouble("amount"));
            transaction.setDescription(rs.getString("description"));
            transaction.setOperationType(OperationType.valueOf(rs.getString("operation_type")));

            Category category = new Category();
            category.setId(rs.getInt("category_id"));
            category.setName(rs.getString("category_name"));
            transaction.setCategory(category);

            User user = new User();
            user.setId(rs.getInt("user_id"));
            user.setName(rs.getString("user_name"));
            transaction.setUser(user);
            return transaction;
        };
    }


    /**
     * Возвращает список всех транзакций.
     *
     * @return список всех транзакций
     */
    @Override
    public List<Transaction> findAll() {
        return jdbcTemplate.query("""
                                SELECT
                                    t.id AS id,
                                    t.date AS date,
                                    t.amount AS amount,
                                    t.description AS description,
                                    t.operation_type AS operation_type,
                                    c.id AS category_id,
                                    c.name AS category_name,
                                    u.id AS user_id,
                                    u.name AS user_name
                                FROM app.transactions t
                            JOIN app.categories c ON t.category_id=c.id
                            JOIN app.users u ON t.user_id=u.id
                        """,
                rowMapper);
    }

    /**
     * Возвращает транзакцию по её ID.
     *
     * @param id ID транзакции
     * @return найденная транзакция или Optional.empty(), если транзакция не найдена
     */
    @Override
    public Optional<Transaction> findById(int id) {
        try {
            Transaction transaction = jdbcTemplate.queryForObject("""
                            
                                SELECT
                                t.id AS id,
                                t.date AS date,
                                t.amount AS amount,
                                t.description AS description,
                                t.operation_type AS operation_type,
                                c.id AS category_id,
                                c.name AS category_name,
                                u.id AS user_id,
                                u.name AS user_name
                            FROM app.transactions t
                            JOIN app.categories c ON t.category_id=c.id
                            JOIN app.users u ON t.user_id=u.id
                            WHERE t.id = ?
                            
                            """,
                    rowMapper,
                    id);
            return Optional.ofNullable(transaction);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Сохраняет транзакцию в репозитории. Если транзакция новая (ID = 0), генерирует для неё ID.
     *
     * @param transaction транзакция для сохранения
     * @return сохранённая транзакция
     */
    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == 0) {
            Integer id = jdbcTemplate.queryForObject("""
                            INSERT INTO app.transactions (date, operation_type, category_id,  user_id, amount, description)
                            VALUES (?, ?, ?, ?, ?, ?) RETURNING id
                            """,
                    Integer.class,
                    transaction.getDate(),
                    transaction.getOperationType().toString(),
                    transaction.getCategory().getId(),
                    transaction.getUser().getId(),
                    transaction.getAmount(),
                    transaction.getDescription());
            if (id == null) {
                throw new DatabaseOperationException("Failed to save transaction");
            }
            transaction.setId(id);
        } else {
            jdbcTemplate.update("""
                            UPDATE app.transactions SET date = ?, operation_type = ?, category_id = ?, user_id = ?, amount = ?, description = ?
                            WHERE id = ?
                            """,
                    transaction.getDate(),
                    transaction.getOperationType().toString(),
                    transaction.getCategory().getId(),
                    transaction.getUser().getId(),
                    transaction.getAmount(),
                    transaction.getDescription(),
                    transaction.getId());
        }
        return transaction;
    }

    /**
     * Удаляет транзакцию из репозитория.
     *
     * @param transaction транзакция для удаления
     */
    @Override
    public void delete(Transaction transaction) {
        jdbcTemplate.update("DELETE FROM app.transactions WHERE id = ?", transaction.getId());
    }

    /**
     * Вычисляет текущий баланс пользователя на основе его транзакций.
     *
     * @param currentUser текущий пользователь
     * @return текущий баланс пользователя
     */
    @Override
    public double getCurrentBalance(User currentUser) {
        Double balance = jdbcTemplate.queryForObject("""
                SELECT SUM(CASE WHEN t.operation_type =? THEN t.amount ELSE 0 END)
                - SUM(CASE WHEN t.operation_type !=? THEN t.amount ELSE 0 END)
                FROM app.transactions t
                WHERE t.user_id =?
                """, Double.class, OperationType.DEPOSIT.toString(), OperationType.DEPOSIT.toString(), currentUser.getId());
        return balance == null ? 0 : balance;
    }
}
