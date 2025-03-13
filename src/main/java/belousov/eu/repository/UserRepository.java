package belousov.eu.repository;

import belousov.eu.exception.EmailAlreadyExistsException;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.utils.IdGenerator;
import belousov.eu.utils.Password;

import java.util.*;

/**
 * Репозиторий для управления пользователями.
 * Обеспечивает хранение, добавление, удаление и поиск пользователей.
 */
public class UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private final Set<Integer> admins = new HashSet<>();
    private final Set<String> emails = new HashSet<>();
    private final IdGenerator<Integer> idCounter = IdGenerator.create(Integer.class);

    /**
     * Конструктор по умолчанию. Инициализирует репозиторий начальными данными.
     */
    public UserRepository() {
        init();
    }

    /**
     * Инициализация репозитория. Добавляет администратора по умолчанию.
     */
    private void init() {
        String encodedPassword = Password.encode("Admin123");
        save(new User(idCounter.nextId(), "admin", "admin@admin.com", encodedPassword, Role.ADMIN, true));
    }

    /**
     * Сохраняет пользователя в репозитории. Если пользователь новый (ID = 0), генерирует для него ID.
     *
     * @param user пользователь для сохранения
     * @return сохранённый пользователь
     * @throws EmailAlreadyExistsException если пользователь с таким email уже существует
     */
    public User save(User user) {
        if (user.getId() == 0 && emails.contains(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        if (user.getId() == 0) {
            user.setId(idCounter.nextId());
        }

        users.put(user.getId(), user);

        if (user.getRole() == Role.ADMIN) {
            admins.add(user.getId());
        }
        emails.add(user.getEmail());
        return user;
    }

    /**
     * Удаляет пользователя из репозитория.
     *
     * @param user пользователь для удаления
     */
    public void delete(User user) {
        users.remove(user.getId());
        admins.remove(user.getId());
        emails.remove(user.getEmail());
    }

    /**
     * Находит пользователя по его ID.
     *
     * @param id ID пользователя
     * @return найденный пользователь или Optional.empty(), если пользователь не найден
     */
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Находит пользователя по его email.
     *
     * @param email email пользователя
     * @return найденный пользователь или Optional.empty(), если пользователь не найден
     */
    public Optional<User> findByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }

    /**
     * Удаляет email из хранилища email. Используется при обновлении email пользователя.
     *
     * @param email email для удаления
     */
    public void removeOldEmail(String email) {
        emails.remove(email);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей
     */
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Возвращает список всех ID администраторов.
     *
     * @return список всех ID администраторов
     */
    public List<Integer> getAllAdminIds() {
        return new ArrayList<>(admins);
    }

}
