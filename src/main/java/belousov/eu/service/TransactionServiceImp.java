package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.TransactionNotFoundException;
import belousov.eu.model.*;
import belousov.eu.model.reportDto.IncomeStatement;
import belousov.eu.observer.BalanceChangeSubject;
import belousov.eu.repository.TransactionRepository;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TransactionServiceImp implements TransactionService, AdminAccessTransactionService, ReportService {

    private final TransactionRepository transactionRepository;
    private final BalanceChangeSubject balanceChangeSubject;

    @Override
    public Transaction addTransaction(LocalDate date, OperationType type, Category category, double amount, String description) {
        Transaction transaction = transactionRepository
                .save(new Transaction(0, date, type, category, amount, description, PersonalMoneyTracker.getCurrentUser()));
        balanceChangeSubject.notifyObservers(transaction);
        return transaction;
    }

    @Override
    public Transaction updateTransaction(int id, Category category, double amount, String description) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        checkTransactionBelongsToCurrentUser(transaction);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        balanceChangeSubject.notifyObservers(updatedTransaction);
        return updatedTransaction;
    }

    @Override
    public void deleteTransaction(int id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        checkTransactionBelongsToCurrentUser(transaction);
        transactionRepository.delete(transaction);
        balanceChangeSubject.notifyObservers(transaction);
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


    @Override
    public double getCurrentBalance() {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        return transactionRepository.getCurrentBalance(currentUser);
    }

    @Override
    public String getIncomeStatement(LocalDate from, LocalDate to) {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        List<Transaction> transactions = getTransactions(TransactionFilter.builder().user(currentUser).from(from).to(to).build());
        return IncomeStatement.builder()
                .user(currentUser)
                .from(from)
                .to(to)
                .income(transactions.stream().filter(t -> t.getOperationType().equals(OperationType.DEPOSIT)).mapToDouble(Transaction::getAmount).sum())
                .outcome(transactions.stream().filter(t -> t.getOperationType().equals(OperationType.WITHDRAW)).mapToDouble(Transaction::getAmount).sum())
                .build()
                .toString();
    }

    @Override
    public List<String> getCostsByCategory(LocalDate from, LocalDate to) {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        List<Transaction> transactions = getTransactions(
                TransactionFilter.builder().user(currentUser).from(from).to(to).type(OperationType.WITHDRAW).build()
        );

        Map<String, Double> costByCategory = transactions.stream().collect(Collectors.groupingBy(
                t -> Optional.ofNullable(t.getCategory())
                        .map(Category::getName)
                        .orElse("Без категории"),
                Collectors.summingDouble(Transaction::getAmount)));
        double totalCost = transactions.stream().mapToDouble(Transaction::getAmount).sum();
        costByCategory.put("Итого по всем категориям:", totalCost);

        return costByCategory
                .entrySet().stream()
                .map(e -> e.getKey() + " : " + e.getValue())
                .toList();
    }

    private void checkTransactionBelongsToCurrentUser(Transaction transaction) {
        if (!transaction.getUser().equals(PersonalMoneyTracker.getCurrentUser())) {
            throw new TransactionNotFoundException(transaction.getId());
        }
    }
}
