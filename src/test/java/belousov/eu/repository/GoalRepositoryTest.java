package belousov.eu.repository;

import belousov.eu.config.ConfigLoader;
import belousov.eu.config.HibernateConfig;
import belousov.eu.model.Goal;
import belousov.eu.model.Role;
import belousov.eu.model.User;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Testcontainers
class GoalRepositoryTest {
    private static final ConfigLoader configLoader = new ConfigLoader("test");

    @Container
    private static final PostgreSQLContainer<?> postgres;

    private static SessionFactory sessionFactory;
    private GoalRepository goalRepository;

    private User testUser;

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
            session.createMutationQuery("DELETE FROM Goal").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE app.goal_id_seq RESTART WITH 1", Goal.class).executeUpdate();
            session.getTransaction().commit();
        }
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            User newUser = new User(0, "user1", "user1@example.com", "Password1", Role.USER, true);
            testUser = session.merge(newUser);
            session.getTransaction().commit();

        }
        goalRepository = new GoalRepository(sessionFactory);
    }

    @Test
    void test_findById() {
        Goal newGoal = new Goal(0, testUser, "Test Goal", "Test Description", 100);
        goalRepository.save(newGoal);

        Optional<Goal> foundGoal = goalRepository.findById(1);

        assertThat(foundGoal).isPresent();
        assertThat(foundGoal.get().getId()).isNotZero();
        assertThat(foundGoal.get().getName()).isEqualTo("Test Goal");
        assertThat(foundGoal.get().getDescription()).isEqualTo("Test Description");
    }

    @Test
    void test_findAllByUser() {
        Goal newGoal = new Goal(0, testUser, "Test Goal", "Test Description", 100);
        goalRepository.save(newGoal);

        List<Goal> foundGoals = goalRepository.findAllByUser(testUser.getId());
        assertThat(foundGoals).hasSize(1);
        assertThat(foundGoals.get(0).getName()).isEqualTo("Test Goal");
        assertThat(foundGoals.get(0).getDescription()).isEqualTo("Test Description");

    }

    @Test
    void test_delete() {
        Goal newGoal = new Goal(0, testUser, "Test Goal", "Test Description", 100);
        goalRepository.save(newGoal);

        Optional<Goal> foundGoal = goalRepository.findById(1);

        assertThat(foundGoal).isPresent();

        goalRepository.delete(foundGoal.get());
        Optional<Goal> deletedGoal = goalRepository.findById(1);
        assertThat(deletedGoal).isNotPresent();

    }

    @Test
    void test_save() {

        Goal newGoal = new Goal(0, testUser, "Test Goal", "Test Description", 100);
        goalRepository.save(newGoal);

        Optional<Goal> foundGoal = goalRepository.findById(1);

        assertThat(foundGoal).isPresent();
        assertThat(foundGoal.get().getId()).isNotZero();
        assertThat(foundGoal.get().getName()).isEqualTo("Test Goal");
        assertThat(foundGoal.get().getDescription()).isEqualTo("Test Description");
        assertThat(foundGoal.get().getUser()).isEqualTo(testUser);
    }
}