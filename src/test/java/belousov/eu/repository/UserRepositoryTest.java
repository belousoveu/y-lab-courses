package belousov.eu.repository;

import belousov.eu.config.ConfigLoader;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import belousov.eu.repository.imp.UserRepositoryImp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@Testcontainers
class UserRepositoryTest {

    private static final ConfigLoader configLoader = new ConfigLoader("test");

    @Container
    private static final PostgreSQLContainer<?> postgres;

    private JdbcTemplate jdbcTemplate;
    private UserRepositoryImp userRepository;

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
    }

    @AfterAll
    static void tearDown() {
        postgres.stop();
    }


    @BeforeEach
    void setUp() {
//        userRepository = new UserRepositoryImp(sessionFactory);
    }


    @Test
    void test_init_checkForDefaultAdminWasCreated() {
        List<Integer> adminIds = userRepository.getAllAdminIds();
        assertThat(adminIds).hasSize(1);

        Optional<User> admin = userRepository.findByEmail("admin@admin.com");
        assertThat(admin).isPresent();
        assertThat(admin.get().getName()).isEqualTo("admin");
        assertThat(admin.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void save() {
        User newUser = new User(0, "testuser", "test@test.com", "Password123", Role.USER, true);
        User savedUser = userRepository.save(newUser);

        assertThat(savedUser.getId()).isNotZero();
        assertThat(savedUser.getName()).isEqualTo(newUser.getName());

        Optional<User> foundUser = userRepository.findByEmail(newUser.getEmail());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(savedUser);
    }

    @Test
    void delete() {

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

        Optional<User> foundUser = userRepository.findById(1);
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(1);
        assertThat(foundUser.get().getName()).isEqualTo("admin");
        assertThat(foundUser.get().getEmail()).isEqualTo("admin@admin.com");
        assertThat(foundUser.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void findByEmail() {
        Optional<User> foundUser = userRepository.findByEmail("admin@admin.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(1);
        assertThat(foundUser.get().getName()).isEqualTo("admin");
        assertThat(foundUser.get().getEmail()).isEqualTo("admin@admin.com");
        assertThat(foundUser.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void findAll() {

        User newUser = new User(0, "testuser", "test@test.com", "Password123", Role.USER, true);
        userRepository.save(newUser);
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(2);

    }

    @Test
    void getAllAdminIds() {
        List<Integer> adminIds = userRepository.getAllAdminIds();
        assertThat(adminIds).hasSize(1);
    }

}