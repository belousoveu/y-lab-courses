package belousov.eu.repository;

import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
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
class UserRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres;

    private static JdbcTemplate jdbcTemplate;

    private UserRepository userRepository;

    static {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
                .withDatabaseName("pmt_test")
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
        jdbcTemplate.execute("TRUNCATE TABLE app.users CASCADE");
        userRepository = new UserRepositoryImp(jdbcTemplate);

    }



    @Test
    void test_save_whenNewUser() {
        User newUser = new User(0, "testuser", "test@test.com", "Password123", Role.USER, true);
        User savedUser = userRepository.save(newUser);

        assertThat(savedUser.getId()).isNotZero();
        assertThat(savedUser.getName()).isEqualTo(newUser.getName());

        Optional<User> foundUser = userRepository.findByEmail(newUser.getEmail());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(savedUser);
    }

    @Test
    void test_delete() {

        User newUser = new User(0, "testuser", "test@test.com", "Password123", Role.USER, true);
        User savedUser = userRepository.save(newUser);

        Optional<User> foundUser = userRepository.findByEmail(newUser.getEmail());
        assertThat(foundUser).isPresent();

        userRepository.delete(savedUser);

        Optional<User> deletedUser = userRepository.findByEmail(newUser.getEmail());
        assertThat(deletedUser).isNotPresent();
    }

    @Test
    void findById() {
        User testUser = userRepository.save(new User(0, "testuser", "test@test.com", "Password123", Role.USER, true));
        Optional<User> foundUser = userRepository.findById(testUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(testUser.getId());
        assertThat(foundUser.get().getName()).isEqualTo(testUser.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(testUser.getEmail());
        assertThat(foundUser.get().getRole()).isEqualTo(testUser.getRole());
    }

    @Test
    void findByEmail() {
        User testUser = userRepository.save(new User(0, "testuser", "test@test.com", "Password123", Role.USER, true));
        Optional<User> foundUser = userRepository.findByEmail(testUser.getEmail());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(testUser.getId());
        assertThat(foundUser.get().getName()).isEqualTo(testUser.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(testUser.getEmail());
        assertThat(foundUser.get().getRole()).isEqualTo(testUser.getRole());
    }

    @Test
    void findAll() {

        User newUser = new User(0, "testuser", "test@test.com", "Password123", Role.USER, true);
        userRepository.save(newUser);
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);

    }

    @Test
    void getAllAdminIds() {
        User testAdmin = userRepository.save(new User(0, "admin", "admin@test.com", "Password123", Role.ADMIN, true));
        List<Integer> adminIds = userRepository.getAllAdminIds();
        assertThat(adminIds).hasSize(1);
        assertThat(adminIds).contains(testAdmin.getId());
    }

}