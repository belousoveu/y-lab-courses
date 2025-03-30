package belousov.eu.repository;

import belousov.eu.model.entity.Goal;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import belousov.eu.repository.imp.GoalRepositoryImp;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Testcontainers
class GoalRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres;

    private static JdbcTemplate jdbcTemplate;

    private GoalRepository goalRepository;

    private User testUser;

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
        jdbcTemplate.execute("TRUNCATE TABLE app.goals RESTART IDENTITY CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE app.users RESTART IDENTITY CASCADE;");
        UserRepository userRepository = new UserRepositoryImp(jdbcTemplate);
        goalRepository = new GoalRepositoryImp(jdbcTemplate);
        testUser = userRepository.save(new User(0, "user1", "user1@example.com", "Password1", Role.USER, true));
    }

    @Test
    void test_findById() {
        Goal newGoal = goalRepository.save(new Goal(0, testUser, "Test Goal", "Test Description", 100));

        Optional<Goal> foundGoal = goalRepository.findById(newGoal.getId());

        assertThat(foundGoal).isPresent();
        assertThat(foundGoal.get().getId()).isNotZero();
        assertThat(foundGoal.get().getName()).isEqualTo(newGoal.getName());
        assertThat(foundGoal.get().getDescription()).isEqualTo(newGoal.getDescription());
        assertThat(foundGoal.get().getUser()).isEqualTo(testUser);
    }

    @Test
    void test_findAllByUser() {
        Goal newGoal = goalRepository.save(new Goal(0, testUser, "Test Goal", "Test Description", 100));

        List<Goal> foundGoals = goalRepository.findAllByUser(testUser.getId());
        assertThat(foundGoals).hasSize(1);
        assertThat(foundGoals.get(0).getName()).isEqualTo(newGoal.getName());
        assertThat(foundGoals.get(0).getDescription()).isEqualTo(newGoal.getDescription());

    }

    @Test
    void test_delete() {
        Goal newGoal = goalRepository.save(new Goal(0, testUser, "Test Goal", "Test Description", 100));

        Optional<Goal> foundGoal = goalRepository.findById(newGoal.getId());

        assertThat(foundGoal).isPresent();

        goalRepository.delete(foundGoal.get());
        Optional<Goal> deletedGoal = goalRepository.findById(foundGoal.get().getId());
        assertThat(deletedGoal).isNotPresent();

    }

    @Test
    void test_save() {

        Goal newGoal = goalRepository.save(new Goal(0, testUser, "Test Goal", "Test Description", 100));

        Optional<Goal> foundGoal = goalRepository.findById(newGoal.getId());

        assertThat(foundGoal).isPresent();
        assertThat(foundGoal.get().getId()).isNotZero();
        assertThat(foundGoal.get().getName()).isEqualTo(newGoal.getName());
        assertThat(foundGoal.get().getDescription()).isEqualTo(newGoal.getDescription());
        assertThat(foundGoal.get().getUser()).isEqualTo(testUser);
    }
}