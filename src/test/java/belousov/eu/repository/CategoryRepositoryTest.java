package belousov.eu.repository;

import belousov.eu.model.Category;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryRepositoryTest {


    private CategoryRepository categoryRepository;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        categoryRepository = new CategoryRepository();
        user = new User(1, "Иван Иванов", "ivan@example.com", "password123", Role.USER, true);
        category = new Category(1, "Продукты", user);
    }

    @Test
    void test_findById_whenCategoryExists() {
        categoryRepository.save(category);

        Optional<Category> foundCategory = categoryRepository.findById(1);

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getId()).isEqualTo(1);
        assertThat(foundCategory.get().getName()).isEqualTo("Продукты");
    }

    @Test
    void test_findById_whenCategoryDoesNotExist() {
        Optional<Category> foundCategory = categoryRepository.findById(999);

        assertThat(foundCategory).isNotPresent();
    }

    @Test
    void test_save_whenCategoryIsNew() {
        Category newCategory = new Category(0, "Транспорт", user);

        categoryRepository.save(newCategory);

        Optional<Category> savedCategory = categoryRepository.findById(newCategory.getId());
        assertThat(savedCategory).isPresent();
        assertThat(savedCategory.get().getName()).isEqualTo("Транспорт");
    }

    @Test
    void test_save_whenCategoryExists() {
        categoryRepository.save(category);
        category.setName("Развлечения");

        categoryRepository.save(category);

        Optional<Category> updatedCategory = categoryRepository.findById(1);
        assertThat(updatedCategory).isPresent();
        assertThat(updatedCategory.get().getName()).isEqualTo("Развлечения");
    }

    @Test
    void test_delete_whenCategoryExists() {
        categoryRepository.save(category);

        categoryRepository.delete(category);

        Optional<Category> deletedCategory = categoryRepository.findById(1);
        assertThat(deletedCategory).isNotPresent();
    }

    @Test
    void test_delete_whenCategoryDoesNotExist() {
        Category nonExistentCategory = new Category(999, "Несуществующая категория", user);

        categoryRepository.delete(nonExistentCategory);

        Optional<Category> deletedCategory = categoryRepository.findById(999);
        assertThat(deletedCategory).isNotPresent();
    }

    @Test
    void test_findAllByUser_whenUserHasCategories() {
        categoryRepository.save(category);
        Category anotherCategory = new Category(2, "Транспорт", user);
        categoryRepository.save(anotherCategory);

        List<Category> userCategories = categoryRepository.findAllByUser(user);

        assertThat(userCategories).hasSize(2);
        assertThat(userCategories).extracting(Category::getName).containsExactlyInAnyOrder("Продукты", "Транспорт");
    }

    @Test
    void test_findAllByUser_whenUserHasNoCategories() {
        User anotherUser = new User(2, "Петр Петров", "petr@example.com", "password456", Role.USER, true);

        List<Category> userCategories = categoryRepository.findAllByUser(anotherUser);

        assertThat(userCategories).isEmpty();
    }
}