package belousov.eu.service;

import belousov.eu.model.dto.BudgetDto;
import belousov.eu.model.dto.BudgetReport;
import belousov.eu.model.entity.Transaction;
import belousov.eu.model.entity.User;

import java.time.YearMonth;

public interface BudgetService {

    void addBudget(User user, BudgetDto budgetDto);

    BudgetReport getBudgetReport(User user, YearMonth period);

    String checkBudget(Transaction lastTransaction);
}
