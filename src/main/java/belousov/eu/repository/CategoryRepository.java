package belousov.eu.repository;

import belousov.eu.model.Category;
import belousov.eu.model.User;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления категориями транзакций.
 * Обеспечивает хранение, добавление, удаление и поиск категорий.
 */
@RequiredArgsConstructor
public class CategoryRepository {

    private final SessionFactory sessionFactory;


    /**
     * Находит категорию по ID.
     *
     * @param id ID категории
     * @return Optional с категорией, если найдена, иначе пустой Optional
     */
    public Optional<Category> findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Category category = session.get(Category.class, id);
            return Optional.ofNullable(category);
        }

    }

    /**
     * Сохраняет категорию в репозитории. Если категория новая (ID = 0), генерирует для неё ID.
     *
     * @param category категория для сохранения
     */
    public void save(Category category) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(category);
            session.getTransaction().commit();
        }
    }


    /**
     * Удаляет категорию из репозитория.
     *
     * @param category категория для удаления
     */
    public void delete(Category category) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(category);
            session.getTransaction().commit();
        }
    }

    /**
     * Возвращает список всех категорий для указанного пользователя.
     *
     * @param currentUser пользователь, чьи категории нужно найти
     * @return список категорий пользователя
     */
    public List<Category> findAllByUser(User currentUser) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Category WHERE user = :currentUser", Category.class)
                    .setParameter("currentUser", currentUser)
                    .getResultList();
        }
    }
}
