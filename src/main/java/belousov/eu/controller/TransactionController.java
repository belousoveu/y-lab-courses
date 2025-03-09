package belousov.eu.controller;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.model.*;
import belousov.eu.service.CategoryService;
import belousov.eu.service.TransactionService;
import belousov.eu.utils.InputPattern;
import belousov.eu.utils.MessageColor;
import belousov.eu.view.ConsoleView;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final ConsoleView consoleView;

    public void createTransaction() {
        List<Category> categories = categoryService.getAllCategories();
        LocalDate date = consoleView.readPeriod("Введите дату (yyyy-MM-dd):", InputPattern.DATE, LocalDate::parse);
        OperationType type = consoleView.readFromList("Введите тип операции", List.of(OperationType.values()));
        Category category = consoleView.readFromList("Введите категорию", categories);
        double amount = consoleView.readDouble("Введите сумму:", InputPattern.SUM);
        String description = consoleView.readString("Введите описание: ");
        Transaction transaction = transactionService.addTransaction(date, type, category, amount, description);
        consoleView.println("Транзакция %s успешно добавлена".formatted(transaction), MessageColor.CYAN);
    }


    public void updateTransaction() {
        List<Category> categories = categoryService.getAllCategories();
        int id = consoleView.readInt("Введите ID транзакции", InputPattern.POSITIVE_INTEGER);
        Category category = consoleView.readFromList("Введите категорию", categories);
        double amount = consoleView.readDouble("Введите сумму:", InputPattern.SUM);
        String description = consoleView.readString("Введите описание: ");
        Transaction transaction = transactionService.updateTransaction(id, category, amount, description);
        consoleView.println("Транзакция %s успешно обновлена".formatted(transaction), MessageColor.CYAN);
    }

    public void deleteTransaction() {
        int id = consoleView.readInt("Введите ID транзакции", InputPattern.POSITIVE_INTEGER);
        transactionService.deleteTransaction(id);
        consoleView.println("Транзакция успешно удалена", MessageColor.CYAN);
    }

    public void getTransactions() {
        List<Category> categories = categoryService.getAllCategories();
        LocalDate dateFrom = consoleView
                .readOptionalPeriod("Начальная дата (yyyy-MM-dd):", InputPattern.DATE, LocalDate::parse)
                .orElse(null);
        LocalDate dateTo = consoleView
                .readOptionalPeriod("Конечная дата (yyyy-MM-dd):", InputPattern.DATE, LocalDate::parse)
                .orElse(null);
        Category category = consoleView.readOptionalFromList("Введите категорию", categories)
                .orElse(null);
        OperationType type = consoleView
                .readOptionalFromList("Введите тип операции", List.of(OperationType.values()))
                .orElse(null);
        User user = PersonalMoneyTracker.getCurrentUser();
        List<Transaction> transactions = transactionService.getTransactions(
                new TransactionFilter(user, dateFrom, dateTo, category, type));
        consoleView.println("Список транзакций по установленным фильтрам:", transactions, MessageColor.WHITE, MessageColor.YELLOW);

    }
}
