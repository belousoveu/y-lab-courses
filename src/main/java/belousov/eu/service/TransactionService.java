package belousov.eu.service;

import belousov.eu.model.Category;
import belousov.eu.model.OperationType;
import belousov.eu.model.Transaction;
import belousov.eu.model.TransactionFilter;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    Transaction addTransaction(LocalDate date, OperationType type, Category category, double amount, String description);

    Transaction updateTransaction(int id, Category category, double amount, String description);

    void deleteTransaction(int id);

    List<Transaction> getTransactions(TransactionFilter filter);
}
