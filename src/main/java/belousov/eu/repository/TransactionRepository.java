package belousov.eu.repository;

import belousov.eu.model.entity.Transaction;
import belousov.eu.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    List<Transaction> findAll();

    Optional<Transaction> findById(int id);

    Transaction save(Transaction transaction);

    void delete(Transaction transaction);

    double getCurrentBalance(User currentUser);
}
