package belousov.eu.service;

import belousov.eu.model.Category;
import belousov.eu.model.Transaction;
import belousov.eu.model.reportDto.BudgetReport;

import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;

public interface BudgetService {

    void addBudget(YearMonth period, Map<Category, Double> budgetMap);

    Optional<BudgetReport> getBudgetReport(YearMonth period);

    String checkBudget(Transaction lastTransaction);
}
