package belousov.eu.controller;

import belousov.eu.model.Transaction;
import belousov.eu.model.User;
import belousov.eu.model.dto.BudgetDto;
import belousov.eu.model.dto.BudgetReport;
import belousov.eu.observer.BalanceChangeObserver;
import belousov.eu.service.BudgetService;
import belousov.eu.utils.MessageColor;
import belousov.eu.view.ConsoleView;
import lombok.AllArgsConstructor;

import java.time.YearMonth;

@AllArgsConstructor
public class BudgetController implements BalanceChangeObserver {

    private final BudgetService budgetService;
    private final ConsoleView consoleView;

    public void addBudget(User user, BudgetDto budgetDto) {
        budgetService.addBudget(user, budgetDto);
    }


    @Override
    public void balanceChanged(Transaction lastTransaction) {
        String resultMessage = budgetService.checkBudget(lastTransaction);
        if (!resultMessage.isEmpty()) {
            consoleView.println(resultMessage, MessageColor.PURPLE);
        }
    }

    public BudgetReport getBudgetByPeriod(User user, String period) {
        return budgetService.getBudgetReport(user, YearMonth.parse(period));
    }
}
