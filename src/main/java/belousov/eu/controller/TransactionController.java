package belousov.eu.controller;

import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.TransactionFilter;
import belousov.eu.model.entity.User;
import belousov.eu.service.TransactionService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionDto createTransaction(User user, TransactionDto transactionDto) {
        return transactionService.addTransaction(user, transactionDto);
    }


    public TransactionDto updateTransaction(int id, TransactionDto transactionDto, User user) {
        return transactionService.updateTransaction(id, transactionDto, user);
    }

    public void deleteTransaction(int id, User user) {
        transactionService.deleteTransaction(id, user);
    }

    public TransactionDto getTransactionById(int id, User user) {
        return transactionService.getTransactionById(id, user);
    }

    public List<TransactionDto> getTransactions(TransactionFilter filter) {
        return transactionService.getTransactions(filter);
    }
}
