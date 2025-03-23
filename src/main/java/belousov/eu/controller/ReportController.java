package belousov.eu.controller;

import belousov.eu.model.User;
import belousov.eu.model.dto.BalanceDto;
import belousov.eu.model.dto.IncomeStatement;
import belousov.eu.service.ReportService;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;

    public BalanceDto getCurrentBalance(User user) {
        return reportService.getCurrentBalance(user);
    }

    public IncomeStatement getStatement(User user, LocalDate from, LocalDate to) {
        return reportService.getIncomeStatement(user, from, to);
    }

    public List<String> getCategories(User user, LocalDate from, LocalDate to) {
        return reportService.getCostsByCategory(user, from, to);
    }
}
