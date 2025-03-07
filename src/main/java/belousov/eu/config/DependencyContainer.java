package belousov.eu.config;

import belousov.eu.controller.AuthController;
import belousov.eu.controller.ProfileController;
import belousov.eu.repository.UserRepository;
import belousov.eu.service.AuthService;
import belousov.eu.service.ProfileService;
import belousov.eu.service.UserService;
import belousov.eu.view.ConsoleView;

import java.util.HashMap;
import java.util.Map;

public class DependencyContainer {

    private final Map<Class<?>, Object> dependencies = new HashMap<>();

    public DependencyContainer() {
        register(UserRepository.class, new UserRepository());
        register(ConsoleView.class, new ConsoleView());
        register(AuthService.class, new UserService(this.get(UserRepository.class)));
        register(ProfileService.class, new UserService(this.get(UserRepository.class)));
        register(AuthController.class, new AuthController(this.get(AuthService.class), this.get(ConsoleView.class)));
        register(ProfileController.class, new ProfileController(this.get(ProfileService.class), this.get(ConsoleView.class)));

    }

    public <T> void register(Class<T> clazz, T dependency) {
        dependencies.put(clazz, dependency);
    }

    public <T> T get(Class<T> clazz) {
        return clazz.cast(dependencies.get(clazz));
    }

}
