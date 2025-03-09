package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.model.*;
import belousov.eu.model.reportDto.BudgetReport;
import belousov.eu.repository.BudgetRepository;
import lombok.AllArgsConstructor;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class BudgetServiceImp implements BudgetService {

    private final TransactionService transactionService;
    private final EmailService emailService;
    private final BudgetRepository budgetRepository;

    @Override
    public void addBudget(YearMonth period, Map<Category, Double> budgetMap) {
        User user = PersonalMoneyTracker.getCurrentUser();
        budgetMap.forEach((category, amount) ->
                budgetRepository.save(new Budget(0, period, category, user, amount.intValue())));

    }

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
