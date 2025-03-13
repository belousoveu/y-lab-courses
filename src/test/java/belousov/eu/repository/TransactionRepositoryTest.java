package belousov.eu.repository;

import belousov.eu.model.OperationType;
import belousov.eu.model.Role;
import belousov.eu.model.Transaction;
import belousov.eu.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тестовый класс для {@link TransactionRepository}.
 */
class TransactionRepositoryTest {

    private TransactionRepository transactionRepository;
    private User user;

    @BeforeEach
    void setUp() {
        transactionRepository = new TransactionRepository();
        user = new User(1, "John Doe", "john@example.com", "password123", Role.USER, true);
    }

    @Test
    void test_save_whenNewTransaction_shouldAddTransactionAndGenerateId() {
        Transaction transaction = new Transaction(0, LocalDate.now(), OperationType.DEPOSIT, null, 100.0, "Deposit", user);
        Transaction savedTransaction = transactionRepository.save(transaction);

        assertThat(savedTransaction.getId()).isNotZero();
        assertThat(transactionRepository.findById(savedTransaction.getId())).contains(savedTransaction);
    }

    @Test
    void test_save_whenExistingTransaction_shouldUpdateTransaction() {
        Transaction transaction = new Transaction(0, LocalDate.now(), OperationType.DEPOSIT, null, 100.0, "Deposit", user);
        Transaction savedTransaction = transactionRepository.save(transaction);

        savedTransaction.setAmount(200.0);
        transactionRepository.save(savedTransaction);

        Optional<Transaction> updatedTransaction = transactionRepository.findById(savedTransaction.getId());
        assertThat(updatedTransaction).isPresent();
        assertThat(updatedTransaction.get().getAmount()).isEqualTo(200.0);
    }

    @Test
    void test_delete_shouldRemoveTransaction() {
        Transaction transaction = new Transaction(0, LocalDate.now(), OperationType.DEPOSIT, null, 100.0, "Deposit", user);
        Transaction savedTransaction = transactionRepository.save(transaction);

        transactionRepository.delete(savedTransaction);
        assertThat(transactionRepository.findById(savedTransaction.getId())).isEmpty();
    }

    @Test
    void test_findById_whenTransactionExists_shouldReturnTransaction() {
        Transaction transaction = new Transaction(0, LocalDate.now(), OperationType.DEPOSIT, null, 100.0, "Deposit", user);
        Transaction savedTransaction = transactionRepository.save(transaction);

        Optional<Transaction> foundTransaction = transactionRepository.findById(savedTransaction.getId());
        assertThat(foundTransaction).contains(savedTransaction);
    }

    @Test
    void test_findById_whenTransactionDoesNotExist_shouldReturnEmpty() {
        Optional<Transaction> foundTransaction = transactionRepository.findById(999);
        assertThat(foundTransaction).isEmpty();
    }

    @Test
    void test_findAll_shouldReturnAllTransactions() {
        Transaction transaction1 = new Transaction(0, LocalDate.now(), OperationType.DEPOSIT, null, 100.0, "Deposit", user);
        Transaction transaction2 = new Transaction(0, LocalDate.now(), OperationType.WITHDRAW, null, 50.0, "Withdraw", user);
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);

        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions).containsExactlyInAnyOrder(transaction1, transaction2);
    }

    @Test
    void test_getCurrentBalance_whenDepositsAndWithdrawsExist_shouldReturnCorrectBalance() {
        Transaction deposit1 = new Transaction(0, LocalDate.now(), OperationType.DEPOSIT, null, 100.0, "Deposit 1", user);
        Transaction deposit2 = new Transaction(0, LocalDate.now(), OperationType.DEPOSIT, null, 200.0, "Deposit 2", user);
        Transaction withdraw1 = new Transaction(0, LocalDate.now(), OperationType.WITHDRAW, null, 50.0, "Withdraw 1", user);
        Transaction withdraw2 = new Transaction(0, LocalDate.now(), OperationType.WITHDRAW, null, 75.0, "Withdraw 2", user);

        transactionRepository.save(deposit1);
        transactionRepository.save(deposit2);
        transactionRepository.save(withdraw1);
        transactionRepository.save(withdraw2);

        double balance = transactionRepository.getCurrentBalance(user);
        assertThat(balance).isEqualTo(100.0 + 200.0 - 50.0 - 75.0);
    }

    @Test
    void test_getCurrentBalance_whenNoTransactionsExist_shouldReturnZero() {
        double balance = transactionRepository.getCurrentBalance(user);
        assertThat(balance).isEqualTo(0.0);
    }
}