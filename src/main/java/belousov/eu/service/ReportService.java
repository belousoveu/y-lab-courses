package belousov.eu.service;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    double getCurrentBalance();

    String getIncomeStatement(LocalDate from, LocalDate to);

    List<String> getCostsByCategory(LocalDate from, LocalDate to);
}
