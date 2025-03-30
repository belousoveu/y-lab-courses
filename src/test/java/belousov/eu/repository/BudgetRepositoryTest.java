package belousov.eu.repository;

import belousov.eu.model.entity.Budget;
import belousov.eu.model.entity.Category;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import belousov.eu.repository.imp.BudgetRepositoryImp;
import belousov.eu.repository.imp.CategoryRepositoryImp;
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
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Testcontainers
class BudgetRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres;

    private static JdbcTemplate jdbcTemplate;

    private BudgetRepositoryImp budgetRepository;

    private User testUser;
    private Category testCategory;

    static {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
                .withDatabaseName("pmt-test")
                .withUsername("testuser")
                .withPassword("testpassword")
                .withInitScript("init.sql");
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
        jdbcTemplate.execute("TRUNCATE TABLE app.budgets RESTART IDENTITY CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE app.categories RESTART IDENTITY CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE app.users RESTART IDENTITY CASCADE;");
        budgetRepository = new BudgetRepositoryImp(jdbcTemplate);
        CategoryRepository categoryRepository = new CategoryRepositoryImp(jdbcTemplate);
        UserRepository userRepository = new UserRepositoryImp(jdbcTemplate);
        testUser = userRepository.save(new User(0, "user1", "user1@example.com", "Password1", Role.USER, true));
        testCategory = categoryRepository.save(new Category(0, "category1", testUser));
    }

    @Test
    void test_save() {
        Budget budget = budgetRepository.save(new Budget(0, LocalDate.of(2025, 3, 1), testCategory, testUser, 100));

        List<Budget> foundBudgets = budgetRepository.findAllByPeriod(testUser, budget.getPeriod());
        assertThat(foundBudgets).hasSize(1);
        assertThat(foundBudgets.get(0).getId()).isEqualTo(budget.getId());
        assertThat(foundBudgets.get(0).getUser()).isEqualTo(testUser);
    }

    @Test
    void test_findAllByPeriod() {
        budgetRepository.save(new Budget(0, LocalDate.of(2025, 3, 1), testCategory, testUser, 100));
        budgetRepository.save(new Budget(0, LocalDate.of(2025, 3, 1), testCategory, testUser, 200));

        List<Budget> foundBudgets = budgetRepository.findAllByPeriod(testUser, YearMonth.of(2025, 3));
        assertThat(foundBudgets).hasSize(2);
    }

    @Test
    void test_findByCategoryAndPeriod() {
        budgetRepository.save(new Budget(0, LocalDate.of(2025, 3, 1), testCategory, testUser, 100));
        budgetRepository.save(new Budget(0, LocalDate.of(2025, 2, 1), testCategory, testUser, 200));

        Optional<Budget> foundBudget = budgetRepository.findByCategoryAndPeriod(testCategory, testUser, YearMonth.of(2025, 3));
        assertThat(foundBudget).isPresent();
        assertThat(foundBudget.get().getCategory().getName()).isEqualTo(testCategory.getName());
        assertThat(foundBudget.get().getUser()).isEqualTo(testUser);
        assertThat(foundBudget.get().getPeriod()).isEqualTo(YearMonth.of(2025, 3));
    }
}