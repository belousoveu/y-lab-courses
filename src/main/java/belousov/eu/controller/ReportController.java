package belousov.eu.controller;

import belousov.eu.model.dto.BalanceDto;
import belousov.eu.model.dto.IncomeStatement;
import belousov.eu.model.entity.User;
import belousov.eu.service.ReportService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private static final String CURRENT_USER = "currentUser";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ReportService reportService;

    @GetMapping("/balance")
    public BalanceDto getCurrentBalance(HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return reportService.getCurrentBalance(user);
    }

    @GetMapping("/statement")
    public IncomeStatement getStatement(@RequestParam String from, @RequestParam String to, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return reportService.getIncomeStatement(user, LocalDate.parse(from, DATE_FORMATTER), LocalDate.parse(to, DATE_FORMATTER));
    }

    @GetMapping("/categories")
    public List<String> getCategories(@RequestParam String from, @RequestParam String to, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return reportService.getCostsByCategory(user, LocalDate.parse(from, DATE_FORMATTER), LocalDate.parse(to, DATE_FORMATTER));
    }
}
