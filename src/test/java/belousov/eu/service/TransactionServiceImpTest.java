package belousov.eu.service;

import belousov.eu.event.SavedTransactionalEvent;
import belousov.eu.exception.TransactionNotFoundException;
import belousov.eu.mapper.TransactionMapper;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.TransactionFilter;
import belousov.eu.model.entity.*;
import belousov.eu.repository.imp.TransactionRepositoryImp;
import belousov.eu.service.imp.TransactionServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для {@link TransactionServiceImp}.
 */
class TransactionServiceImpTest {

    @Mock
    private TransactionRepositoryImp transactionRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    ApplicationEventPublisher publisher;

    @InjectMocks
    private TransactionServiceImp transactionServiceImp;

    private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    private User user;
    private Category category;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1, "John Doe", "john@example.com", "password123", Role.USER, true);
        category = new Category(1, "Продукты", user);
        transaction = new Transaction(1, LocalDate.of(2023, 10, 1), OperationType.WITHDRAW, category, 1000.0, "Покупка продуктов", user);
    }

    @Test
    void test_addTransaction_shouldSaveTransactionAndNotifyObservers() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(categoryService.getCategoryByName(category.getName(), user)).thenReturn(category);
        TransactionDto dto = new TransactionDto(0, LocalDate.of(2023, 10, 1), OperationType.WITHDRAW.toString(), category.getName(), 1000.0, "Покупка продуктов", user.getId());
        TransactionDto result = transactionServiceImp.addTransaction(user, dto);
        assertThat(result).isEqualTo(transactionMapper.toDto(transaction));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(publisher, times(1)).publishEvent(new SavedTransactionalEvent(this, transaction));
    }

    @Test
    void test_updateTransaction_whenTransactionExistsAndBelongsToUser_shouldUpdateTransactionAndNotifyObservers() {
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(categoryService.getCategoryByName(category.getName(), user)).thenReturn(category);
        TransactionDto updatedDto = new TransactionDto(1, LocalDate.of(2023, 10, 1), OperationType.WITHDRAW.toString(), category.getName(), 1500.0, "Покупка продуктов и напитков", user.getId());

        TransactionDto updatedTransaction = transactionServiceImp.updateTransaction(1, updatedDto, user);

        assertThat(updatedTransaction.amount()).isEqualTo(1500.0);
        assertThat(updatedTransaction.description()).isEqualTo("Покупка продуктов и напитков");
        verify(transactionRepository, times(1)).save(transaction);
        verify(publisher, times(1)).publishEvent(new SavedTransactionalEvent(this, transaction));
    }

    @Test
    void test_updateTransaction_whenTransactionDoesNotExist_shouldThrowException() {
        when(transactionRepository.findById(1)).thenReturn(Optional.empty());
        TransactionDto updatedDto = new TransactionDto(1, LocalDate.of(2023, 10, 1), OperationType.WITHDRAW.toString(), category.getName(), 1500.0, "Покупка продуктов и напитков", user.getId());

        assertThatThrownBy(() -> transactionServiceImp.updateTransaction(1, updatedDto, user))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Не найдена транзакция с идентификатором 1");
    }

    @Test
    void test_updateTransaction_whenTransactionDoesNotBelongToUser_shouldThrowException() {
        User otherUser = new User(2, "Jane Doe", "jane@example.com", "password456", Role.USER, true);
        Transaction otherTransaction = new Transaction(1, LocalDate.of(2023, 10, 1), OperationType.WITHDRAW, category, 1000.0, "Покупка продуктов", otherUser);
        when(transactionRepository.findById(1)).thenReturn(Optional.of(otherTransaction));
        TransactionDto updatedDto = new TransactionDto(1, LocalDate.of(2023, 10, 1), OperationType.WITHDRAW.toString(), category.getName(), 1500.0, "Покупка продуктов и напитков", user.getId());

        assertThatThrownBy(() -> transactionServiceImp.updateTransaction(1, updatedDto, user))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Не найдена транзакция с идентификатором 1");
    }

    @Test
    void test_deleteTransaction_whenTransactionExistsAndBelongsToUser_shouldDeleteTransactionAndNotifyObservers() {
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        transactionServiceImp.deleteTransaction(1, user);

        verify(transactionRepository, times(1)).delete(transaction);
        verify(publisher, times(1)).publishEvent(new SavedTransactionalEvent(this, transaction));
    }

    @Test
    void test_deleteTransaction_whenTransactionDoesNotExist_shouldThrowException() {
        when(transactionRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionServiceImp.deleteTransaction(1, user))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Не найдена транзакция с идентификатором 1");
    }

    @Test
    void test_deleteTransaction_whenTransactionDoesNotBelongToUser_shouldThrowException() {
        User otherUser = new User(2, "Jane Doe", "jane@example.com", "password456", Role.USER, true);
        Transaction otherTransaction = new Transaction(1, LocalDate.of(2023, 10, 1), OperationType.WITHDRAW, category, 1000.0, "Покупка продуктов", otherUser);
        when(transactionRepository.findById(1)).thenReturn(Optional.of(otherTransaction));

        assertThatThrownBy(() -> transactionServiceImp.deleteTransaction(1, user))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessage("Не найдена транзакция с идентификатором 1");
    }

    @Test
    void test_getTransactions_shouldReturnFilteredTransactions() {
        TransactionFilter filter = TransactionFilter.builder()
                .user(user)
                .from(LocalDate.of(2023, 10, 1))
                .to(LocalDate.of(2023, 10, 31))
                .category(category)
                .type(OperationType.WITHDRAW)
                .build();

        when(transactionRepository.findAll()).thenReturn(List.of(transaction));

        List<TransactionDto> transactions = transactionServiceImp.getTransactions(filter);
        assertThat(transactions).containsExactly(transactionMapper.toDto(transaction));
    }

    @Test
    void test_getAllTransactions_shouldReturnAllTransactions() {
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));
        TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

        List<TransactionDto> result = transactionServiceImp.getAllTransactions();
        assertThat(result).containsExactly(mapper.toDto(transaction));
    }

    @Test
    void test_getCurrentBalance_shouldReturnCurrentBalance() {
        when(transactionRepository.getCurrentBalance(user)).thenReturn(5000.0);

        double balance = transactionServiceImp.getCurrentBalance(user).amount();
        assertThat(balance).isEqualTo(5000.0);
    }

    @Test
    void test_getIncomeStatement_shouldReturnIncomeStatement() {
        Transaction deposit = new Transaction(1, LocalDate.of(2023, 10, 1), OperationType.DEPOSIT, null, 10000.0, "Зарплата", user);
        Transaction withdraw = new Transaction(2, LocalDate.of(2023, 10, 2), OperationType.WITHDRAW, category, 5000.0, "Покупка продуктов", user);

        when(transactionRepository.findAll()).thenReturn(List.of(deposit, withdraw));

        String result = transactionServiceImp.getIncomeStatement(user, LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 31)).toString();
        assertThat(result).contains("Доход: 10 000,00").contains("Расход: 5 000,00");
    }

    @Test
    void test_getCostsByCategory_shouldReturnCostsByCategory() {
        Transaction transaction1 = new Transaction(1, LocalDate.of(2023, 10, 1), OperationType.WITHDRAW, category, 3000.0, "Покупка продуктов", user);
        Transaction transaction2 = new Transaction(2, LocalDate.of(2023, 10, 2), OperationType.WITHDRAW, null, 2000.0, "Транспорт", user);

        when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2));

        List<String> result = transactionServiceImp.getCostsByCategory(user, LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 31));
        assertThat(result).contains("Продукты : 3000.0").contains("Без категории : 2000.0").contains("Итого по всем категориям: : 5000.0");
    }
}