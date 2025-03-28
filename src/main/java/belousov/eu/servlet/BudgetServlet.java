package belousov.eu.servlet;

import belousov.eu.annotation.AuthorizationRequired;
import belousov.eu.controller.BudgetController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.model.User;
import belousov.eu.model.dto.BudgetDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jakarta.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY;

@AuthorizationRequired
@RequiredArgsConstructor
public class BudgetServlet extends HttpServlet {

    private static final String CURRENT_USER = "currentUser";
    private static final String CONTENT_TYPE = "application/json";
    private static final String PATH_BUDGET = "/budgets";
    private static final Pattern PATTERN_BUDGET = Pattern.compile("^/budgets/(\\d{4}-\\d{2})$");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final transient BudgetController budgetController;
    private final ObjectMapper objectMapper;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Matcher matcher = PATTERN_BUDGET.matcher(path);
        if (PATH_BUDGET.equals(path)) {
            String currentPeriod = LocalDate.now().format(DATE_FORMAT);
            resp.sendRedirect("/api" + PATH_BUDGET + "/" + currentPeriod);
            return;
        } else if (matcher.matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            String period = matcher.group(1);
            resp.setContentType(CONTENT_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(budgetController.getBudgetByPeriod(user, period)));
            return;
        }
        throw new PathNotFoundException(path);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (PATH_BUDGET.equals(path)) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            BudgetDto dto = objectMapper.readValue(req.getInputStream(), BudgetDto.class);
            budgetController.addBudget(user, dto);
            String period = dto.getPeriod().format(DATE_FORMAT);
            resp.sendRedirect("/api" + PATH_BUDGET + "/" + period);
            resp.setStatus(SC_MOVED_TEMPORARILY);
            return;
        }
        throw new PathNotFoundException(path);
    }

}
