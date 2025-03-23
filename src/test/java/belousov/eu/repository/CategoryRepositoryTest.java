package belousov.eu.repository;

import belousov.eu.config.ConfigLoader;
import belousov.eu.config.HibernateConfig;
import belousov.eu.model.Category;
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
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class CategoryRepositoryTest {

    private static final ConfigLoader configLoader = new ConfigLoader("test");

    @Container
    private static final PostgreSQLContainer<?> postgres;

    private static SessionFactory sessionFactory;
    private CategoryRepository categoryRepository;

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
            session.createMutationQuery("DELETE FROM Category").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.createNativeQuery("ALTER SEQUENCE app.category_id_seq RESTART WITH 1", Category.class).executeUpdate();
            session.getTransaction().commit();
        }
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            User newUser = new User(0, "user1", "user1@example.com", "Password1", Role.USER, true);
            testUser = session.merge(newUser);
            session.getTransaction().commit();

        }
        categoryRepository = new CategoryRepository(sessionFactory);
    }


    @Test
    void test_findById_success() {

        Category category = new Category(0, "Test Category", testUser);
        categoryRepository.save(category);

        Optional<Category> foundCategory = categoryRepository.findById(1);
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Test Category");
        assertThat(foundCategory.get().getUser()).isEqualTo(testUser);


    }

    @Test
    void test_save() {
        Category category = new Category(0, "Test Category", testUser);
        categoryRepository.save(category);

        Optional<Category> foundCategory = categoryRepository.findById(1);
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getId()).isEqualTo(1);
        assertThat(foundCategory.get().getName()).isEqualTo("Test Category");
        assertThat(foundCategory.get().getUser()).isEqualTo(testUser);
    }

    @Test
    void test_delete() {
        Category category = new Category(0, "Test Category", testUser);
        categoryRepository.save(category);
        Optional<Category> foundCategory = categoryRepository.findById(1);
        assertThat(foundCategory).isPresent();
        categoryRepository.delete(foundCategory.get());
        Optional<Category> deletedCategory = categoryRepository.findById(1);
        assertThat(deletedCategory).isNotPresent();
    }

    @Test
    void test_findAllByUser() {
        Category category = new Category(0, "Test Category", testUser);
        categoryRepository.save(category);
        List<Category> foundCategories = categoryRepository.findAllByUser(testUser);
        assertThat(foundCategories).hasSize(1);
        assertThat(foundCategories.get(0).getName()).isEqualTo("Test Category");

    }
}