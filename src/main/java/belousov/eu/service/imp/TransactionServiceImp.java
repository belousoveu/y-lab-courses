package belousov.eu.service.imp;

import belousov.eu.event.BalanceChangedEvent;
import belousov.eu.exception.TransactionNotFoundException;
import belousov.eu.mapper.TransactionMapper;
import belousov.eu.model.dto.BalanceDto;
import belousov.eu.model.dto.IncomeStatement;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.TransactionFilter;
import belousov.eu.model.entity.OperationType;
import belousov.eu.model.entity.Transaction;
import belousov.eu.model.entity.User;
import belousov.eu.repository.imp.TransactionRepositoryImp;
import belousov.eu.service.AdminAccessTransactionService;
import belousov.eu.service.CategoryService;
import belousov.eu.service.ReportService;
import belousov.eu.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для управления транзакциями.
 * Обеспечивает добавление, обновление, удаление и поиск транзакций, а также формирование отчётов.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImp implements TransactionService, AdminAccessTransactionService, ReportService {
    /**
     * Репозиторий для хранения транзакций.
     */
    private final TransactionRepositoryImp transactionRepository;

    /**
     * Сервис для работы с категориями.
     */
    private final CategoryService categoryService;
    /**
     * Наблюдатель за изменением баланса
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Маппер для преобразования объектов транзакций в DTO и обратно.
     */
    private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    /**
     * Добавляет новую транзакцию.
     *
     * @param user текущий авторизованный пользователь
     * @param dto  объект с данными транзакции
     * @return добавленная транзакция
     */
    @Override
    public TransactionDto addTransaction(User user, TransactionDto dto) {
        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setUser(user);
        transaction.setCategory(categoryService.getCategoryByName(dto.category(), user));

        Transaction savedTransaction = transactionRepository
                .save(transaction);
        eventPublisher.publishEvent(new BalanceChangedEvent(this, savedTransaction));
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
     * @param id             ID транзакции
     * @param transactionDto новые данные транзакции
     * @param user           текущий авторизованный пользователь
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
        eventPublisher.publishEvent(new BalanceChangedEvent(this, updatedTransaction));
        return transactionMapper.toDto(updatedTransaction);
    }

    /**
     * Удаляет транзакцию по ID.
     *
     * @param id   ID транзакции
     * @param user текущий авторизованный пользователь
     * @throws TransactionNotFoundException если транзакция не найдена или не принадлежит текущему пользователю
     */
    @Override
    public void deleteTransaction(int id, User user) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        checkTransactionBelongsToCurrentUser(transaction, user);
        transactionRepository.delete(transaction);
        eventPublisher.publishEvent(new BalanceChangedEvent(this, transaction));
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
    public BalanceDto getCurrentBalance(User user) {
        return new BalanceDto(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                user.getName(),
                transactionRepository.getCurrentBalance(user));
    }

    /**
     * Возвращает отчёт о доходах и расходах за указанный период.
     *
     * @param currentUser текущий авторизованный пользователь
     * @param from        начальная дата периода
     * @param to          конечная дата периода
     * @return строковое представление отчёта
     */
    @Override
    public IncomeStatement getIncomeStatement(User currentUser, LocalDate from, LocalDate to) {
        List<TransactionDto> transactions = getTransactions(TransactionFilter.builder().user(currentUser).from(from).to(to).build());
        return IncomeStatement.builder()
                .user(currentUser)
                .from(from)
                .to(to)
                .income(transactions.stream().filter(t -> t.operationType().equals(OperationType.DEPOSIT.name())).mapToDouble(TransactionDto::amount).sum())
                .outcome(transactions.stream().filter(t -> t.operationType().equals(OperationType.WITHDRAW.name())).mapToDouble(TransactionDto::amount).sum())
                .build();
    }

    /**
     * Возвращает список расходов по категориям за указанный период.
     *
     * @param user текущий авторизованный пользователь
     * @param from начальная дата периода
     * @param to   конечная дата периода
     * @return список строк с информацией о расходах по категориям
     */
    @Override
    public List<String> getCostsByCategory(User user, LocalDate from, LocalDate to) {
        List<TransactionDto> transactions = getTransactions(
                TransactionFilter.builder().user(user).from(from).to(to).type(OperationType.WITHDRAW).build()
        );

        Map<String, Double> costByCategory = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> Optional.ofNullable(t.category()).orElse("Без категории"),
                        Collectors.summingDouble(TransactionDto::amount)
                ));
        double totalCost = transactions.stream().mapToDouble(TransactionDto::amount).sum();

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
     * @param user        текущий авторизованный пользователь
     * @throws TransactionNotFoundException если транзакция не принадлежит текущему пользователю
     */
    private void checkTransactionBelongsToCurrentUser(Transaction transaction, User user) {
        if (!transaction.getUser().equals(user)) {
            throw new TransactionNotFoundException(transaction.getId());
        }
    }
}
