package belousov.eu.repository.imp;

import belousov.eu.model.entity.OperationType;
import belousov.eu.model.entity.Transaction;
import belousov.eu.model.entity.User;
import belousov.eu.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления транзакциями.
 * Обеспечивает хранение, добавление, удаление и поиск транзакций, а также расчёт текущего баланса пользователя.
 */
@Repository
@AllArgsConstructor
public class TransactionRepositoryImp implements TransactionRepository {

    private final JdbcTemplate jdbcTemplate;


    /**
     * Возвращает список всех транзакций.
     *
     * @return список всех транзакций
     */
    @Override
    public List<Transaction> findAll() {
        return jdbcTemplate.queryForList("select * from app.transactions", Transaction.class);
    }

    /**
     * Возвращает транзакцию по её ID.
     *
     * @param id ID транзакции
     * @return найденная транзакция или Optional.empty(), если транзакция не найдена
     */
    @Override
    public Optional<Transaction> findById(int id) {
        Transaction transaction = jdbcTemplate.queryForObject("SELECT * FROM app.transactions WHERE id = ?",
                Transaction.class,
                id);
        return Optional.ofNullable(transaction);
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
            int id = jdbcTemplate.update("""
                            INSERT INTO app.transactions (date, operation_type, category_id,  user_id, amount, description)
                            VALUES (?, ?, ?, ?, ?, ?) RETURNING id
                            """,
                    transaction.getDate(),
                    transaction.getOperationType().toString(),
                    transaction.getCategory().getId(),
                    transaction.getUser().getId(),
                    transaction.getAmount(),
                    transaction.getDescription());
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
                SELECT SUM(CASE WHEN t.operation_type = :deposit THEN t.amount ELSE 0 END)
                - SUM(CASE WHEN t.operation_type != :deposit THEN t.amount ELSE 0 END)
                FROM app.transactions t
                WHERE t.user_id =?
                """, Double.class, currentUser.getId(), new Object[]{"deposit", OperationType.DEPOSIT.toString()});
        return balance == null ? 0 : balance;
    }
}
