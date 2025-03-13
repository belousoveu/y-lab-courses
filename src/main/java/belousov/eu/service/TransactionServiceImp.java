package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.TransactionNotFoundException;
import belousov.eu.model.*;
import belousov.eu.model.report_dto.IncomeStatement;
import belousov.eu.observer.BalanceChangeSubject;
import belousov.eu.repository.TransactionRepository;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для управления транзакциями.
 * Обеспечивает добавление, обновление, удаление и поиск транзакций, а также формирование отчётов.
 */
@AllArgsConstructor
public class TransactionServiceImp implements TransactionService, AdminAccessTransactionService, ReportService {
    /**
     * Репозиторий для хранения транзакций.
     */
    private final TransactionRepository transactionRepository;
    /**
     * Наблюдатель за изменением баланса
     */
    private final BalanceChangeSubject balanceChangeSubject;

    /**
     * Добавляет новую транзакцию.
     *
     * @param date        дата транзакции
     * @param type        тип операции (доход или расход)
     * @param category    категория транзакции
     * @param amount      сумма транзакции
     * @param description описание транзакции
     * @return добавленная транзакция
     */
    @Override
    public Transaction addTransaction(LocalDate date, OperationType type, Category category, double amount, String description) {
        Transaction transaction = transactionRepository
                .save(new Transaction(0, date, type, category, amount, description, PersonalMoneyTracker.getCurrentUser()));
        balanceChangeSubject.notifyObservers(transaction);
        return transaction;
    }

    /**
     * Обновляет существующую транзакцию.
     *
     * @param id          ID транзакции
     * @param category    новая категория
     * @param amount      новая сумма
     * @param description новое описание
     * @return обновлённая транзакция
     * @throws TransactionNotFoundException если транзакция не найдена или не принадлежит текущему пользователю
     */
    @Override
    public Transaction updateTransaction(int id, Category category, double amount, String description) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        checkTransactionBelongsToCurrentUser(transaction);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        balanceChangeSubject.notifyObservers(updatedTransaction);
        return updatedTransaction;
    }

    /**
     * Удаляет транзакцию по ID.
     *
     * @param id ID транзакции
     * @throws TransactionNotFoundException если транзакция не найдена или не принадлежит текущему пользователю
     */
    @Override
    public void deleteTransaction(int id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        checkTransactionBelongsToCurrentUser(transaction);
        transactionRepository.delete(transaction);
        balanceChangeSubject.notifyObservers(transaction);
    }

    /**
     * Возвращает список транзакций, соответствующих фильтру.
     *
     * @param filter фильтр для поиска транзакций
     * @return список транзакций
     */
    @Override
    public List<Transaction> getTransactions(TransactionFilter filter) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getUser().equals(filter.getUser()))
                .filter(t -> filter.getFrom() == null || !filter.getFrom().isAfter(t.getDate()))
                .filter(t -> filter.getTo() == null || !filter.getTo().isBefore(t.getDate()))
                .filter(t -> filter.getCategory() == null || filter.getCategory().equals(t.getCategory()))
                .filter(t -> filter.getType() == null || filter.getType().equals(t.getOperationType()))
                .toList();
    }

    /**
     * Возвращает список всех транзакций текущего пользователя.
     *
     * @return список всех транзакций текущего пользователя
     */
    @Override
    public List<String> getAllTransactions() {
        return transactionRepository.findAll().stream().map(Transaction::toStringWithUser).toList();
    }

    /**
     * Возвращает текущий баланс пользователя.
     *
     * @return текущий баланс
     */
    @Override
    public double getCurrentBalance() {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        return transactionRepository.getCurrentBalance(currentUser);
    }

    /**
     * Возвращает отчёт о доходах и расходах за указанный период.
     *
     * @param from начальная дата периода
     * @param to   конечная дата периода
     * @return строковое представление отчёта
     */
    @Override
    public String getIncomeStatement(LocalDate from, LocalDate to) {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        List<Transaction> transactions = getTransactions(TransactionFilter.builder().user(currentUser).from(from).to(to).build());
        return IncomeStatement.builder()
                .user(currentUser)
                .from(from)
                .to(to)
                .income(transactions.stream().filter(t -> t.getOperationType().equals(OperationType.DEPOSIT)).mapToDouble(Transaction::getAmount).sum())
                .outcome(transactions.stream().filter(t -> t.getOperationType().equals(OperationType.WITHDRAW)).mapToDouble(Transaction::getAmount).sum())
                .build()
                .toString();
    }

    /**
     * Возвращает список расходов по категориям за указанный период.
     *
     * @param from начальная дата периода
     * @param to   конечная дата периода
     * @return список строк с информацией о расходах по категориям
     */
    @Override
    public List<String> getCostsByCategory(LocalDate from, LocalDate to) {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        List<Transaction> transactions = getTransactions(
                TransactionFilter.builder().user(currentUser).from(from).to(to).type(OperationType.WITHDRAW).build()
        );

        Map<String, Double> costByCategory = transactions.stream().collect(Collectors.groupingBy(
                t -> Optional.ofNullable(t.getCategory())
                        .map(Category::getName)
                        .orElse("Без категории"),
                Collectors.summingDouble(Transaction::getAmount)));
        double totalCost = transactions.stream().mapToDouble(Transaction::getAmount).sum();
        costByCategory.put("Итого по всем категориям:", totalCost);

        return costByCategory
                .entrySet().stream()
                .map(e -> e.getKey() + " : " + e.getValue())
                .toList();
    }

    /**
     * Проверяет, принадлежит ли транзакция текущему пользователю.
     *
     * @param transaction транзакция для проверки
     * @throws TransactionNotFoundException если транзакция не принадлежит текущему пользователю
     */
    private void checkTransactionBelongsToCurrentUser(Transaction transaction) {
        if (!transaction.getUser().equals(PersonalMoneyTracker.getCurrentUser())) {
            throw new TransactionNotFoundException(transaction.getId());
        }
    }
}
