package belousov.eu.controller;

import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.TransactionFilter;
import belousov.eu.model.entity.User;
import belousov.eu.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private static final String CURRENT_USER = "currentUser";

    private final TransactionService transactionService;

    @PostMapping
    public TransactionDto createTransaction(@Valid @RequestBody TransactionDto transactionDto, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return transactionService.addTransaction(user, transactionDto);
    }


    @PutMapping("/{id}")
    public TransactionDto updateTransaction(@PathVariable int id,
                                            @Valid @RequestBody TransactionDto transactionDto,
                                            HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return transactionService.updateTransaction(id, transactionDto, user);
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        transactionService.deleteTransaction(id, user);
    }

    @GetMapping("/{id}")
    public TransactionDto getTransactionById(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return transactionService.getTransactionById(id, user);
    }

    @GetMapping
    public List<TransactionDto> getTransactions(@RequestBody TransactionFilter filter) {
        return transactionService.getTransactions(filter);
    }
}
