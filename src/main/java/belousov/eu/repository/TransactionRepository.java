package belousov.eu.repository;

import belousov.eu.model.OperationType;
import belousov.eu.model.Transaction;
import belousov.eu.model.User;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления транзакциями.
 * Обеспечивает хранение, добавление, удаление и поиск транзакций, а также расчёт текущего баланса пользователя.
 */
@AllArgsConstructor
public class TransactionRepository {

    private final SessionFactory sessionFactory;


    /**
     * Возвращает список всех транзакций.
     *
     * @return список всех транзакций
     */
    public List<Transaction> findAll() {

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Transaction", Transaction.class).getResultList();
        }
    }

    /**
     * Возвращает транзакцию по её ID.
     *
     * @param id ID транзакции
     * @return найденная транзакция или Optional.empty(), если транзакция не найдена
     */
    public Optional<Transaction> findById(int id) {

        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(Transaction.class, id));
        }
    }

    /**
     * Сохраняет транзакцию в репозитории. Если транзакция новая (ID = 0), генерирует для неё ID.
     *
     * @param transaction транзакция для сохранения
     * @return сохранённая транзакция
     */
    public Transaction save(Transaction transaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Transaction savedTransaction = session.merge(transaction);
            session.getTransaction().commit();
            return savedTransaction;
        }

    }

    /**
     * Удаляет транзакцию из репозитория.
     *
     * @param transaction транзакция для удаления
     */
    public void delete(Transaction transaction) {
        sessionFactory.inTransaction(session -> session.remove(transaction));
    }

    /**
     * Вычисляет текущий баланс пользователя на основе его транзакций.
     *
     * @param currentUser текущий пользователь
     * @return текущий баланс пользователя
     */
    public double getCurrentBalance(User currentUser) {

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("""
                            SELECT sum(CASE WHEN t.operationType = :deposit THEN t.amount ELSE 0 END)
                            - sum(CASE WHEN t.operationType != :deposit THEN t.amount ELSE 0 END)
                            from Transaction t
                            where t.user = :currentUser
                            """, Double.class)
                    .setParameter("currentUser", currentUser)
                    .setParameter("deposit", OperationType.DEPOSIT)
                    .getSingleResult();
        }
    }
}
