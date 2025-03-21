package belousov.eu.service;

import belousov.eu.PersonalMoneyTracker;
import belousov.eu.exception.CategoryNotFoundException;
import belousov.eu.model.Category;
import belousov.eu.model.Role;
import belousov.eu.model.User;
import belousov.eu.repository.CategoryRepository;
import belousov.eu.service.imp.CategoryServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для {@link CategoryServiceImp}.
 */
class CategoryServiceImpTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImp categoryServiceImp;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1, "John Doe", "john@example.com", "password123", Role.USER, true);
        category = new Category(1, "Продукты", user);
        PersonalMoneyTracker.setCurrentUser(user); // Устанавливаем текущего пользователя
    }

    @Test
    void test_addCategory_shouldSaveCategory() {
        categoryServiceImp.addCategory("Продукты");

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void test_deleteCategory_whenCategoryExistsAndBelongsToUser_shouldDeleteCategory() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        categoryServiceImp.deleteCategory(1);

        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void test_deleteCategory_whenCategoryDoesNotExist_shouldThrowException() {
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryServiceImp.deleteCategory(1))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Не найдена категория с идентификатором 1");
    }

    @Test
    void test_deleteCategory_whenCategoryDoesNotBelongToUser_shouldThrowException() {
        User otherUser = new User(2, "Jane Doe", "jane@example.com", "password456", Role.USER, true);
        Category otherCategory = new Category(1, "Продукты", otherUser);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(otherCategory));

        assertThatThrownBy(() -> categoryServiceImp.deleteCategory(1))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Не найдена категория с идентификатором 1");
    }

    @Test
    void test_editCategory_whenCategoryExistsAndBelongsToUser_shouldUpdateCategory() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        categoryServiceImp.editCategory(1, "Транспорт");

        assertThat(category.getName()).isEqualTo("Транспорт");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void test_editCategory_whenCategoryDoesNotExist_shouldThrowException() {
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryServiceImp.editCategory(1, "Транспорт"))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Не найдена категория с идентификатором 1");
    }

    @Test
    void test_editCategory_whenCategoryDoesNotBelongToUser_shouldThrowException() {
        User otherUser = new User(2, "Jane Doe", "jane@example.com", "password456", Role.USER, true);
        Category otherCategory = new Category(1, "Продукты", otherUser);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(otherCategory));

        assertThatThrownBy(() -> categoryServiceImp.editCategory(1, "Транспорт"))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Не найдена категория с идентификатором 1");
    }

    @Test
    void test_getAllCategories_shouldReturnCategoriesForUser() {
        when(categoryRepository.findAllByUser(user)).thenReturn(List.of(category));

        List<Category> categories = categoryServiceImp.getAllCategories();
        assertThat(categories).containsExactly(category);
    }
}