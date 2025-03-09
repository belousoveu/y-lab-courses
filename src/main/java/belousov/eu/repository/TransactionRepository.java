package belousov.eu.repository;

import belousov.eu.model.OperationType;
import belousov.eu.model.Transaction;
import belousov.eu.model.User;
import belousov.eu.utils.IdGenerator;
import lombok.AllArgsConstructor;

import java.util.*;

/**
 * Репозиторий для управления транзакциями.
 * Обеспечивает хранение, добавление, удаление и поиск транзакций, а также расчёт текущего баланса пользователя.
 */
@AllArgsConstructor
public class TransactionRepository {

    private final Map<Integer, Transaction> transactions = new HashMap<>();
    private final IdGenerator<Integer> idCounter = IdGenerator.create(Integer.class);

    /**
     * Возвращает список всех транзакций.
     *
     * @return список всех транзакций
     */
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    /**
     * Возвращает транзакцию по её ID.
     *
     * @param id ID транзакции
     * @return найденная транзакция или Optional.empty(), если транзакция не найдена
     */
    public Optional<Transaction> findById(int id) {
        return Optional.ofNullable(transactions.get(id));
    }

    /**
     * Сохраняет транзакцию в репозитории. Если транзакция новая (ID = 0), генерирует для неё ID.
     *
     * @param transaction транзакция для сохранения
     * @return сохранённая транзакция
     */
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == 0) {
            transaction.setId(idCounter.nextId());
        }
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    /**
     * Удаляет транзакцию из репозитория.
     *
     * @param transaction транзакция для удаления
     */
    public void delete(Transaction transaction) {
        transactions.remove(transaction.getId());
    }

    /**
     * Вычисляет текущий баланс пользователя на основе его транзакций.
     *
     * @param currentUser текущий пользователь
     * @return текущий баланс пользователя
     */
    public double getCurrentBalance(User currentUser) {
        double totalDeposit = transactions.values().stream()
                .filter(transaction -> transaction.getUser().equals(currentUser) && transaction.getOperationType() == OperationType.DEPOSIT)
                .mapToDouble(Transaction::getAmount).sum();
        double totalWithdraw = transactions.values().stream()
                .filter(transaction -> transaction.getUser().equals(currentUser) && transaction.getOperationType() == OperationType.WITHDRAW)
                .mapToDouble(Transaction::getAmount).sum();
        return totalDeposit - totalWithdraw;
    }
}
