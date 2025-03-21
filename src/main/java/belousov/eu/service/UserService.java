package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.*;
import belousov.eu.mapper.UserProfileMapper;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.model.dto.LoginDto;
import belousov.eu.model.dto.RegisterDto;
import belousov.eu.model.dto.UserProfileDto;
import belousov.eu.model.dto.UserProfileUpdateDto;
import belousov.eu.repository.UserRepository;
import belousov.eu.utils.Password;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.mapstruct.factory.Mappers;

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
    private final UserProfileMapper userProfileMapper = Mappers.getMapper(UserProfileMapper.class);

    /**
     * Регистрирует нового пользователя.
     *
     * @param registerDto данные для регистрации (имя, электронная почта, пароль)
     */
    @Override
    public User register(RegisterDto registerDto) {
        try {
            User user = userRepository.save(new User(
                    0,
                    registerDto.name(),
                    registerDto.email().toLowerCase(),
                    Password.encode(registerDto.password()),
                    Role.USER,
                    true
            ));
            PersonalMoneyTracker.setCurrentUser(user);
            return user;
        } catch (ConstraintViolationException e) {
            throw new EmailAlreadyExistsException(registerDto.email().toLowerCase());
        }

    }

    /**
     * Авторизует пользователя.
     *
     * @param loginDto данные для входа (электронная почта и пароль)
     * @throws UserNotFoundException    если пользователь не найден
     * @throws UserWasBlockedException  если пользователь заблокирован
     * @throws InvalidPasswordException если пароль неверный
     */
    @Override
    public User login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.email().toLowerCase())
                .orElseThrow(() -> new UserNotFoundException(loginDto.email().toLowerCase()));
        if (!user.isActive()) {
            throw new UserWasBlockedException(user.getName());
        }
        checkPassword(user, loginDto.password());
        PersonalMoneyTracker.setCurrentUser(user);
        return user;
    }


    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return пользователь с указанным идентификатором
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public UserProfileDto getUserById(int id) {
        return userProfileMapper.toDto(findById(id));
    }

    /** Изменяет поля профиля пользователя.
     *
     * @param id идентификатор пользователя
     * @param updateDto - объект с данными для изменения профиля (имя, электронная почта, пароль).
     *                  При изменении пароля требуется указать старый пароль.
     * @throws InvalidPasswordException если пароль неверный
     */
    @Override
    public void updateUser(int id, UserProfileUpdateDto updateDto) {
        User user = findById(id);
        if (updateDto.password() != null) {
            checkPassword(user, updateDto.oldPassword());
            user.setPassword(Password.encode(updateDto.password()));
        }
        user.setName(updateDto.name() != null ? updateDto.name() : user.getName());
        user.setEmail(updateDto.email() != null ? updateDto.email().toLowerCase() : user.getEmail());
        userRepository.save(user);
    }

    /**
     * Удаляет пользователя.
     *
     * @param id          идентификатор пользователя
     * @param password    пароль (требуется, если пользователь удаляет себя)
     * @param currentUser текущий авторизованный пользователь
     * @throws ForbiddenException       если попытка удалить другого пользователя без прав администратора
     * @throws LastAdminDeleteException если попытка удалить последнего администратора
     * @throws InvalidPasswordException если пароль неверный
     */
    @Override
    public void deleteUser(int id, String password, User currentUser) {
        User user = findById(id);
        if (currentUser.isAdmin()) {
            checkIfLastAdmin(user);
            userRepository.delete(user);
        } else if (currentUser.equals(user)) {
            checkPassword(currentUser, password);
            userRepository.delete(user);
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
        deleteUser(id, "", PersonalMoneyTracker.getCurrentUser());
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
