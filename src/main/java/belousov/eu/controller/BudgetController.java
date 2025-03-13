package belousov.eu.controller;

import belousov.eu.exception.BudgetNotFoundException;
import belousov.eu.model.Category;
import belousov.eu.model.Transaction;
import belousov.eu.model.report_dto.BudgetReport;
import belousov.eu.observer.BalanceChangeObserver;
import belousov.eu.service.BudgetService;
import belousov.eu.service.CategoryService;
import belousov.eu.utils.InputPattern;
import belousov.eu.utils.MessageColor;
import belousov.eu.view.ConsoleView;
import lombok.AllArgsConstructor;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class BudgetController implements BalanceChangeObserver {

    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final ConsoleView consoleView;

    public void addBudget() {
        List<Category> categories = categoryService.getAllCategories();
        Map<Category, Double> budgetMap = new HashMap<>();
        YearMonth period = consoleView.readPeriod("Введите период (yyyy-MM): ", InputPattern.YEAR_MONTH, YearMonth::parse);
        for (Category category : categories) {
            budgetMap.put(category, consoleView.readDouble("Введите бюджет по категории " + category.getName() + ": ", InputPattern.SUM));
        }
        budgetService.addBudget(period, budgetMap);
        consoleView.println(String.format("Общий бюджет на период %s составляет %,.2f",
                period, budgetMap.values().stream().mapToDouble(Double::doubleValue).sum()), MessageColor.CYAN);
    }

    public void viewBudgetDetails() {
        YearMonth period = consoleView.readPeriod("Введите период (yyyy-MM): ", InputPattern.YEAR_MONTH, YearMonth::parse);
        BudgetReport report = budgetService.getBudgetReport(period).orElseThrow(() -> new BudgetNotFoundException(period));
        consoleView.println("Пользователь: %s".formatted(report.getUser()), MessageColor.CYAN);
        consoleView.println("Исполнение бюджета на период %s:".formatted(period), report.getReportRows(), MessageColor.YELLOW, MessageColor.WHITE);
        consoleView.println(report.getTotalRow(), MessageColor.CYAN);
    }

    @Override
    public void balanceChanged(Transaction lastTransaction) {
        String resultMessage = budgetService.checkBudget(lastTransaction);
        if (!resultMessage.isEmpty()) {
            consoleView.println(resultMessage, MessageColor.PURPLE);
        }
    }
}
