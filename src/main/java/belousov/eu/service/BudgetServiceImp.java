package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.model.*;
import belousov.eu.model.report_dto.BudgetReport;
import belousov.eu.repository.BudgetRepository;
import lombok.AllArgsConstructor;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Реализация сервиса для управления бюджетами.
 * Обеспечивает добавление бюджетов, формирование отчётов и проверку превышения бюджета.
 */
@AllArgsConstructor
public class BudgetServiceImp implements BudgetService {

    /**
     * Сервис для управления транзакциями.
     */
    private final TransactionService transactionService;
    /**
     * Сервис для отправки уведомлений по электронной почте.
     */
    private final EmailService emailService;
    /**
     * Репозиторий для управления бюджетами.
     */
    private final BudgetRepository budgetRepository;

    /**
     * Добавляет бюджеты для указанного периода и категории.
     *
     * @param period    период
     * @param budgetMap карта категорий и их бюджетов
     */
    @Override
    public void addBudget(YearMonth period, Map<Category, Double> budgetMap) {
        User user = PersonalMoneyTracker.getCurrentUser();
        budgetMap.forEach((category, amount) ->
                budgetRepository.save(new Budget(0, period.atDay(1), category, user, amount.intValue())));

    }

    /**
     * Подготавливает отчёт о бюджете для указанного периода.
     *
     * @param period период
     * @return отчёт о бюджете или Optional.empty(), если не найден
     */
    @Override
    public Optional<BudgetReport> getBudgetReport(YearMonth period) {
        List<Budget> budgets = budgetRepository.findAllByPeriod(PersonalMoneyTracker.getCurrentUser(), period);
        if (budgets.isEmpty()) {
            return Optional.empty();
        }

        TransactionFilter filter = TransactionFilter.builder()
                .user(PersonalMoneyTracker.getCurrentUser())
                .from(period.atDay(1))
                .to(period.atEndOfMonth())
                .build();

        List<Transaction> transactions = transactionService.getTransactions(filter);

        BudgetReport budgetReport = new BudgetReport();
        budgetReport.setPeriod(period);
        budgetReport.setUser(PersonalMoneyTracker.getCurrentUser());
        for (Budget budget : budgets) {
            double spent = transactions.stream()
                    .filter(t -> t.getCategory() != null)
                    .filter(t -> t.getCategory().equals(budget.getCategory())).mapToDouble(Transaction::getAmount).sum();
            budgetReport.addReportRow(budget.getCategory(), budget.getAmount(), spent);
        }

        return Optional.of(budgetReport);
    }

    /**
     * Проверяет, превышен ли бюджет для последней транзакции.
     *
     * @param lastTransaction последняя транзакция
     * @return сообщение о превышении бюджета или пустая строка, если бюджет не превышен
     */
    @Override
    public String checkBudget(Transaction lastTransaction) {
        if (lastTransaction.getOperationType() == OperationType.DEPOSIT || lastTransaction.getCategory() == null) {
            return "";
        }
        YearMonth period = YearMonth.of(lastTransaction.getDate().getYear(), lastTransaction.getDate().getMonth());
        Optional<Budget> budget = budgetRepository
                .findByCategoryAndPeriod(lastTransaction.getCategory(), lastTransaction.getUser(), period);

        if (budget.isPresent()) {
            List<Transaction> transactions = transactionService.getTransactions(TransactionFilter.builder()
                    .user(lastTransaction.getUser())
                    .category(lastTransaction.getCategory())
                    .from(period.atDay(1))
                    .to(period.atEndOfMonth())
                    .type(OperationType.WITHDRAW)
                    .build());
            double spent = transactions.stream().mapToDouble(Transaction::getAmount).sum();
            if (spent >= budget.get().getAmount()) {
                emailService.sendEmail(lastTransaction.getUser().getEmail(), "Бюджет превышен", "Превышен бюджет по категории %s на сумму %.2f");
                return "Превышен бюджет по категории %s на сумму %.2f"
                        .formatted(lastTransaction.getCategory().getName(), spent - budget.get().getAmount());
            }
        }
        return "";
    }

}
