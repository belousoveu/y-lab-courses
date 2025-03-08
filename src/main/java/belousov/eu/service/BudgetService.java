package belousov.eu.service;

import belousov.eu.model.BudgetReport;
import belousov.eu.model.Category;

import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;

public interface BudgetService {

    void addBudget(YearMonth period, Map<Category, Double> budgetMap);

    Optional<BudgetReport> getBudgetReport(YearMonth period);
}
