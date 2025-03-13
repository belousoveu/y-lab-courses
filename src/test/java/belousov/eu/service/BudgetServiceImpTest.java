package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.model.*;
import belousov.eu.model.report_dto.BudgetReport;
import belousov.eu.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для {@link BudgetServiceImp}.
 */
class BudgetServiceImpTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private EmailService emailService;

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetServiceImp budgetServiceImp;

    private User user;
    private Category category1;
    private Category category2;
    private YearMonth period;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1, "John Doe", "john@example.com", "password123", Role.USER, true);
        category1 = new Category(1, "Продукты", user);
        category2 = new Category(2, "Транспорт", user);
        period = YearMonth.of(2023, 10);
        PersonalMoneyTracker.setCurrentUser(user); // Устанавливаем текущего пользователя
    }

    @Test
    void test_addBudget_shouldSaveBudgets() {
        Map<Category, Double> budgetMap = Map.of(
                category1, 10000.0,
                category2, 5000.0
        );

        budgetServiceImp.addBudget(period, budgetMap);

        verify(budgetRepository, times(2)).save(any(Budget.class));
    }

    @Test
    void test_getBudgetReport_whenBudgetsExist_shouldReturnReport() {
        Budget budget1 = new Budget(1, period, category1, user, 10000.0);
        Budget budget2 = new Budget(2, period, category2, user, 5000.0);
        when(budgetRepository.findAllByPeriod(user, period)).thenReturn(List.of(budget1, budget2));

        Transaction transaction1 = new Transaction(1, period.atDay(1), OperationType.WITHDRAW, category1, 3000.0, "Покупка продуктов", user);
        Transaction transaction2 = new Transaction(2, period.atDay(2), OperationType.WITHDRAW, category2, 2000.0, "Транспорт", user);
        when(transactionService.getTransactions(any(TransactionFilter.class))).thenReturn(List.of(transaction1, transaction2));

        Optional<BudgetReport> report = budgetServiceImp.getBudgetReport(period);
        assertThat(report).isPresent();

        BudgetReport budgetReport = report.get();
        assertThat(budgetReport.getPeriod()).isEqualTo(period);
        assertThat(budgetReport.getUser()).isEqualTo(user);
        assertThat(budgetReport.getReportRows()).hasSize(2);
    }

    @Test
    void test_getBudgetReport_whenNoBudgetsExist_shouldReturnEmpty() {
        when(budgetRepository.findAllByPeriod(user, period)).thenReturn(List.of());

        Optional<BudgetReport> report = budgetServiceImp.getBudgetReport(period);
        assertThat(report).isEmpty();
    }

    @Test
    void test_checkBudget_whenBudgetExceeded_shouldSendEmailAndReturnMessage() {
        Transaction transaction = new Transaction(1, period.atDay(1), OperationType.WITHDRAW, category1, 11000.0, "Покупка продуктов", user);
        Budget budget = new Budget(1, period, category1, user, 10000.0);
        when(budgetRepository.findByCategoryAndPeriod(category1, user, period)).thenReturn(Optional.of(budget));

        List<Transaction> transactions = List.of(transaction);
        when(transactionService.getTransactions(any(TransactionFilter.class))).thenReturn(transactions);

        String result = budgetServiceImp.checkBudget(transaction);
        assertThat(result).isEqualTo("Превышен бюджет по категории Продукты на сумму 1000,00");

        verify(emailService, times(1)).sendEmail(user.getEmail(), "Бюджет превышен", "Превышен бюджет по категории %s на сумму %.2f");
    }

    @Test
    void test_checkBudget_whenBudgetNotExceeded_shouldReturnEmptyMessage() {
        Transaction transaction = new Transaction(1, period.atDay(1), OperationType.WITHDRAW, category1, 9000.0, "Покупка продуктов", user);
        Budget budget = new Budget(1, period, category1, user, 10000.0);
        when(budgetRepository.findByCategoryAndPeriod(category1, user, period)).thenReturn(Optional.of(budget));

        List<Transaction> transactions = List.of(transaction);
        when(transactionService.getTransactions(any(TransactionFilter.class))).thenReturn(transactions);

        String result = budgetServiceImp.checkBudget(transaction);
        assertThat(result).isEmpty();

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void test_checkBudget_whenDepositTransaction_shouldReturnEmptyMessage() {
        Transaction transaction = new Transaction(1, period.atDay(1), OperationType.DEPOSIT, category1, 1000.0, "Пополнение счёта", user);

        String result = budgetServiceImp.checkBudget(transaction);
        assertThat(result).isEmpty();

        verify(budgetRepository, never()).findByCategoryAndPeriod(any(), any(), any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void test_checkBudget_whenCategoryIsNull_shouldReturnEmptyMessage() {
        Transaction transaction = new Transaction(1, period.atDay(1), OperationType.WITHDRAW, null, 1000.0, "Без категории", user);

        String result = budgetServiceImp.checkBudget(transaction);
        assertThat(result).isEmpty();

        verify(budgetRepository, never()).findByCategoryAndPeriod(any(), any(), any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}