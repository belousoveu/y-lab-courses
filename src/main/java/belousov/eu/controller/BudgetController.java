package belousov.eu.controller;

import belousov.eu.model.dto.BudgetDto;
import belousov.eu.model.dto.BudgetReport;
import belousov.eu.model.entity.User;
import belousov.eu.service.BudgetService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@AllArgsConstructor
@RequestMapping("/api/budgets")
public class BudgetController {

    private static final String CURRENT_USER = "currentUser";

    private final BudgetService budgetService;

    @PostMapping("/")
    public void addBudget(@RequestBody BudgetDto budgetDto, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        budgetService.addBudget(user, budgetDto);
    }

    @GetMapping("/{period}")
    public BudgetReport getBudgetByPeriod(@PathVariable String period, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return budgetService.getBudgetReport(user, YearMonth.parse(period));
    }
}
