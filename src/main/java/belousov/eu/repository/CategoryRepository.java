package belousov.eu.repository;

import belousov.eu.model.entity.Category;
import belousov.eu.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Optional<Category> findById(int id);

    Category save(Category category);

    void delete(Category category);

    List<Category> findAllByUser(User currentUser);

    Optional<Category> findByNameAndUser(String categoryName, User user);
}
