package belousov.eu.service;

public interface AuthService {

    void register(String username, String email, String password);

    void login(String email, String password);
}
