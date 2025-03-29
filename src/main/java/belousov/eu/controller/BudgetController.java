package belousov.eu.controller;

import belousov.eu.model.dto.BudgetDto;
import belousov.eu.model.dto.BudgetReport;
import belousov.eu.model.entity.User;
import belousov.eu.service.BudgetService;
import lombok.AllArgsConstructor;

import java.time.YearMonth;

@AllArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    public void addBudget(User user, BudgetDto budgetDto) {
        budgetService.addBudget(user, budgetDto);
    }

    public BudgetReport getBudgetByPeriod(User user, String period) {
        return budgetService.getBudgetReport(user, YearMonth.parse(period));
    }
}
