package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.*;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.repository.UserRepository;
import belousov.eu.utils.Password;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

/**
 * Реализация сервиса для управления пользователями.
 * Обеспечивает регистрацию, авторизацию, управление профилем и административные функции.
 */
@AllArgsConstructor
public class UserService implements AuthService, ProfileService, AdminAccessUserService {
    /**
     * Репозиторий для работы с пользователями.
     */
    private final UserRepository userRepository;

    /**
     * Регистрирует нового пользователя.
     *
     * @param name     имя пользователя
     * @param email    электронная почта
     * @param password пароль
     */
    @Override
    public void register(String name, String email, String password) {
        try {
            User user = userRepository.save(new User(0, name, email.toLowerCase(), Password.encode(password), Role.USER, true));
            PersonalMoneyTracker.setCurrentUser(user);
        } catch (ConstraintViolationException e) {
            throw new EmailAlreadyExistsException(email.toLowerCase());
        }

    }

    /**
     * Авторизует пользователя.
     *
     * @param email    электронная почта
     * @param password пароль
     * @throws UserNotFoundException    если пользователь не найден
     * @throws UserWasBlockedException  если пользователь заблокирован
     * @throws InvalidPasswordException если пароль неверный
     */
    @Override
    public void login(String email, String password) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UserNotFoundException(email.toLowerCase()));
        if (!user.isActive()) {
            throw new UserWasBlockedException(user.getName());
        }
        checkPassword(user, password);
        PersonalMoneyTracker.setCurrentUser(user);
    }

    /**
     * Изменяет имя пользователя.
     *
     * @param user пользователь
     * @param name новое имя
     */
    @Override
    public void changeName(User user, String name) {
        user.setName(name);
        userRepository.save(user);
    }

    /**
     * Изменяет пароль пользователя.
     *
     * @param user        пользователь
     * @param oldPassword старый пароль
     * @param newPassword новый пароль
     * @throws InvalidPasswordException если старый пароль неверный
     */
    public void changePassword(User user, String oldPassword, String newPassword) {
        checkPassword(user, oldPassword);
        user.setPassword(Password.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Изменяет электронную почту пользователя.
     *
     * @param user  пользователь
     * @param email новая электронная почта
     */
    @Override
    public void changeEmail(User user, String email) {
        user.setEmail(email.toLowerCase());
        userRepository.save(user);
    }

    /**
     * Удаляет пользователя.
     *
     * @param user     пользователь
     * @param password пароль (требуется, если пользователь удаляет себя)
     * @throws ForbiddenException       если попытка удалить другого пользователя без прав администратора
     * @throws LastAdminDeleteException если попытка удалить последнего администратора
     * @throws InvalidPasswordException если пароль неверный
     */
    @Override
    public void deleteUser(User user, String password) {
        User currentUser = PersonalMoneyTracker.getCurrentUser();
        if (currentUser.isAdmin()) {
            checkIfLastAdmin(user);
            userRepository.delete(user); //TODO Cascade delete all transactions
        } else if (currentUser.equals(user)) {
            checkPassword(currentUser, password);
            userRepository.delete(user); //TODO Cascade delete all transactions
            PersonalMoneyTracker.setCurrentUser(null);
        } else {
            throw new ForbiddenException();
        }
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id ID пользователя
     */
    public void deleteUserById(int id) {
        User user = findById(id);
        deleteUser(user, "");
    }

    /**
     * Блокирует пользователя по ID.
     *
     * @param id ID пользователя
     * @throws LastAdminDeleteException если попытка заблокировать последнего администратора
     */
    public void blockUser(int id) {
        User user = findById(id);
        checkIfLastAdmin(user);
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Разблокирует пользователя по ID.
     *
     * @param id ID пользователя
     */
    public void unblockUser(int id) {
        User user = findById(id);
        user.setActive(true);
        userRepository.save(user);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Устанавливает роль пользователю по ID.
     *
     * @param userId ID пользователя
     * @param role   новая роль
     * @throws LastAdminDeleteException если попытка изменить роль последнего администратора
     */
    public void setRole(int userId, Role role) {
        User user = findById(userId);
        if (role == Role.ADMIN) {
            user.setRole(Role.ADMIN);
            userRepository.save(user);
        } else {
            checkIfLastAdmin(user);
            user.setRole(Role.USER);
            userRepository.save(user);
        }
        if (PersonalMoneyTracker.getCurrentUser().getId() == userId) {
            PersonalMoneyTracker.setCurrentUser(user);
        }
    }

    /**
     * Находит пользователя по ID.
     *
     * @param id ID пользователя
     * @return пользователь
     * @throws UserNotFoundException если пользователь не найден
     */
    private User findById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Проверяет, совпадает ли пароль с паролем пользователя.
     *
     * @param user     пользователь
     * @param password пароль для проверки
     * @throws InvalidPasswordException если пароль неверный
     */
    private void checkPassword(User user, String password) {
        if (!Password.verify(password, user.getPassword())) {
            throw new InvalidPasswordException();
        }
    }

    /**
     * Проверяет, является ли пользователь последним администратором.
     *
     * @param user пользователь для проверки
     * @throws LastAdminDeleteException если пользователь — последний администратор
     */
    private void checkIfLastAdmin(User user) {
        List<Integer> adminIds = userRepository.getAllAdminIds();
        if (adminIds.contains(user.getId()) && adminIds.size() == 1) {
            throw new LastAdminDeleteException();
        }
    }
}
