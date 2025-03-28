package belousov.eu.service;

import belousov.eu.exception.CategoryNotFoundException;
import belousov.eu.mapper.CategoryMapper;
import belousov.eu.model.dto.CategoryDto;
import belousov.eu.model.entity.Category;
import belousov.eu.model.entity.Role;
import belousov.eu.model.entity.User;
import belousov.eu.repository.imp.CategoryRepositoryImp;
import belousov.eu.service.imp.CategoryServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
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
    private CategoryRepositoryImp categoryRepository;

    @InjectMocks
    private CategoryServiceImp categoryServiceImp;

    private final CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    private User user;
    private Category category;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1, "John Doe", "john@example.com", "password123", Role.USER, true);
        category = new Category(1, "Продукты", user);
    }

    @Test
    void test_addCategory_shouldSaveCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setName("Продукты");
        categoryServiceImp.addCategory(user, dto);

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void test_deleteCategory_whenCategoryExistsAndBelongsToUser_shouldDeleteCategory() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        categoryServiceImp.deleteCategory(1, user);

        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void test_deleteCategory_whenCategoryDoesNotExist_shouldThrowException() {
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryServiceImp.deleteCategory(1, user))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Не найдена категория с идентификатором 1");
    }

    @Test
    void test_deleteCategory_whenCategoryDoesNotBelongToUser_shouldThrowException() {
        User otherUser = new User(2, "Jane Doe", "jane@example.com", "password456", Role.USER, true);
        Category otherCategory = new Category(1, "Продукты", otherUser);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(otherCategory));

        assertThatThrownBy(() -> categoryServiceImp.deleteCategory(1, user))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Не найдена категория с идентификатором 1");
    }

    @Test
    void test_editCategory_whenCategoryExistsAndBelongsToUser_shouldUpdateCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setId(1);
        dto.setName("Транспорт");
        dto.setUser(user);
        Category updatedCategory = categoryMapper.toEntity(dto);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));


        categoryServiceImp.editCategory(1, user, dto);


        assertThat(updatedCategory.getName()).isEqualTo("Транспорт");
        verify(categoryRepository, times(1)).save(updatedCategory);
    }

    @Test
    void test_editCategory_whenCategoryDoesNotExist_shouldThrowException() {
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());
        CategoryDto dto = new CategoryDto();
        dto.setName("Транспорт");

        assertThatThrownBy(() -> categoryServiceImp.editCategory(1, user, dto))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Не найдена категория с идентификатором 1");
    }

    @Test
    void test_editCategory_whenCategoryDoesNotBelongToUser_shouldThrowException() {
        User otherUser = new User(2, "Jane Doe", "jane@example.com", "password456", Role.USER, true);
        Category otherCategory = new Category(1, "Продукты", otherUser);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(otherCategory));
        CategoryDto dto = new CategoryDto();
        dto.setName("Транспорт");

        assertThatThrownBy(() -> categoryServiceImp.editCategory(1, user, dto))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Не найдена категория с идентификатором 1");
    }

    @Test
    void test_getAllCategories_shouldReturnCategoriesForUser() {
        when(categoryRepository.findAllByUser(user)).thenReturn(List.of(category));
        CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);

        List<CategoryDto> categories = categoryServiceImp.getAllCategories(user);
        assertThat(categories).containsExactly(mapper.toDto(category));
    }
}