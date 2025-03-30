package belousov.eu.controller;

import belousov.eu.model.dto.CategoryDto;
import belousov.eu.model.entity.User;
import belousov.eu.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private static final String CURRENT_USER = "currentUser";

    private final CategoryService categoryService;

    @PostMapping
    public void addCategory(@RequestBody CategoryDto categoryDto, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        categoryService.addCategory(user, categoryDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        categoryService.deleteCategory(id, user);
    }

    @PutMapping("/{id}")
    public void editCategory(@PathVariable int id, @RequestBody CategoryDto categoryDto, HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        categoryService.editCategory(id, user, categoryDto);
    }

    @GetMapping
    public List<CategoryDto> getCategories(HttpSession session) {
        User user = (User) session.getAttribute(CURRENT_USER);
        return categoryService.getAllCategories(user);
    }
}
