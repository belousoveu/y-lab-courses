package belousov.eu.repository;

import belousov.eu.config.ConfigLoader;
import belousov.eu.model.entity.Budget;
import belousov.eu.model.entity.Category;
import belousov.eu.model.entity.User;
import belousov.eu.repository.imp.BudgetRepositoryImp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Testcontainers
class BudgetRepositoryTest {

    private static final ConfigLoader configLoader = new ConfigLoader("test");

    @Container
    private static final PostgreSQLContainer<?> postgres;

    //    private static SessionFactory sessionFactory;
    private BudgetRepositoryImp budgetRepository;

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
//        sessionFactory = new HibernateConfig(config).getSessionFactory();
    }

    @AfterAll
    static void tearDown() {
        postgres.stop();
    }


    @BeforeEach
    void setUp() {
//        try (Session session = sessionFactory.openSession()) {
//            session.beginTransaction();
//            session.createMutationQuery("DELETE FROM Budget").executeUpdate();
//            session.createMutationQuery("DELETE FROM Category").executeUpdate();
//            session.createMutationQuery("DELETE FROM User").executeUpdate();
//            session.createNativeQuery("ALTER SEQUENCE app.budget_id_seq RESTART WITH 1", Budget.class).executeUpdate();
//            session.getTransaction().commit();
//        }
//        try (Session session = sessionFactory.openSession()) {
//            session.beginTransaction();
//            User newUser = new User(0, "user1", "user1@example.com", "Password1", Role.USER, true);
//            testUser = session.merge(newUser);
//            Category newCategory = new Category(0, "category1", testUser);
//            testCategory = session.merge(newCategory);
//            session.getTransaction().commit();
//
//        }
//        budgetRepository = new BudgetRepositoryImp(sessionFactory);
    }

    @Test
    void test_save() {
        Budget budget = new Budget(0, LocalDate.of(2025, 3, 1), testCategory, testUser, 100);
        budgetRepository.save(budget);

        List<Budget> foundBudgets = budgetRepository.findAllByPeriod(testUser, YearMonth.of(2025, 3));
        assertThat(foundBudgets).hasSize(1);
        assertThat(foundBudgets.get(0).getId()).isEqualTo(1);
        assertThat(foundBudgets.get(0).getUser()).isEqualTo(testUser);
    }

    @Test
    void test_findAllByPeriod() {
        Budget budget = new Budget(0, LocalDate.of(2025, 3, 1), testCategory, testUser, 100);
        budgetRepository.save(budget);
        Budget budget2 = new Budget(0, LocalDate.of(2025, 3, 1), testCategory, testUser, 200);
        budgetRepository.save(budget2);

        List<Budget> foundBudgets = budgetRepository.findAllByPeriod(testUser, YearMonth.of(2025, 3));
        assertThat(foundBudgets).hasSize(2);
    }

    @Test
    void test_findByCategoryAndPeriod() {
        Budget budget = new Budget(0, LocalDate.of(2025, 3, 1), testCategory, testUser, 100);
        budgetRepository.save(budget);
        Budget budget2 = new Budget(0, LocalDate.of(2025, 2, 1), testCategory, testUser, 200);
        budgetRepository.save(budget2);

        Optional<Budget> foundBudget = budgetRepository.findByCategoryAndPeriod(testCategory, testUser, YearMonth.of(2025, 3));
        assertThat(foundBudget).isPresent();
        assertThat(foundBudget.get().getCategory()).isEqualTo(testCategory);
        assertThat(foundBudget.get().getUser()).isEqualTo(testUser);
        assertThat(foundBudget.get().getPeriod()).isEqualTo(YearMonth.of(2025, 3));
    }
}