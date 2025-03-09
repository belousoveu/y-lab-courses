package belousov.eu.repository;

import belousov.eu.model.OperationType;
import belousov.eu.model.Transaction;
import belousov.eu.model.User;
import belousov.eu.utils.IdGenerator;
import lombok.AllArgsConstructor;

import java.util.*;

@AllArgsConstructor
public class TransactionRepository {

    private final Map<Integer, Transaction> transactions = new HashMap<>();
    private final IdGenerator<Integer> idCounter = IdGenerator.create(Integer.class);


    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    public Optional<Transaction> findById(int id) {
        return Optional.ofNullable(transactions.get(id));
    }

    public Transaction save(Transaction transaction) {
        if (transaction.getId() == 0) {
            transaction.setId(idCounter.nextId());
        }
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    public void delete(Transaction transaction) {
        transactions.remove(transaction.getId());
    }

    public double getCurrentBalance(User currentUser) {
        double totalDeposit = transactions.values().stream()
                .filter(transaction -> transaction.getUser().equals(currentUser) && transaction.getOperationType() == OperationType.DEPOSIT)
                .mapToDouble(Transaction::getAmount).sum();
        double totalWithdraw = transactions.values().stream()
                .filter(transaction -> transaction.getUser().equals(currentUser) && transaction.getOperationType() == OperationType.WITHDRAW)
                .mapToDouble(Transaction::getAmount).sum();
        return totalDeposit - totalWithdraw;
    }
}
