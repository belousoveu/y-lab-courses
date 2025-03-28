package belousov.eu.service;

import belousov.eu.annotation.Loggable;
import belousov.eu.model.dto.TransactionDto;
import belousov.eu.model.dto.TransactionFilter;
import belousov.eu.model.entity.User;

import java.util.List;

public interface TransactionService {

    @Loggable
    TransactionDto addTransaction(User user, TransactionDto transactionDto);

    @Loggable
    TransactionDto updateTransaction(int id, TransactionDto transactionDto, User user);

    @Loggable
    void deleteTransaction(int id, User user);

    List<TransactionDto> getTransactions(TransactionFilter filter);

    TransactionDto getTransactionById(int id, User user);
}
