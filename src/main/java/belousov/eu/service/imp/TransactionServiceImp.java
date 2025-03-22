package belousov.eu.service.imp;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.TransactionNotFoundException;
import belousov.eu.mapper.TransactionMapper;
import belousov.eu.model.OperationType;
import belousov.eu.model.Transaction;
import belousov.eu.model.TransactionFilter;
import belousov.eu.model.User;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.report_dto.IncomeStatement;
import belousov.eu.observer.BalanceChangeSubject;
import belousov.eu.repository.TransactionRepository;
import belousov.eu.service.AdminAccessTransactionService;
import belousov.eu.service.CategoryService;
import belousov.eu.service.ReportService;
import belousov.eu.service.TransactionService;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final CategoryService categoryService;
    /**
     * Наблюдатель за изменением баланса
     */
    private final BalanceChangeSubject balanceChangeSubject;

    /**
     * Маппер для преобразования объектов транзакций в DTO и обратно.
     */
    private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    /**
     * Добавляет новую транзакцию.
     *
     * @param user        текущий авторизованный пользователь
     * @param dto         объект с данными транзакции
     *
     * @return добавленная транзакция
     */
    @Override
    public TransactionDto addTransaction(User user, TransactionDto dto) {
        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setUser(user);
        transaction.setCategory(categoryService.getCategoryByName(dto.category(), user));

        Transaction savedTransaction = transactionRepository
                .save(transaction);
        balanceChangeSubject.notifyObservers(transaction);
        return transactionMapper.toDto(savedTransaction);
    }

    /**
     * Возвращает транзакцию по её ID.
     *
     * @param id ID транзакции
     * @return объект dto, содержащий информацию о транзакции
     * @throws TransactionNotFoundException если транзакция не найдена или не принадлежит текущему пользователю
     */
    @Override
    public TransactionDto getTransactionById(int id, User user) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        checkTransactionBelongsToCurrentUser(transaction, user);
        return transactionMapper.toDto(transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id)));
    }

    /**
     * Обновляет существующую транзакцию.
     *
     * @param id          ID транзакции
     * @param transactionDto новые данные транзакции
     * @param user        текущий авторизованный пользователь
     *
     * @return обновлённая транзакция
     * @throws TransactionNotFoundException если транзакция не найдена или не принадлежит текущему пользователю
     */
    @Override
    public TransactionDto updateTransaction(int id, TransactionDto transactionDto, User user) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        checkTransactionBelongsToCurrentUser(transaction, user);
        transaction.setCategory(categoryService.getCategoryByName(transactionDto.category(), user));
        transaction.setAmount(transactionDto.amount());
        transaction.setDescription(transactionDto.description());
        Transaction updatedTransaction = transactionRepository.save(transaction);
        balanceChangeSubject.notifyObservers(updatedTransaction);
        return transactionMapper.toDto(updatedTransaction);
    }

    /**
     * Удаляет транзакцию по ID.
     *
     * @param id            ID транзакции
     * @param user          текущий авторизованный пользователь
     *
     * @throws TransactionNotFoundException если транзакция не найдена или не принадлежит текущему пользователю
     */
    @Override
    public void deleteTransaction(int id, User user) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        checkTransactionBelongsToCurrentUser(transaction, user);
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
    public List<TransactionDto> getTransactions(TransactionFilter filter) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getUser().equals(filter.getUser()))
                .filter(t -> filter.getFrom() == null || !filter.getFrom().isAfter(t.getDate()))
                .filter(t -> filter.getTo() == null || !filter.getTo().isBefore(t.getDate()))
                .filter(t -> filter.getCategory() == null || filter.getCategory().equals(t.getCategory()))
                .filter(t -> filter.getType() == null || filter.getType().equals(t.getOperationType()))
                .toList()
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    /**
     * Возвращает список всех транзакций.
     *
     * @return список всех транзакций.
     */
    @Override
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream().map(transactionMapper::toDto).toList();
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
        List<TransactionDto> transactions = getTransactions(TransactionFilter.builder().user(currentUser).from(from).to(to).build());
        return IncomeStatement.builder()
                .user(currentUser)
                .from(from)
                .to(to)
                .income(transactions.stream().filter(t -> t.operationType().equals(OperationType.DEPOSIT.name())).mapToDouble(TransactionDto::amount).sum())
                .outcome(transactions.stream().filter(t -> t.operationType().equals(OperationType.WITHDRAW.name())).mapToDouble(TransactionDto::amount).sum())
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
        List<TransactionDto> transactions = getTransactions(
                TransactionFilter.builder().user(currentUser).from(from).to(to).type(OperationType.WITHDRAW).build()
        );

//        Map<String, Double> costByCategory = transactions.stream().collect(Collectors.groupingBy(
//                t -> Optional.ofNullable(t.getCategory())
//                        .map(Category::getName)
//                        .orElse("Без категории"),
//                Collectors.summingDouble(Transaction::getAmount)));

        //TODO
        double totalCost = transactions.stream().mapToDouble(TransactionDto::amount).sum();

        Map<String, Double> costByCategory = new HashMap<>();
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
    private void checkTransactionBelongsToCurrentUser(Transaction transaction, User user) {
        if (!transaction.getUser().equals(user)) {
            throw new TransactionNotFoundException(transaction.getId());
        }
    }
}
