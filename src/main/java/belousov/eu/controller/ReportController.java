package belousov.eu.controller;

import belousov.eu.service.ReportService;
import belousov.eu.utils.InputPattern;
import belousov.eu.utils.MessageColor;
import belousov.eu.view.ConsoleView;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class ReportController {

    ReportService reportService;
    ConsoleView consoleView;

    private static final String PROMPT_DATE_FROM = "Введите дату начала периода (YYYY-MM-DD): ";
    private static final String PROMPT_DATE_TO = "Введите дату конца периода (YYYY-MM-DD): ";


    public void viewCurrentBalance() {
        consoleView.println("Текущий баланс: %,.2f".formatted(reportService.getCurrentBalance()), MessageColor.CYAN);
    }

    public void viewIncomeStatement() {
        LocalDate from = consoleView.readPeriod(PROMPT_DATE_FROM, InputPattern.DATE, LocalDate::parse);
        LocalDate to = consoleView.readPeriod(PROMPT_DATE_TO, InputPattern.DATE, LocalDate::parse);
        consoleView.println(reportService.getIncomeStatement(from, to), MessageColor.YELLOW);
    }

    public void viewCostsByCategory() {
        LocalDate from = consoleView.readPeriod(PROMPT_DATE_FROM, InputPattern.DATE, LocalDate::parse);
        LocalDate to = consoleView.readPeriod(PROMPT_DATE_TO, InputPattern.DATE, LocalDate::parse);
        consoleView.println("Отчет расходов по категориям за период: %s - %s".formatted(from, to),
                reportService.getCostsByCategory(from, to),
                MessageColor.CYAN, MessageColor.YELLOW);
    }

    public void viewBalanceSheet() {
        LocalDate from = consoleView.readPeriod(PROMPT_DATE_FROM, InputPattern.DATE, LocalDate::parse);
        LocalDate to = consoleView.readPeriod(PROMPT_DATE_TO, InputPattern.DATE, LocalDate::parse);
        consoleView.println("Отчет о финансовом состоянии за период: %s - %s".formatted(from, to));
        consoleView.println("В задании отсутствуют правила создания отчета о финансовом состоянии", MessageColor.RED);

    }
}
