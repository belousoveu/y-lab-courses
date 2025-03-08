package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.model.Budget;
import belousov.eu.model.BudgetReport;
import belousov.eu.model.Category;
import belousov.eu.model.User;
import belousov.eu.repository.BudgetRepository;
import lombok.AllArgsConstructor;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class BudgetServiceImp implements BudgetService {

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
        return Optional.empty(); //TODO Собрать отчет по расходам за период
    }
}
