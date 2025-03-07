package belousov.eu.config;

import belousov.eu.controller.AuthController;
import belousov.eu.repository.UserRepository;
import belousov.eu.service.AuthService;

import java.util.HashMap;
import java.util.Map;

public class DependencyContainer {

    private final Map<Class<?>, Object> dependencies = new HashMap<>();

    public DependencyContainer() {
        register(AuthController.class, new AuthController());
        register(AuthService.class, new AuthService());
        register(UserRepository.class, new UserRepository());

    }

    public <T> void register(Class<T> clazz, T dependency) {
        dependencies.put(clazz, dependency);
    }

    public <T> T get(Class<T> clazz) {
        return clazz.cast(dependencies.get(clazz));
    }

}
