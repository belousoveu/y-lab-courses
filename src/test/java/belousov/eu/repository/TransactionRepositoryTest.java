package belousov.eu.repository;

import belousov.eu.config.ConfigLoader;
import belousov.eu.config.HibernateConfig;
import belousov.eu.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Testcontainers
class TransactionRepositoryTest {

    private static final ConfigLoader configLoader = new ConfigLoader("test");

    @Container
    private static final PostgreSQLContainer<?> postgres;

    private static SessionFactory sessionFactory;
    private TransactionRepository transactionRepository;

    private User testUser;
    private Category testCategory;

    static {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
                .withDatabaseName("pmt-test")
                .withUsername("testuser")
                .withPassword("testpassword")
                .withInitScript("init.sql")
        ;

    }

    @BeforeAll
    static void init() {
        postgres.start();
        Map<String, Object> config = configLoader.getConfig();
        config.put("hibernate.connection.url", postgres.getJdbcUrl());
        config.put("hibernate.connection.username", postgres.getUsername());
        config.put("hibernate.connection.password", postgres.getPassword());
        config.put("hibernate.connection.driver_class", postgres.getDriverClassName());
        config.put("hibernate.default_schema", "app");
        sessionFactory = new HibernateConfig(config).getSessionFactory();
    }

    @AfterAll
    static void tearDown() {
        postgres.stop();
    }


    @BeforeEach
    void setUp() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM Transaction").executeUpdate();
            session.createMutationQuery("DELETE FROM Category").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE app.transaction_id_seq RESTART WITH 1", Transaction.class).executeUpdate();
            session.getTransaction().commit();
        }
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            User newUser = new User(0, "user1", "user1@example.com", "Password1", Role.USER, true);
            testUser = session.merge(newUser);
            Category newCategory = new Category(0, "category1", testUser);
            testCategory = session.merge(newCategory);
            session.getTransaction().commit();

        }
        transactionRepository = new TransactionRepository(sessionFactory);
    }

    @Test
    void test_findAll() {
        Transaction transaction = new Transaction(0, LocalDate.of(2025, 3, 1), OperationType.DEPOSIT, testCategory, 100, "test transaction", testUser);
        Transaction transaction2 = new Transaction(0, LocalDate.of(2025, 3, 2), OperationType.WITHDRAW, testCategory, 50, "test transaction", testUser);
        Transaction transaction3 = new Transaction(0, LocalDate.of(2025, 3, 3), OperationType.DEPOSIT, testCategory, 75, "test transaction", testUser);
        Transaction transaction4 = new Transaction(0, LocalDate.of(2025, 3, 4), OperationType.WITHDRAW, testCategory, 25, "test transaction", testUser);

        transactionRepository.save(transaction);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);

        List<Transaction> foundTransactions = transactionRepository.findAll();

        assertThat(foundTransactions).hasSize(4);

    }

    @Test
    void test_findById() {

        Transaction transaction = new Transaction(0, LocalDate.of(2025, 3, 1), OperationType.DEPOSIT, testCategory, 100, "test transaction", testUser);
        Transaction savedTransaction = transactionRepository.save(transaction);

        assertThat(savedTransaction).isNotNull();

        Optional<Transaction> foundTransaction = transactionRepository.findById(savedTransaction.getId());

        assertThat(foundTransaction).isPresent();
        assertThat(foundTransaction.get()).isEqualTo(savedTransaction);

    }

    @Test
    void test_save() {
        Transaction transaction = new Transaction(0, LocalDate.of(2025, 3, 1), OperationType.DEPOSIT, testCategory, 100, "test transaction", testUser);

        Transaction savedTransaction = transactionRepository.save(transaction);

        assertThat(savedTransaction).isNotNull();
        assertThat(savedTransaction.getId()).isPositive();
        assertThat(savedTransaction.getOperationType()).isEqualTo(OperationType.DEPOSIT);
        assertThat(savedTransaction.getCategory()).isEqualTo(testCategory);
        assertThat(savedTransaction.getUser()).isEqualTo(testUser);
        assertThat(savedTransaction.getAmount()).isEqualTo(100);
    }

    @Test
    void test_delete() {

        Transaction transaction = new Transaction(0, LocalDate.of(2025, 3, 1), OperationType.DEPOSIT, testCategory, 100, "test transaction", testUser);
        Transaction savedTransaction = transactionRepository.save(transaction);

        assertThat(savedTransaction).isNotNull();

        transactionRepository.delete(savedTransaction);

        Optional<Transaction> deletedTransaction = transactionRepository.findById(savedTransaction.getId());
        assertThat(deletedTransaction).isEmpty();
    }

    @Test
    void test_getCurrentBalance() {

        Transaction transaction = new Transaction(0, LocalDate.of(2025, 3, 1), OperationType.DEPOSIT, testCategory, 100, "test transaction", testUser);
        Transaction transaction2 = new Transaction(0, LocalDate.of(2025, 3, 2), OperationType.WITHDRAW, testCategory, 50, "test transaction", testUser);
        Transaction transaction3 = new Transaction(0, LocalDate.of(2025, 3, 3), OperationType.DEPOSIT, testCategory, 75, "test transaction", testUser);
        Transaction transaction4 = new Transaction(0, LocalDate.of(2025, 3, 4), OperationType.WITHDRAW, testCategory, 25, "test transaction", testUser);

        transactionRepository.save(transaction);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);

        double currentBalance = transactionRepository.getCurrentBalance(testUser);
        assertThat(currentBalance).isEqualTo(100);
    }
}