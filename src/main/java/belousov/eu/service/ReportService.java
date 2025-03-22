package belousov.eu.service;

import belousov.eu.model.User;
import belousov.eu.model.dto.BalanceDto;
import belousov.eu.model.report_dto.IncomeStatement;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    BalanceDto getCurrentBalance(User user);

    IncomeStatement getIncomeStatement(User user, LocalDate from, LocalDate to);

    List<String> getCostsByCategory(User user, LocalDate from, LocalDate to);
}
