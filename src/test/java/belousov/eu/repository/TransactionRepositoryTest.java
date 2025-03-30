package belousov.eu.repository;

import belousov.eu.model.entity.*;
import belousov.eu.repository.imp.CategoryRepositoryImp;
import belousov.eu.repository.imp.TransactionRepositoryImp;
import belousov.eu.repository.imp.UserRepositoryImp;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Testcontainers
class TransactionRepositoryTest {


    @Container
    private static final PostgreSQLContainer<?> postgres;

    private static JdbcTemplate jdbcTemplate;
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
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());

        HikariDataSource dataSource = new HikariDataSource(config);
        jdbcTemplate = new JdbcTemplate(dataSource);

        runLiquibaseMigrations(dataSource);
    }

    private static void runLiquibaseMigrations(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setLiquibaseSchemaName("service");
            database.setDefaultSchemaName("app");

            Liquibase liquibase = new Liquibase(
                    "db/changelog/changelog.yml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update();
        } catch (Exception e) {
            throw new RuntimeException("Liquibase migration failed", e);
        }
    }

    @AfterAll
    static void tearDown() {
        postgres.stop();
    }


    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE app.transactions CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE app.categories CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE app.users CASCADE");
        transactionRepository = new TransactionRepositoryImp(jdbcTemplate);
        UserRepository userRepository = new UserRepositoryImp(jdbcTemplate);
        CategoryRepository categoryRepository = new CategoryRepositoryImp(jdbcTemplate);
        testUser = userRepository.save(new User(0, "user1", "user1@example.com", "Password1", Role.USER, true));
        testCategory = categoryRepository.save(new Category(0, "category1", testUser));
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