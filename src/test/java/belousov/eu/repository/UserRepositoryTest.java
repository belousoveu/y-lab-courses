//package belousov.eu.repository;
//
//import belousov.eu.exception.EmailAlreadyExistsException;
//import belousov.eu.model.Role;
//import belousov.eu.model.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
/// **
// * Тестовый класс для {@link UserRepository}.
// */
//class UserRepositoryTest {
//
//    private UserRepository userRepository;
//
//    @BeforeEach
//    void setUp() {
//        userRepository = new UserRepository();
//    }
//
//    @Test
//    void test_save_whenNewUser_shouldAddUserAndGenerateId() {
//        User user = new User(0, "John Doe", "john@example.com", "password123", Role.USER, true);
//        User savedUser = userRepository.save(user);
//
//        assertThat(savedUser.getId()).isNotZero();
//        assertThat(userRepository.findById(savedUser.getId())).contains(savedUser);
//    }
//
//    @Test
//    void test_save_whenEmailAlreadyExists_shouldThrowException() {
//        User user1 = new User(0, "John Doe", "john@example.com", "password123", Role.USER, true);
//        userRepository.save(user1);
//
//        User user2 = new User(0, "Jane Doe", "john@example.com", "password456", Role.USER, true);
//        assertThatThrownBy(() -> userRepository.save(user2))
//                .isInstanceOf(EmailAlreadyExistsException.class)
//                .hasMessage("Пользователь с email john@example.com уже существует");
//    }
//
//    @Test
//    void test_save_whenUserIsAdmin_shouldAddToAdminsSet() {
//        User admin = new User(0, "Admin", "admin@example.com", "admin123", Role.ADMIN, true);
//        userRepository.save(admin);
//
//        assertThat(userRepository.getAllAdminIds()).contains(admin.getId());
//    }
//
//    @Test
//    void test_delete_shouldRemoveUser() {
//        User user = new User(0, "John Doe", "john@example.com", "password123", Role.USER, true);
//        userRepository.save(user);
//
//        userRepository.delete(user);
//        assertThat(userRepository.findById(user.getId())).isEmpty();
//    }
//
//    @Test
//    void test_findById_whenUserExists_shouldReturnUser() {
//        User user = new User(0, "John Doe", "john@example.com", "password123", Role.USER, true);
//        userRepository.save(user);
//
//        Optional<User> foundUser = userRepository.findById(user.getId());
//        assertThat(foundUser).contains(user);
//    }
//
//    @Test
//    void test_findById_whenUserDoesNotExist_shouldReturnEmpty() {
//        Optional<User> foundUser = userRepository.findById(999);
//        assertThat(foundUser).isEmpty();
//    }
//
//    @Test
//    void test_findByEmail_whenUserExists_shouldReturnUser() {
//        User user = new User(0, "John Doe", "john@example.com", "password123", Role.USER, true);
//        userRepository.save(user);
//
//        Optional<User> foundUser = userRepository.findByEmail("john@example.com");
//        assertThat(foundUser).contains(user);
//    }
//
//    @Test
//    void test_findByEmail_whenUserDoesNotExist_shouldReturnEmpty() {
//        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
//        assertThat(foundUser).isEmpty();
//    }
//
//
//    @Test
//    void test_findAll_shouldReturnAllUsers() {
//        User admin = userRepository.findById(1).get();
//        User user1 = new User(0, "John Doe", "john@example.com", "password123", Role.USER, true);
//        User user2 = new User(0, "Jane Doe", "jane@example.com", "password456", Role.USER, true);
//        userRepository.save(user1);
//        userRepository.save(user2);
//
//        List<User> users = userRepository.findAll();
//        assertThat(users).containsExactlyInAnyOrder(user1, user2, admin);
//    }
//
//    @Test
//    void test_getAllAdminIds_shouldReturnAdminIds() {
//        User admin = userRepository.findById(1).get();
//        User admin1 = new User(0, "Admin1", "admin1@example.com", "admin123", Role.ADMIN, true);
//        User admin2 = new User(0, "Admin2", "admin2@example.com", "admin456", Role.ADMIN, true);
//        userRepository.save(admin1);
//        userRepository.save(admin2);
//
//        List<Integer> adminIds = userRepository.getAllAdminIds();
//        assertThat(adminIds).containsExactlyInAnyOrder(admin1.getId(), admin2.getId(), admin.getId());
//    }
//}