package belousov.eu.repository;

import belousov.eu.model.entity.Category;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Testcontainers
class CategoryRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres;

    private static JdbcTemplate jdbcTemplate;

    private CategoryRepositoryImp categoryRepository;

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
        jdbcTemplate.execute("TRUNCATE TABLE app.categories RESTART IDENTITY CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE app.users RESTART IDENTITY CASCADE;");
        categoryRepository = new CategoryRepositoryImp(jdbcTemplate);
        UserRepository userRepository = new UserRepositoryImp(jdbcTemplate);
        testUser = userRepository.save(new User(0, "user1", "user1@example.com", "Password1", Role.USER, true));
    }


    @Test
    void test_findById_success() {

        Category category = categoryRepository.save(new Category(0, "Test Category", testUser));

        Optional<Category> foundCategory = categoryRepository.findById(category.getId());
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Test Category");
        assertThat(foundCategory.get().getUser()).isEqualTo(testUser);


    }

    @Test
    void test_save() {
        Category category = categoryRepository.save(new Category(0, "Test Category", testUser));

        Optional<Category> foundCategory = categoryRepository.findById(category.getId());
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getId()).isEqualTo(category.getId());
        assertThat(foundCategory.get().getName()).isEqualTo(category.getName());
        assertThat(foundCategory.get().getUser()).isEqualTo(testUser);
    }

    @Test
    void test_delete() {
        Category category = categoryRepository.save(new Category(0, "Test Category", testUser));

        Optional<Category> foundCategory = categoryRepository.findById(category.getId());
        assertThat(foundCategory).isPresent();

        categoryRepository.delete(foundCategory.get());

        Optional<Category> deletedCategory = categoryRepository.findById(category.getId());
        assertThat(deletedCategory).isNotPresent();
    }

    @Test
    void test_findAllByUser() {
        categoryRepository.save(new Category(0, "Test Category", testUser));

        List<Category> foundCategories = categoryRepository.findAllByUser(testUser);
        assertThat(foundCategories).hasSize(1);
        assertThat(foundCategories.get(0).getName()).isEqualTo("Test Category");

    }
}