package belousov.eu.servlet;

import belousov.eu.annotation.AuthorizationRequired;
import belousov.eu.controller.ReportController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.exception.RequiredArgumentException;
import belousov.eu.model.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

@AuthorizationRequired
@RequiredArgsConstructor
public class ReportServlet extends HttpServlet {

    private static final String CURRENT_USER = "currentUser";
    private static final String CONTENT_TYPE = "application/json";
    private static final String PARAMETER_FROM = "from";
    private static final String PARAMETER_TO = "to";
    private static final Pattern PATTERN_BALANCE = Pattern.compile("^/reports/(\\d+)/balance$");
    private static final Pattern PATTERN_STATEMENT = Pattern.compile("^/reports/(\\d+)/statement\\?*$");
    private static final Pattern PATTERN_CATEGORIES = Pattern.compile("^/reports/(\\d+)/categories\\?*$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final transient ReportController reportController;
    private final ObjectMapper objectMapper;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (PATTERN_BALANCE.matcher(path).matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            resp.setContentType(CONTENT_TYPE);
            resp.getWriter().write(objectMapper.writeValueAsString(reportController.getCurrentBalance(user)));
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        } else if (PATTERN_STATEMENT.matcher(path).matches()) {
            String fromDate = req.getParameter(PARAMETER_FROM);
            String toDate = req.getParameter(PARAMETER_TO);
            if (fromDate == null || toDate == null) {
                throw new RequiredArgumentException(List.of(PARAMETER_FROM, PARAMETER_TO));
            }
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            resp.setContentType(CONTENT_TYPE);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    reportController.getStatement(
                            user,
                            LocalDate.parse(fromDate, DATE_FORMATTER),
                            LocalDate.parse(toDate, DATE_FORMATTER)
                    )));
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        } else if (PATTERN_CATEGORIES.matcher(path).matches()) {
            String fromDate = req.getParameter(PARAMETER_FROM);
            String toDate = req.getParameter(PARAMETER_TO);
            if (fromDate == null || toDate == null) {
                throw new RequiredArgumentException(List.of(PARAMETER_FROM, PARAMETER_TO));
            }
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            resp.setContentType(CONTENT_TYPE);
            resp.getWriter().write(objectMapper.writeValueAsString(
                    reportController.getCategories(
                            user,
                            LocalDate.parse(fromDate, DATE_FORMATTER),
                            LocalDate.parse(toDate, DATE_FORMATTER)
                    )));
            resp.setStatus(HttpServletResponse.SC_OK);
            return;

        }
        throw new PathNotFoundException(path);
    }

}

