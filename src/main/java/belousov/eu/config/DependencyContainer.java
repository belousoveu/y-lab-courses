package belousov.eu.config;

import belousov.eu.controller.*;
import belousov.eu.repository.CategoryRepository;
import belousov.eu.repository.GoalRepository;
import belousov.eu.repository.UserRepository;
import belousov.eu.service.*;
import belousov.eu.view.ConsoleView;

import java.util.HashMap;
import java.util.Map;

public class DependencyContainer {

    private final Map<Class<?>, Object> dependencies = new HashMap<>();

    public DependencyContainer() {

        register(UserRepository.class, new UserRepository());
        register(GoalRepository.class, new GoalRepository());
        register(CategoryRepository.class, new CategoryRepository());

        register(ConsoleView.class, new ConsoleView());
        register(CategoryService.class, new CategoryServiceImp(this.get(CategoryRepository.class)));
        register(GoalService.class, new GoalServiceImp(this.get(GoalRepository.class)));
        register(UserService.class, new UserService(this.get(UserRepository.class)));
        register(AdminService.class, new AdminServiceImp(this.get(AdminAccess.class)));

        register(AuthService.class, this.get(UserService.class));
        register(ProfileService.class, this.get(UserService.class));
        register(AdminAccess.class, this.get(UserService.class));

        register(CategoryController.class, new CategoryController(this.get(CategoryService.class), this.get(ConsoleView.class)));
        register(AuthController.class, new AuthController(this.get(AuthService.class), this.get(ConsoleView.class)));
        register(ProfileController.class, new ProfileController(this.get(ProfileService.class), this.get(ConsoleView.class)));
        register(AdminController.class, new AdminController(this.get(AdminService.class), this.get(ConsoleView.class)));
        register(GoalController.class, new GoalController(this.get(GoalService.class), this.get(ConsoleView.class)));

    }

    public <T> void register(Class<T> clazz, T dependency) {
        dependencies.put(clazz, dependency);
    }

    public <T> T get(Class<T> clazz) {
        return clazz.cast(dependencies.get(clazz));
    }

}
