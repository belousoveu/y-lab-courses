package belousov.eu.repository;

import belousov.eu.model.Category;
import belousov.eu.model.User;
import belousov.eu.utils.IdGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CategoryRepository {

    private final Map<Integer, Category> categories = new HashMap<>();
    private final IdGenerator<Integer> idCounter = IdGenerator.create(Integer.class);

    public Optional<Category> findById(int id) {
        return Optional.ofNullable(categories.get(id));
    }


    public void save(Category category) {
        if (category.getId() == 0) {
            category.setId(idCounter.nextId());
        }
        categories.put(category.getId(), category);
    }

    public void delete(Category category) {
        categories.remove(category.getId());
    }


    public List<Category> findAllByUser(User currentUser) {
        return categories.values().stream().filter(category -> category.getUser().equals(currentUser)).toList();
    }
}
