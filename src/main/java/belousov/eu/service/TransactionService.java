package belousov.eu.service;

import belousov.eu.model.TransactionFilter;
import belousov.eu.model.User;
import belousov.eu.model.dto.TransactionDto;

import java.util.List;

public interface TransactionService {
    TransactionDto addTransaction(User user, TransactionDto transactionDto);

    TransactionDto updateTransaction(int id, TransactionDto transactionDto, User user);

    void deleteTransaction(int id, User user);

    List<TransactionDto> getTransactions(TransactionFilter filter);

    TransactionDto getTransactionById(int id, User user);
}
