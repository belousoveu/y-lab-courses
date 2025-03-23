package belousov.eu.service;

import belousov.eu.annotation.Benchmark;
import belousov.eu.model.User;
import belousov.eu.model.dto.BalanceDto;
import belousov.eu.model.dto.IncomeStatement;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    @Benchmark
    BalanceDto getCurrentBalance(User user);

    @Benchmark
    IncomeStatement getIncomeStatement(User user, LocalDate from, LocalDate to);

    @Benchmark
    List<String> getCostsByCategory(User user, LocalDate from, LocalDate to);
}
