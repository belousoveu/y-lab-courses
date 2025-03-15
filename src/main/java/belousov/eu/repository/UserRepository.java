package belousov.eu.repository;

import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.utils.Password;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления пользователями.
 * Обеспечивает хранение, добавление, удаление и поиск пользователей.
 */
public class UserRepository {

    private final SessionFactory sessionFactory;

    /**
     * Конструктор по умолчанию. При отсутствии в базе данных администратора, добавляет администратора по умолчанию
     */
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        init();
    }

    /**
     * Инициализация репозитория. Добавляет администратора по умолчанию.
     */
    private void init() {
        if (getAllAdminIds().isEmpty()) {
            String encodedPassword = Password.encode("Admin123"); //TODO: Данные администратора по умолчанию взять из конфигурации
            save(new User(0, "admin", "admin@admin.com", encodedPassword, Role.ADMIN, true));
        }
    }

    /**
     * Сохраняет или обновляет пользователя в базе данных.
     *
     * @param user - пользователь для сохранения или обновления
     * @return - сохраненный или обновленный пользователь
     */
    public User save(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        User savedUser = session.merge(user);
        session.getTransaction().commit();
        session.close();

        return savedUser;
    }

    /**
     * Удаляет пользователя из репозитория.
     *
     * @param user пользователь для удаления
     */
    public void delete(User user) {
        sessionFactory.inTransaction(session -> session.remove(user));
    }

    /**
     * Находит пользователя по его ID.
     *
     * @param id ID пользователя
     * @return найденный пользователь или Optional.empty(), если пользователь не найден
     */
    public Optional<User> findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.find(User.class, id);
            return Optional.ofNullable(user);
        }
    }

    /**
     * Находит пользователя по его email.
     *
     * @param email email пользователя
     * @return найденный пользователь или Optional.empty(), если пользователь не найден
     */
    public Optional<User> findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return Optional.ofNullable(user);
        }
    }


    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей
     */
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT u FROM User u", User.class).getResultList();
        }
    }

    /**
     * Возвращает список всех ID администраторов.
     *
     * @return список всех ID администраторов
     */
    public List<Integer> getAllAdminIds() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT id FROM User WHERE role = 'ADMIN'", Integer.class).getResultList();
        }
    }

}
