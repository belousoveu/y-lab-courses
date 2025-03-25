package belousov.eu.servlet;

import belousov.eu.annotation.AuthorizationRequired;
import belousov.eu.controller.TransactionController;
import belousov.eu.exception.PathNotFoundException;
import belousov.eu.model.Category;
import belousov.eu.model.OperationType;
import belousov.eu.model.TransactionFilter;
import belousov.eu.model.User;
import belousov.eu.model.dto.TransactionDto;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AuthorizationRequired
@RequiredArgsConstructor
public class TransactionServlet extends HttpServlet {

    private static final String CURRENT_USER = "currentUser";
    private static final String CONTENT_TYPE = "application/json";
    private static final Pattern PATTERN_ALL_TRANSACTIONS = Pattern.compile("^/transactions/(\\d+)$");
    private static final Pattern PATTERN_ONE_TRANSACTION = Pattern.compile("^/transaction/(\\d+)/(\\d+)$");
    private static final Pattern PATTERN_FILTER_TRANSACTIONS = Pattern.compile("^/api/transaction/(\\d+)/search(\\?.*)?$");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private final transient TransactionController transactionController;
    private final ObjectMapper objectMapper;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        String query = req.getQueryString();
        String fullPath = query == null ? path : path + "?" + query;
        if (PATTERN_ALL_TRANSACTIONS.matcher(fullPath).matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            List<TransactionDto> transactions = transactionController.getTransactions(
                    TransactionFilter.builder().user(user).build()
            );
            resp.setContentType(CONTENT_TYPE);
            resp.getWriter().write(objectMapper.writeValueAsString(transactions));
            resp.setStatus(HttpServletResponse.SC_OK);
        } else if (PATTERN_ONE_TRANSACTION.matcher(fullPath).matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            Matcher matcher = PATTERN_ONE_TRANSACTION.matcher(path);
            matcher.matches();
            int id = Integer.parseInt(matcher.group(2));
            resp.setContentType(CONTENT_TYPE);
            resp.getWriter().write(objectMapper.writeValueAsString(transactionController.getTransactionById(id, user)));
            resp.setStatus(HttpServletResponse.SC_OK);
        } else if (PATTERN_FILTER_TRANSACTIONS.matcher(fullPath).matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);

            List<TransactionDto> transactions = transactionController.getTransactions(
                    TransactionFilter.builder()
                            .user(user)
                            .from(LocalDate.parse(req.getParameter("from"), DATE_FORMAT))
                            .to(LocalDate.parse(req.getParameter("to"), DATE_FORMAT))
                            .category(new Category(0, req.getParameter("category"), user))
                            .type(OperationType.valueOf(req.getParameter("type")))
                            .build()
            );
            resp.setContentType(CONTENT_TYPE);
            resp.getWriter().write(objectMapper.writeValueAsString(transactions));
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            throw new PathNotFoundException(fullPath);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (PATTERN_ALL_TRANSACTIONS.matcher(path).matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            TransactionDto transactionDto = objectMapper.readValue(req.getInputStream(), TransactionDto.class);
            resp.getWriter().write(objectMapper.writeValueAsString(transactionController.createTransaction(user, transactionDto)));
            resp.setContentType(CONTENT_TYPE);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            return;
        }
        throw new PathNotFoundException(path);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        Matcher matcher = PATTERN_ONE_TRANSACTION.matcher(path);
        if (matcher.matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            int id = Integer.parseInt(matcher.group(2));
            TransactionDto transactionDto = objectMapper.readValue(req.getInputStream(), TransactionDto.class);
            resp.getWriter().write(objectMapper.writeValueAsString(transactionController.updateTransaction(id, transactionDto, user)));
            resp.setContentType(CONTENT_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        throw new PathNotFoundException(path);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getPathInfo();
        Matcher matcher = PATTERN_ONE_TRANSACTION.matcher(path);
        if (matcher.matches()) {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute(CURRENT_USER);
            int id = Integer.parseInt(matcher.group(2));
            transactionController.deleteTransaction(id, user);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        throw new PathNotFoundException(path);
    }
}
