package belousov.eu.service;

import belousov.eu.model.Transaction;
import belousov.eu.model.User;
import belousov.eu.model.dto.BudgetDto;
import belousov.eu.model.dto.BudgetReport;

import java.time.YearMonth;

public interface BudgetService {

    void addBudget(User user, BudgetDto budgetDto);

    BudgetReport getBudgetReport(User user, YearMonth period);

    String checkBudget(Transaction lastTransaction);
}
