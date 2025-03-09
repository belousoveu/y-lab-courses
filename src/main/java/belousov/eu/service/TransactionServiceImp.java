package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.TransactionNotFoundException;
import belousov.eu.model.Category;
import belousov.eu.model.OperationType;
import belousov.eu.model.Transaction;
import belousov.eu.model.TransactionFilter;
import belousov.eu.repository.TransactionRepository;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
public class TransactionServiceImp implements TransactionService, AdminAccessTransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction addTransaction(LocalDate date, OperationType type, Category category, double amount, String description) {
        return transactionRepository
                .save(new Transaction(0, date, type, category, amount, description, PersonalMoneyTracker.getCurrentUser()));
    }

    @Override
    public Transaction updateTransaction(int id, Category category, double amount, String description) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        checkTransactionBelongsToCurrentUser(transaction);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        return transactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(int id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        checkTransactionBelongsToCurrentUser(transaction);
        transactionRepository.delete(transaction);

    }

    @Override
    public List<Transaction> getTransactions(TransactionFilter filter) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getUser().equals(filter.getUser()))
                .filter(t -> filter.getFrom() == null || filter.getFrom().isBefore(t.getDate()))
                .filter(t -> filter.getTo() == null || filter.getTo().isAfter(t.getDate()))
                .filter(t -> filter.getCategory() == null || filter.getCategory().equals(t.getCategory()))
                .filter(t -> filter.getType() == null || filter.getType().equals(t.getOperationType()))
                .toList();
    }

    @Override
    public List<String> getAllTransactions() {
        return transactionRepository.findAll().stream().map(Transaction::toStringWithUser).toList();
    }

    private void checkTransactionBelongsToCurrentUser(Transaction transaction) {
        if (!transaction.getUser().equals(PersonalMoneyTracker.getCurrentUser())) {
            throw new TransactionNotFoundException(transaction.getId());
        }
    }
}
